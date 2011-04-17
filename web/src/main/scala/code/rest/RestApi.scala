package code.rest

import net.liftweb.common._
import net.liftweb.http._
import net.liftweb.http.rest._
import code.model._
import code.comet._
import protocol._

object RestApi extends RestHelper {
  serve {
    //TODO 4xx response on:
    // - no signed-in User?
    // - no lat or lng params
    // - lat or lng param not parseable as Double
    case Post(List("location"), _) =>
      for {
        u <- User.signedIn
        lat <- S param "latitude"
        lng <- S param "longitude"
      } {
        u.location = Full(Location(lat.toDouble, lng.toDouble))
	Central ! u
      }
      OkResponse()
  }
}
