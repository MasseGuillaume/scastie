// package com.olegych.scastie.web.oauth2

// import scala.util.Try
// import java.util.UUID
// import java.nio.file.Paths

// import com.olegych.scastie.api.User

// import com.softwaremill.session._

// import com.typesafe.config.ConfigFactory

// import scala.concurrent.{ExecutionContext, Future}
// import scala.concurrent.duration.Duration

// class GithubUserSession2()(implicit val executionContext: ExecutionContext) {
//   private val configuration =
//     ConfigFactory.load().getConfig("com.olegych.scastie.web")
//   private val sessionConfig =
//     SessionConfig.default(configuration.getString("session-secret"))
//   private val usersFile =
//     Paths.get(configuration.getString("oauth2.users-file"))
//   private val usersSessions =
//     Paths.get(configuration.getString("oauth2.sessions-file"))

//   implicit def serializer: SessionSerializer[UUID, String] =
//     new SingleValueSessionSerializer(
//       _.toString(),
//       (id: String) => Try { UUID.fromString(id) }
//     )

//   implicit val refreshTokenStorage: RefreshTokenStorage[UUID] =
//     new FileRefreshTokenStorage()

//   implicit val sessionManager: SessionManager[UUID] =
//     new SessionManager(sessionConfig)

//   def getUser(id: Option[UUID]): Option[User] = ???
//   def addUser(user: User): UUID = ???
// }

// class FileRefreshTokenStorage[T] extends RefreshTokenStorage[T] {
//   def lookup(selector: String): Future[Option[RefreshTokenLookupResult[T]]] = ???
//   def store(data: RefreshTokenData[T]): Future[Unit] = ???
//   def remove(selector: String): Future[Unit] = ???
//   def schedule[S](after: Duration)(op: => Future[S]): Unit = ???
// }







// trait InMemoryRefreshTokenStorage[T] extends RefreshTokenStorage[T] {
//   case class Store(session: T, tokenHash: String, expires: Long)
//   private val _store = mutable.Map[String, Store]()

//   def store: Map[String, Store] = _store.toMap

//   override def lookup(selector: String) = {
//     Future.successful {
//       val r = _store.get(selector).map(s => RefreshTokenLookupResult[T](s.tokenHash, s.expires,
//         () => s.session))
//       log(s"Looking up token for selector: $selector, found: ${r.isDefined}")
//       r
//     }
//   }

//   override def store(data: RefreshTokenData[T]) = {
//     log(s"Storing token for selector: ${data.selector}, user: ${data.forSession}, " +
//       s"expires: ${data.expires}, now: ${System.currentTimeMillis()}")
//     Future.successful(_store.put(data.selector, Store(data.forSession, data.tokenHash, data.expires)))
//   }

//   override def remove(selector: String) = {
//     log(s"Removing token for selector: $selector")
//     Future.successful(_store.remove(selector))
//   }

//   override def schedule[S](after: Duration)(op: => Future[S]) = {
//     log("Running scheduled operation immediately")
//     op
//     Future.successful(())
//   }

//   def log(msg: String): Unit
// }
