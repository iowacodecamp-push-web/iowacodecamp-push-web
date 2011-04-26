package code.comet

import net.liftweb.http.CometActor
import net.liftweb.common.Logger
import net.liftweb.util.Props
import code.model.LiftUser.signedIn
import code.protocol._
import code.js.WebJsCmds._
import code.zeromq._

class NearbyUsers extends CometActor with Logger {
  val subscriber = new FilteredSubscriber(
    Props.get("centralNearbySubEndpoint", "tcp://localhost:5560"),
    this,
    signedIn map { _.username } openOr (throw new IllegalStateException("No signed-in user")))

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
      partialUpdate(PrependAndFade(containerId, render(other), id(other)))

    case UserNoLongerNearby(_, UserGone(other)) =>
      debug(other + " is no longer nearby")
      partialUpdate(FadeAndRemove(id(other)))
  }

  def id(u: User) = u.username
  def render(u: User) = <li id={ id(u) } style="display:none;">{ u.username }</li>
}
