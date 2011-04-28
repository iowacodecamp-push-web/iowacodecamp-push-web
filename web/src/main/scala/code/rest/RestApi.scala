package code.rest

import net.liftweb.common._
import net.liftweb.http._
import net.liftweb.http.rest._
import code.model.LiftUser
import code.comet.CentralPush
import code.protocol.{ UserAt, User, Location }

object RestApi extends RestHelper {
  serve {
    case Post(List("location"), _) =>
      for {
        user <- LiftUser.signedIn
        lat <- S param "latitude" map { _.toDouble }
        lng <- S param "longitude" map { _.toDouble }
      } {
        user.location = Full(Location(lat, lng))
        CentralPush ! UserAt(User(user.username), Location(lat, lng))
      }
      OkResponse()
  }
}
