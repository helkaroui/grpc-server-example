package dev.sharek.examples

import akka.actor.ActorSystem
import akka.grpc.scaladsl.{ServerReflection, ServiceHandler, WebHandler}
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.typesafe.config.ConfigFactory
import dev.sharek.examples.AuthenticationProvider.tokenDirective
import example.myapp.helloworld.grpc._

object Main {
  def main(args: Array[String]): Unit = {

    val conf = ConfigFactory
      .parseString("akka.http.server.preview.enable-http2 = on")
      .withFallback(ConfigFactory.defaultApplication())

    implicit val system = ActorSystem("HelloWorld", conf)
    implicit val ec = system.dispatcher

    // Create services
    val greeterService =
      GreeterServiceHandler.partial(new GreeterServiceImpl())
    val echoService =
      EchoServiceHandler.partial(new EchoServiceImpl())
    val reflectionService =
      ServerReflection.partial(List(GreeterService, EchoService))

    // Combine multiple services
    val serviceHandlers =
      ServiceHandler.concatOrNotFound(greeterService, echoService, reflectionService)

    val secureRoute = concat(
      tokenDirective {
        handle(serviceHandlers)
      }
    )

    // Bind service handler servers to localhost:8080/8081
    val bindingGrpc = Http().newServerAt("127.0.0.1", 8080).bind(secureRoute)

    // Expose a GRPC-Web endpoint
    val grpcWebServiceHandlers = WebHandler.grpcWebHandler(greeterService, echoService, reflectionService)

    // AuthRoute
    val authenticationRoute: Route = path("login") {
      get {
        complete("Psst, please use token XYZ!")
      }
    }

    // Secure routes
    val secureWebRoute = concat(
      authenticationRoute,
      tokenDirective {
        handle(grpcWebServiceHandlers)
      }
    )
    val bindingWebGrpc = Http().newServerAt("127.0.0.1", 8081).bind(secureWebRoute)

    // report successful binding
    bindingGrpc.foreach { binding => println(s"gRPC server bound to: ${binding.localAddress}") }
    bindingWebGrpc.foreach { binding => println(s"Web-gRPC server bound to: ${binding.localAddress}") }
  }
}
