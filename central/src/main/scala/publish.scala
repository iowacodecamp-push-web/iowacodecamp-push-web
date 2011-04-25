package code.central

import code.protocol._
import org.zeromq.ZMQ
import akka.actor.{Actor, ActorRef}

class ZMQSocketMessagePublisher(port: Int) extends Actor with ZMQContext {
  import ProtocolSerialization._
  import ZMQMultipart._
  
  lazy val publishSocket = {
    val publishSocket = context.socket(ZMQ.PUB)
    publishSocket.bind("tcp://*:" + port)
    publishSocket
  }
  
  def receive = {
    case msg @ UserAt(username, location) =>
      log.info("User " + username + " is at: " + location)
      writeTwoPartMessage(serializeToMessage(msg), publishSocket)
    case msg @ UserGone(who) =>
      log.info(who + " has left")
      writeTwoPartMessage(serializeToMessage(msg), publishSocket)
  }
}

