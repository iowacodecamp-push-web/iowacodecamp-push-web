package code.model

import net.liftweb.http.{SessionVar, S}
import net.liftweb.common._
import net.liftweb.sitemap._
import net.liftweb.sitemap.Loc._

object User {
  object signedIn extends SessionVar[Option[String]](Empty)
  def signedIn_? = signedIn.is.isDefined
  def signOut = {
    for (r <- S.request) r.request.session.terminate
    S redirectTo "/"
  }
  def signOutMenu = Menu(Loc("SignOut", List("sign-out"), "Sign Out", List(Hidden, Template(signOut _))))
}
