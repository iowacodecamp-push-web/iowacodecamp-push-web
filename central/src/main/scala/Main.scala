package code.central

import code.protocol._
import org.zeromq.ZMQ
import akka.actor.Actor
import Actor._

case object StartMultiplexing

object CentralServer {
  def main(args: Array[String]) {
    val receiver = actorOf[CentralReceiver].start
    receiver ! StartMultiplexing
  }
}

trait ZMQContext {
  lazy val context = ZMQ.context(1)
}

class CentralReceiver extends Actor with ZMQContext {
  import ProtocolDeserialization._
  import ZMQMultipart._
  
  lazy val pullSocket = {
    val pullSocket = context.socket(ZMQ.PULL)
    pullSocket.bind("tcp://*:5558")
    pullSocket
  }

  def receive = {
    case StartMultiplexing =>
      val message = ((blockingReadTwoPartMessage _) andThen (deserializeMessage _))(pullSocket)
      self ! message
    case UserGone(who) =>
      println(who + " has left")
  }
}

object Push extends ZMQContext {
  import ProtocolSerialization._
  import ZMQMultipart._
  
  def main(args: Array[String]) {
    val pushSocket = context.socket(ZMQ.PUSH)
    pushSocket.connect("tcp://localhost:5558")

    writeTwoPartMessage(serializeToMessage(UserGone("Luke")), pushSocket)
  }
}
