package dev.sharek.examples

import akka.http.scaladsl.server.Directive0
import akka.http.scaladsl.server.Directives.{headerValueByName, pass, reject}

object AuthenticationProvider {

  val tokenDirective: Directive0 =
    headerValueByName("token").flatMap { token =>
      println(s"Token received: $token")
      if (token == "XYZ") pass
      else reject
    }

}
