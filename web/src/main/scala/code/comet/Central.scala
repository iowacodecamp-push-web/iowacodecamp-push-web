package code.comet

import net.liftweb.actor._
import net.liftweb.http._
import net.liftweb.util.Props
import net.liftweb.common.Logger
import code.model._
import code.protocol._
import code.zeromq._
import org.zeromq.ZMQ

object CentralPush extends LiftActor with Logger {
  lazy val pusher = new Pusher(Props.get("centralPushEndpoint", "tcp://localhost:5558"))

  override def messageHandler = {
    case u: LiftUser =>
      //TODO implicit LiftUser => UserAt
      for (l <- u.location) {
        val msg = UserAt(User(u.username), l)
        println("Sending " + msg + " to Central...")
        pusher ! msg
      }

    case g: UserGone =>
      println("Sending " + g + " to Central...")
      pusher ! g

    case Stop =>
      pusher.close()
  }
}

object CentralSub extends LiftActor with ListenerManager {
  lazy val subscriber = new Subscriber(Props.get("centralSubEndpoint", "tcp://localhost:5559"), this)

  def createUpdate = "Registered" //do we need this?
  override def lowPriority = {
    case Receive => subscriber ! Receive
    case Stop => subscriber ! Stop
    case m => updateListeners(m)
  }
}
