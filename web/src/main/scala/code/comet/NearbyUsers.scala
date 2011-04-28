package code.comet

import net.liftweb.http.CometActor
import net.liftweb.common.Logger
import net.liftweb.util.Props
import code.model.LiftUser.signedIn
import code.protocol._
import code.js.WebJsCmds._
import org.salvero.lift.{ FilterableSubscribe, Start, Stop }
import scala.xml.NodeSeq

class NearbyUsers extends CometActor with Logger {
  var users: Set[User] = Set()
  def key = signedIn map { _.username } openOr (throw new IllegalStateException("No signed-in user"))
  val subscribe = new FilterableSubscribe(Props.get("centralNearbySubEndpoint", "tcp://localhost:5560"), this, Set(key))

  override def localSetup() {
    subscribe ! Start
  }

  override def localShutdown() {
    subscribe ! Stop
  }

  def containerId = "nearbyUsers"
  def render = NodeSeq.Empty
  override def lowPriority = {
    case UserNearby(_, UserAt(other, _)) => if (!(users contains other)) {
      users += other
      debug(other + " is now nearby")
      partialUpdate(PrependAndFade(containerId, render(other), id(other)))
    }
    case UserNoLongerNearby(_, UserGone(other)) => if (users contains other) {
      users -= other
      debug(other + " is no longer nearby")
      partialUpdate(FadeAndRemove(id(other)))
    }
  }

  def id(u: User) = u.username
  def render(u: User) = <li id={ id(u) } style="display:none;">{ u.username }</li>
}
