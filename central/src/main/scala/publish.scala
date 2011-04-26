package code.central

import code.protocol._
import org.zeromq.ZMQ
import akka.actor.{Actor, ActorRef}

trait ZMQPubSocket {
  val port: Int
  val context: ZMQ.Context
  
  lazy val pubSocket = {
    val publishSocket = context.socket(ZMQ.PUB)
    publishSocket.bind("tcp://*:" + port)
    publishSocket
  }
}

class ZMQSocketBroadcastPublisher(val port: Int) extends Actor with ZMQContext with ZMQPubSocket {
  import ProtocolSerialization._
  import ZMQMultipart._
  
  def receive = {
    case msg @ UserAt(user, location) =>
      log.info("User " + user + " is at: " + location)
      writeTwoPartMessage(serializeToMessage(msg), pubSocket)
    case msg @ UserGone(who) =>
      log.info(who + " has left")
      writeTwoPartMessage(serializeToMessage(msg), pubSocket)
  }
}

class ZMQSocketNearbyPublisher(val port: Int) extends Actor with ZMQContext with ZMQPubSocket {
  import ProtocolSerialization._
  import ZMQMultipart._

  def receive = {
    case msg @ UserNearby(User(targetUsername), _) =>
      log.info("Notifying user " + targetUsername + " that: " + msg)
      writeFilterableMessage(serializeToMessage(targetUsername, msg), pubSocket)
    case msg @ UserNoLongerNearby(User(targetUsername), _) =>
      log.info("Notifying user " + targetUsername + " that: " + msg)
      writeFilterableMessage(serializeToMessage(targetUsername, msg), pubSocket)
  }
}
