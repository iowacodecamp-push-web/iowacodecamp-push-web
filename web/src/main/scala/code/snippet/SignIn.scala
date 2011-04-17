package code.snippet

import net.liftweb.common._
import net.liftweb.http.SHtml._
import net.liftweb.util.Helpers._
import code.model._

class SignIn {
  def render = "@username" #> text("", u => User.signedIn(Full(u)))
}
