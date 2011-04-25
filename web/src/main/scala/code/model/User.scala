package code.model

import net.liftweb.http.{ SessionVar, S }
import net.liftweb.common._
import net.liftweb.sitemap._
import net.liftweb.sitemap.Loc._
import code.protocol._
import code.comet._

case class User(val username: String, var location: Box[Location] = Empty)

object User {
  object signedIn extends SessionVar[Box[User]](Empty)
  def signedIn_? = signedIn.is.isDefined
  def signOut = {
    for (u <- signedIn) CentralPush ! UserGone(u.username)
    for (r <- S.request) r.request.session.terminate
    S redirectTo "/"
  }
  def signOutMenu = Menu(Loc("SignOut", List("sign-out"), "Sign Out", List(Hidden, Template(signOut _))))
}
