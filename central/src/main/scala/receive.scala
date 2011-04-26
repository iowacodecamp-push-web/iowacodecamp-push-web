package code.central

import code.protocol._
import org.zeromq.ZMQ
import akka.actor.{Actor, ActorRef}
import akka.routing.Listeners

case object ReceiveMessage

class ZMQSocketMessageReceiver(port: Int) extends Actor with ZMQContext with Listeners {
  import ProtocolDeserialization._
  import ZMQMultipart._
  
  lazy val pullSocket = {
    val pullSocket = context.socket(ZMQ.PULL)
    pullSocket.bind("tcp://*:" + port)
    pullSocket
  }

  def receive = listenerManagement orElse {
    case ReceiveMessage =>
      val message = ((blockingReadTwoPartMessage _) andThen (deserializeMessage _))(pullSocket)
      gossip(message)
      self ! ReceiveMessage
  }
}

class CentralBroadcastReceiver(centralPublisher: ActorRef) extends Actor {
  def receive = {
    case msg @ UserAt(user, location) =>
      log.info("User " + user + " is at: " + location)
      centralPublisher forward msg
    case msg @ UserGone(who) =>
      log.info(who + " has left")
      centralPublisher forward msg
    case msg => log.info("ignoring message " + msg)
  }
}

class NearbyUsersBroadcast(centralNearbyPublisher: ActorRef) extends Actor {
  var userLocations: Map[User, Location] = Map()

  def receive = {
    case msg @ UserAt(user, location) =>
      forThoseWithin5kmOf(user, location) { otherUser =>
        centralNearbyPublisher ! UserNearby(otherUser, msg)
      }
      userLocations += (user -> location)
    case msg @ UserGone(who) =>
      val location = userLocations(who)
      forThoseWithin5kmOf(who, location) { otherUser =>
        centralNearbyPublisher ! UserNoLongerNearby(otherUser, msg)
      }
      userLocations -= who
  }

  def forThoseWithin5kmOf(user: User, from: Location)(f: (User) => Unit) {
    for {
      (u, l) <- userLocations if l.within5km(from) && u != user
    } f(u)
  }
}
