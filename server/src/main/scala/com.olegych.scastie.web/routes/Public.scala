package com.olegych.scastie
package web
package routes

import TwirlSupport._
import akka.http.scaladsl.server.Directives._

import akka.NotUsed
import akka.stream.scaladsl.Source
import de.heikoseeberger.akkasse.ServerSentEvent
import de.heikoseeberger.akkasse.EventStreamMarshalling._

import java.time.LocalTime
import java.time.format.DateTimeFormatter
import scala.concurrent.duration.DurationInt


object Public {
  val routes =
    concat(
      get(
        path("beta")(
          complete(views.html.beta())
        )
      ),
      get(
        path("time-demo")(
          complete(views.html.timeDemo())
        )
      ),
      get(
        path("time")(
          complete(
            Source
              .tick(0.second, 1.seconds, NotUsed)
              .take(5)
              .map(_ => LocalTime.now())
              .map(timeToServerSentEvent)
          )
        )
      ),
      Assets.routes
    )


  private def timeToServerSentEvent(time: LocalTime) =
    ServerSentEvent(DateTimeFormatter.ISO_LOCAL_TIME.format(time))
}
