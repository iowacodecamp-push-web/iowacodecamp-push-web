package code.comet

import net.liftweb.http._
import code.model._
import code.protocol._

class NearbyUsers extends CometActor {
  private var users: List[User] = Nil

  def localSetup() {
    //TODO connect to SUB and filter on signed-in username
  }

  def localShutdown() {
    //TODO close socket
  }

  def render = "*" #> "blah"
  //TODO get initial list of nearby users & display them
  //we should not block page load waiting for Central to send reply
  //we should:
  // - display "loading" message/image
  // - send req asynchronously
  // - when we receive reply, do a re-render or partialUpdate to show users

  /*override def lowPriority = {
    //on NearbyUsersReply => render all users in list
    //on UserNearby => prepend/append user element to container & fade element in
    //on UserNotNearby => fade element out & remove from container
  }*/
}
