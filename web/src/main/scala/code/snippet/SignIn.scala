package code.snippet

import net.liftweb.common.Full
import net.liftweb.http.SHtml._
import net.liftweb.util.Helpers._
import code.model.LiftUser

class SignIn {
  def render = "@username" #> text("", u => LiftUser.signedIn(Full(LiftUser(u))))
}
