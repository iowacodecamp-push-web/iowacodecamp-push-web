package code.comet

import net.liftweb.actor._
import code.model._
import protocol._

object Central extends LiftActor {
  override def messageHandler = {
    case u: User =>
      //TODO implicit User => UserAt
      for (l <- u.location) {
        val msg = UserAt(u.username, l)
        println("Sending " + msg + " to Central...")
      }
    case g: UserGone => println("Sending " + g + " to Central...")
  }
}
