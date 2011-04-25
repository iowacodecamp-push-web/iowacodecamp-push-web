package code.comet

import net.liftweb.http._
import net.liftweb.http.js._
import net.liftweb.http.js.JsCmds._
import net.liftweb.http.js.JE._
import code.protocol._
import scala.xml.NodeSeq

class MapView extends CometActor with CometListener {
  def registerWith = CentralSub

  override def lowPriority = {
    case UserAt(username, Location(lat, lng)) =>
      partialUpdate(Call("userAt", Str(username), Num(lat), Num(lng)))
    case UserGone(username) =>
      partialUpdate(Call("userGone", Str(username)))
  }

  override def render = NodeSeq.Empty
}
