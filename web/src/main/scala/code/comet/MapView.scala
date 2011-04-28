package code.comet

import net.liftweb.http.{CometActor, CometListener}
import net.liftweb.http.js.JsCmds._
import net.liftweb.http.js.JE._
import code.protocol.{UserAt, User, Location, UserGone}
import scala.xml.NodeSeq

class MapView extends CometActor with CometListener {
  def registerWith = CentralSub

  override def lowPriority = {
    case UserAt(User(username), Location(lat, lng)) =>
      partialUpdate(Call("userAt", Str(username), Num(lat), Num(lng)))
    case UserGone(User(username)) =>
      partialUpdate(Call("userGone", Str(username)))
  }

  //this snippet just sets up the comet connection, nothing to render
  def render = NodeSeq.Empty
}
