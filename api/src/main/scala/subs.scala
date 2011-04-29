package code.api

import akka.actor._
import Actor._
import akka.routing.Listeners
import code.protocol._
import org.zeromq.ZMQ

object Subs {
  lazy val centralSub = actorOf(new CentralSub(5559)).start
}

case object ReceiveMessage

class CentralSub(port: Int) extends Actor with ZMQContext with Listeners {
  import ProtocolDeserialization._
  import ZMQMultipart._

  lazy val subSocket = {
    val subSocket = context.socket(ZMQ.SUB)
    subSocket.connect("tcp://*:" + port)
    subSocket.subscribe("".getBytes)
    subSocket
  }

  def receive = listenerManagement orElse {
    case ReceiveMessage =>
      val message = ((blockingReadTwoPartMessage _) andThen (deserializeMessage _))(subSocket)
      log.info("central sub received message " + message)
      gossip(message)
      self ! ReceiveMessage
  }
}

