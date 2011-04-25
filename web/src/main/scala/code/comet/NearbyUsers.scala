package code.comet

import net.liftweb.http._
import net.liftweb.http.js.jquery.JqJsCmds._
import net.liftweb.common.Logger
import net.liftweb.util.Props
import code.model._
import code.model.LiftUser.signedIn
import code.protocol._
import code.zeromq._
import scala.xml.NodeSeq

class NearbyUsers extends CometActor with Logger {
  lazy val subscriber = new FilteredSubscriber(Props.get("centralNearbySubEndpoint", "tcp://localhost:5555"), this, signedIn map { _.username } openOr (throw new IllegalStateException("No signed-in user")))

  override def localSetup() {
    subscriber ! Receive
  }

  override def localShutdown() {
    subscriber ! Stop //TODO if subscriber is blocking on receive, it won't get this!
  }

  def containerId = "nearbyUsers"
  def render = "*" #> "loading..."
  //TODO get initial list of nearby users & display them
  //we should not block page load waiting for Central to send reply
  //we should:
  // - display "loading" message/image
  // - send req asynchronously
  // - when we receive reply, do a re-render or partialUpdate to show users

  //a Subscriber will send messages here
  override def lowPriority = {
    //on NearbyUsersReply => render all users in list

    case UserNearby(_, UserAt(other, _)) =>
      debug(other + " is now nearby")
      partialUpdate(PrependHtml(containerId, render(other)) & FadeIn(id(other)))

    case UserNoLongerNearby(_, UserGone(other)) =>
      debug(other + " is no longer nearby")
      partialUpdate(FadeOut(id(other)) & Remove(id(other)))
  }

  def id(u: User) = u.username
  def render(u: User): NodeSeq = <li id={ id(u) } style="display:none;">{ u.username }</li>

  //this should really be part of Lift, but is suspiciously missing...
  import net.liftweb.http.js._
  import net.liftweb.http.js.jquery._
  object Remove {
    def apply(uid: String): JsCmd = JqJE.JqId(JE.Str(uid)) ~> JqJE.JqRemove()
  }
}
