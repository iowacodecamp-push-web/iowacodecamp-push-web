package code.rest

import net.liftweb.common._
import net.liftweb.http._
import net.liftweb.http.rest._

object RestApi extends RestHelper {
  serve {
    case Post(List("location"),_) => 
      val latitude = S param "latitude"
      val longitude = S param "longitude"
      println("lat = " + latitude + ", lng = " + longitude)
      OkResponse()
  }
}
