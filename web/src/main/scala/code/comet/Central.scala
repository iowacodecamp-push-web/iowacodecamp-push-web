package code.comet

import net.liftweb.actor._
import net.liftweb.http._
import code.model._
import protocol._

object CentralReq extends LiftActor {
  //TODO wrap a 0MQ Req socket and send messages to it
  override def messageHandler = {
    case u: User =>
      //TODO implicit User => UserAt
      for (l <- u.location) {
        val msg = UserAt(u.username, l)
        println("Sending " + msg + " to Central...")
        CentralSub ! msg //for development only!
      }
    case g: UserGone =>
      println("Sending " + g + " to Central...")
      CentralSub ! g //for development only!
  }
}

object CentralSub extends LiftActor with ListenerManager {
  //TODO wrap a 0MQ Sub socket and receive messages from it
  def createUpdate = "Registered" //do we need this?
  override def lowPriority = {
    case ua: UserAt => updateListeners(ua)
    case ug: UserGone => updateListeners(ug)
  }
}
