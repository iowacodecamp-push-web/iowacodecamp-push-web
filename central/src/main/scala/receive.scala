package code.central

import code.protocol._
import org.zeromq.ZMQ
import akka.actor.{Actor, ActorRef}

case object ReceiveMessage

class ZMQSocketMessageReceiver(toForward: ActorRef, port: Int) extends Actor with ZMQContext {
  import ProtocolDeserialization._
  import ZMQMultipart._
  
  lazy val pullSocket = {
    val pullSocket = context.socket(ZMQ.PULL)
    pullSocket.bind("tcp://*:" + port)
    pullSocket
  }

  def receive = {
    case ReceiveMessage =>
      val message = ((blockingReadTwoPartMessage _) andThen (deserializeMessage _))(pullSocket)
      toForward ! message
      self ! ReceiveMessage
  }
}

class CentralReceiver(centralPublisher: ActorRef) extends Actor {
  def receive = {
    case msg @ UserAt(username, location) =>
      log.info("User " + username + " is at: " + location)
      centralPublisher forward msg
    case msg @ UserGone(who) =>
      log.info(who + " has left")
      centralPublisher forward msg
  }
}

