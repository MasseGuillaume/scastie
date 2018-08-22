import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.testkit.{TestKit, TestProbe}

import com.typesafe.config.{ConfigFactory, Config}
import org.scalatest.{BeforeAndAfterAll, FunSuiteLike}
import scala.concurrent.duration._

import com.olegych.scastie.sbt.SbtActor
import com.olegych.scastie.balancer.{DispatchActor, RunSnippet, InputsWithIpAndUser, UserTrace}
import com.olegych.scastie.api.{SnippetId, Inputs, SnippetProgress}

object RemotePortConfig {
  def apply(port: Int): Config =
    ConfigFactory.parseString(
      s"""|akka {
          |  actor {
          |    provider = "akka.remote.RemoteActorRefProvider"
          |  }
          |  remote {
          |    netty.tcp {
          |      hostname = "127.0.0.1"
          |      port = $port
          |    }
          |  }
          |}""".stripMargin
    )
}


class IntegrationSuite() extends TestKit(ActorSystem("SbtActorTest")) with FunSuiteLike with BeforeAndAfterAll {

  val runnerCount = 2
  val portsStart = 6000

  val configDispatch = ConfigFactory.parseString(
    s"""|com.olegych.scastie {
        |  balancer {
        |    remote-sbt-ports-start = $portsStart
        |    remote-sbt-ports-size = $runnerCount
        |  }
        |  web {
        |    production = false
        |  }
        |  sbt {
        |    runTimeout = 30s
        |    sbtReloadTimeout = 40s
        |  }
        |}
        |""".stripMargin
  )
  val systemDispatch = ActorSystem("dispatch", configDispatch)

  val sbtPorts = (0 until runnerCount).map(portsStart + _)
  def createRunner(port: Int): (ActorSystem, ActorRef) = {
    val configRunners = ConfigFactory.parseString(
      s"""|com.olegych.scastie {
          |  sbt {
          |    hostname = "127.0.0.1"
          |    akka-port = $port
          |    reconnect = false
          |    production = false
          |  }
          |}
          |
          |akka {
          |  loggers = ["akka.event.slf4j.Slf4jLogger"]
          |  loglevel = "INFO"
          |  actor {
          |    provider = "akka.remote.RemoteActorRefProvider"
          |    warn-about-java-serializer-usage = false
          |  }
          |  remote {
          |    transport = "akka.remote.netty.NettyRemoteTransport"
          |    netty.tcp {
          |      hostname = "127.0.0.1"
          |      port = $port
          |      bind-hostname = "127.0.0.1"
          |      bind-port = $port
          |    }
          |  }
          |}""".stripMargin
    )
    val systemRunner = ActorSystem("runners", configRunners)

    val actor =
      systemRunner.actorOf(
        Props(
          new SbtActor(
            system = systemRunner,
            runTimeout = 30.seconds,
            sbtReloadTimeout = 40.seconds,
            isProduction = false,
            readyRef = None,
            reconnectInfo = None
          )
        ),
        name = "SbtActor"
      )

    (systemRunner, actor)
  }

  val runners = sbtPorts.map(createRunner)


  val progressActor = TestProbe()
  val statusActor = TestProbe()

  val dispatchActor =
    systemDispatch.actorOf(
      Props(new DispatchActor(progressActor.ref, statusActor.ref)),
      name = "DispatchActor"
    )

  override def afterAll: Unit = {
    systemDispatch.terminate()
    runners.foreach{ case (system, _) => system.terminate() }
    system.terminate()
  }

  test("fun") {
    val todo = 1 to 10

    todo.foreach{ x =>
      val actor = system.actorOf(Props(new Actor{
        override def receive = {
          case sid: SnippetId => println(s"got: $sid")
          case run: RunSnippet => dispatchActor ! run
        }
      }))
      actor ! RunSnippet(InputsWithIpAndUser(Inputs.default, UserTrace("ip" + x,  None)))
    }

    todo.foreach{x =>
      progressActor.fishForMessage(1.minute){
        case progress: SnippetProgress => progress.isDone
      }
    }

  }
}
