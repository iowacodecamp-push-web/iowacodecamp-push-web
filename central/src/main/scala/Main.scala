package code.central

import org.zeromq.ZMQ
import akka.actor.Actor
import Actor._

object CentralServer {
  def main(args: Array[String]) {
    val receiver = actorOf[CentralReceiver].start
    receiver ! Start
  }
}

trait ZMQContext {
  lazy val context = ZMQ.context(1)
}

case object StartMultiplexing

class CentralReceiver extends Actor with ZMQContext {
  lazy val pullSocket = {
    val pullSocket = context.socket(ZMQ.PULL)
    pullSocket.bind("tcp://*:5558")
    pullSocket
  }
  
  def receive = {
    case StartMultiplexing =>
      val message = new String(pullSocket.recv(0))
      println("Hello " + message)
  }
}

object Push extends ZMQContext {
  def main(args: Array[String]) {
    val pushSocket = context.socket(ZMQ.PUSH)
    pushSocket.connect("tcp://localhost:5558")

    pushSocket send ("Luke".getBytes, 0)
  }
}
