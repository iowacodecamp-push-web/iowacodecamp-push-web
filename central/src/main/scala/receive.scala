package code.central

import code.protocol._
import akka.actor.{Actor, ActorRef}
import akka.routing.Listeners
import org.salvero.core.{Send, FilterableSend}

case object ReceiveMessage

class Handler extends Actor with Listeners {
  def receive = listenerManagement orElse {
    case msg => gossip(msg)
  }
}

class CentralBroadcastReceiver(centralPublisher: Send) extends Actor {
  def receive = {
    case msg @ UserAt(user, location) =>
      log.info("User " + user + " is at: " + location)
      centralPublisher ! msg
    case msg @ UserGone(who) =>
      log.info(who + " has left")
      centralPublisher ! msg
    case msg => log.info("ignoring message " + msg)
  }
}


class NearbyUsersBroadcast(centralNearbyPublisher: FilterableSend) extends Actor {
  var userLocations: Map[User, Location] = Map()

  def receive = {
    case msg @ UserAt(user, location) =>
      forThoseWithin5kmOf(user, location) { otherUser =>
        centralNearbyPublisher ! (otherUser.username, UserNearby(otherUser, msg))
      }
      userLocations += (user -> location)
    case msg @ UserGone(who) =>
      val location = userLocations(who)
      forThoseWithin5kmOf(who, location) { otherUser =>
        centralNearbyPublisher ! (otherUser.username, UserNoLongerNearby(otherUser, msg))
      }
      userLocations -= who
  }

  def forThoseWithin5kmOf(user: User, from: Location)(f: (User) => Unit) {
    for {
      (u, l) <- userLocations if l.within5km(from) && u != user
    } f(u)
  }
}
