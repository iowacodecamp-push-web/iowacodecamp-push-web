package bootstrap.liftweb

import net.liftweb._
import util._
import Helpers._

import common._
import http._
import sitemap._
import Loc._

import code.rest._
import code.model._

class Boot {
  val mobileDocType = """<!DOCTYPE html PUBLIC "-//WAPFORUM//DTD XHTML Mobile 1.0//EN" "http://www.wapforum.org/DTD/xhtml-mobile10.dtd">"""
  def boot {
    LiftRules.addToPackages("code")
    val entries = List(
      Menu.i("Home") / "index",
      Menu.i("Map") / "map",
      User.signOutMenu)
    LiftRules.setSiteMap(SiteMap(entries: _*))

    LiftRules.ajaxStart = Full(() => LiftRules.jsArtifacts.show("ajax-loader").cmd)
    LiftRules.ajaxEnd = Full(() => LiftRules.jsArtifacts.hide("ajax-loader").cmd)

    LiftRules.jsArtifacts = net.liftweb.http.js.jquery.JQuery14Artifacts
    //LiftRules.htmlProperties.default.set((r: Req) => new Html5Properties(r.userAgent))
    LiftRules.htmlProperties.default.set((r: Req) => r match {
      case Req(List("index"),_,_) => new Html5Properties(r.userAgent).setDocType(() => Full(mobileDocType)) //TODO this is not working...
      case _ => new Html5Properties(r.userAgent)
    })
    LiftRules.early.append(_.setCharacterEncoding("UTF-8"))

    LiftRules.dispatch.prepend(RestApi)
    LiftRules.loggedInTest = Full(() => User.signedIn_?)
  }
}
