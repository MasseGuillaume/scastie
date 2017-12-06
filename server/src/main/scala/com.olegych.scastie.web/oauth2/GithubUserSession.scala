package com.olegych.scastie.web.oauth2

import com.olegych.scastie.api.User

import play.api.libs.json.Json

import com.softwaremill.session._
import com.typesafe.config.ConfigFactory

import scala.collection.parallel.mutable.ParTrieMap
import scala.concurrent.{ExecutionContext, Future}
import com.typesafe.scalalogging.Logger

import scala.util.Try
import java.util.UUID
import java.nio.file._

import scala.collection.JavaConverters._
import scala.collection.mutable

import System.{lineSeparator => nl}

class GithubUserSession()(implicit val executionContext: ExecutionContext) {

  val logger = Logger("GithubUserSession")

  private val configuration =
    ConfigFactory.load().getConfig("com.olegych.scastie.web")
  private val usersFile =
    Paths.get(configuration.getString("oauth2.users-file"))
  private val usersSessions =
    Paths.get(configuration.getString("oauth2.sessions-file"))

  private val sessionConfig =
    SessionConfig.default(configuration.getString("session-secret"))

  private lazy val users = {
    val trie = ParTrieMap[UUID, User]()
    readSessionsFile().map {
      case (uuid, user) =>
        val pair = uuid -> user
        trie += pair
    }
    trie
  }

  implicit def serializer: SessionSerializer[UUID, String] =
    new SingleValueSessionSerializer(
      _.toString(),
      (id: String) => Try { UUID.fromString(id) }
    )
  implicit val sessionManager = new SessionManager[UUID](sessionConfig)
  implicit val refreshTokenStorage = new InMemoryRefreshTokenStorage[UUID] {
    def log(msg: String): Unit =
      if (msg.startsWith("Looking up token for selector")) () // boring
      else logger.info(msg)
  }

  private def readSessionsFile(): Array[(UUID, User)] = {
    if (Files.exists(usersSessions)) {
      val content = Files.readAllLines(usersSessions).toArray.mkString(nl)
      Json
        .fromJson[Array[(UUID, User)]](Json.parse(content))
        .asOpt
        .getOrElse(Array())
    } else {
      Array()
    }
  }

  def appendSessionsFile(uuid: UUID, user: User): Unit = {
    val pair = uuid -> user
    users += pair
    val sessions = readSessionsFile()
    val sessions0 = sessions :+ pair

    if (Files.exists(usersSessions)) {
      Files.delete(usersSessions)
    }

    Files.write(
      usersSessions,
      Json.prettyPrint(Json.toJson(sessions0)).getBytes,
      StandardOpenOption.CREATE
    )

    ()
  }

  def addUser(user: User): UUID = {
    val uuid = UUID.randomUUID
    appendSessionsFile(uuid, user)
    storeUser(user.login)
    uuid
  }

  def storeUser(login: String): Unit = {
    val lines =
      if (Files.exists(usersFile)) Files.readAllLines(usersFile).asScala
      else Seq()

    if (!lines.contains(login)) {
      Files.write(usersFile,
                  (login + nl).getBytes,
                  StandardOpenOption.APPEND,
                  StandardOpenOption.CREATE)
      ()
    }
  }

  def getUser(id: Option[UUID]): Option[User] =
    id.flatMap(users.get)
}

trait InMemoryRefreshTokenStorage[T] extends RefreshTokenStorage[T] {
  case class Store(session: T, tokenHash: String, expires: Long)
  private val _store = mutable.Map[String, Store]()

  def store: Map[String, Store] = _store.toMap

  override def lookup(selector: String) = {
    Future.successful {
      val r = _store.get(selector).map(s => RefreshTokenLookupResult[T](s.tokenHash, s.expires,
        () => s.session))
      log(s"Looking up token for selector: $selector, found: ${r.isDefined}")
      r
    }
  }

  override def store(data: RefreshTokenData[T]) = {
    log(s"Storing token for selector: ${data.selector}, user: ${data.forSession}, " +
      s"expires: ${data.expires}, now: ${System.currentTimeMillis()}")
    Future.successful(_store.put(data.selector, Store(data.forSession, data.tokenHash, data.expires)))
  }

  override def remove(selector: String) = {
    log(s"Removing token for selector: $selector")
    Future.successful(_store.remove(selector))
  }

  override def schedule[S](after: Duration)(op: => Future[S]) = {
    log("Running scheduled operation immediately")
    op
    Future.successful(())
  }

  def log(msg: String): Unit
}

