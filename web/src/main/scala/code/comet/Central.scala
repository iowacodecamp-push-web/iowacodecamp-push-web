package code.comet

import net.liftweb.actor.LiftActor
import net.liftweb.http.ListenerManager
import net.liftweb.util.Props
import org.salvero.core.{Push, Connect}

object CentralPush extends Push(Props.get("centralPushEndpoint", "tcp://localhost:5558")) with Connect

object CentralSub extends LiftActor with ListenerManager {
  def createUpdate = "Ignore"

  override def lowPriority = {
    case m => updateListeners(m)
  }
}
