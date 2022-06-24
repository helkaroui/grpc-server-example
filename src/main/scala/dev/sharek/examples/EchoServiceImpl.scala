package dev.sharek.examples

import akka.stream.Materializer
import example.myapp.helloworld.grpc.{EchoReply, EchoRequest, EchoService}

import scala.concurrent.Future

class EchoServiceImpl(implicit mat: Materializer) extends EchoService {
  override def echo(in: EchoRequest): Future[EchoReply] =
    Future.successful(EchoReply(in.value))
}
