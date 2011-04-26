package code.js

import net.liftweb.http.js._
import net.liftweb.http.js.jquery._
import net.liftweb.http.js.JsCmds._
import net.liftweb.http.js.jquery.JqJsCmds._
import net.liftweb.util.Helpers._
import scala.xml.NodeSeq

object WebJsCmds {
  object Remove {
    /** Remove element with specified id from dom. */
    def apply(id: String): JsCmd = JqJE.JqId(JE.Str(id)) ~> JqJE.JqRemove()
  }

  val zero = 0 seconds
  val fadeTime = 1 second

  object PrependAndFade {
    /** Prepend specified html to specified container and then fade in specified element. */
    def apply(containerId: String, html: NodeSeq, fadeId: String) =
      PrependHtml(containerId, html) & FadeIn(fadeId, zero, fadeTime)
  }

  object FadeAndRemove {
    /** Fade out specified element and then remove it from dom. */
    def apply(id: String) = FadeOut(id, zero, fadeTime) & After(fadeTime, Remove(id))
  }
}
