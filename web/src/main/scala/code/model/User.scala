package code.model

import net.liftweb.http.{ SessionVar, S }
import net.liftweb.common._
import net.liftweb.sitemap._
import net.liftweb.sitemap.Loc._
import code.protocol._
import code.comet._

case class LiftUser(val username: String, var location: Box[Location] = Empty)

object LiftUser {
  object signedIn extends SessionVar[Box[LiftUser]](Empty)
  def signedIn_? = signedIn.is.isDefined
  def signOut = {
    for (u <- signedIn) CentralPush ! UserGone(User(u.username))
    for (r <- S.request) r.request.session.terminate
    S redirectTo "/"
  }
  def signOutMenu = Menu(Loc("SignOut", List("sign-out"), "Sign Out", List(Hidden, Template(signOut _))))
}
