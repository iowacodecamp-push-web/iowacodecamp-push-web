package code.api
  
import code.protocol._
import dispatch.json.{JsString, JsObject, JsNumber}
import sjson.json.{DefaultProtocol, Writes, JsonSerialization}

object JsonProtocols {
  import JsonSerialization._
  import DefaultProtocol._

  implicit object userWrites extends Writes[User] {
    def writes(user: User) = JsString(user.username)
  }
  
  implicit object locationWrites extends Writes[Location] {
    def writes(location: Location) =
      JsObject(List(
        JsString("lat") -> JsNumber(location.latitude),
        JsString("long") -> JsNumber(location.longitude)
      ))
  }
  
  implicit object userAtWrites extends Writes[UserAt] {
    def writes(userAt: UserAt) = {
      JsObject(List(
        JsString("userAt") ->
          JsObject(List(
            JsString("user") -> tojson(userAt.user),
            JsString("location") -> tojson(userAt.location)
          ))
      ))
    }
  }

  implicit object userGoneWrites extends Writes[UserGone] {
    def writes(userGone: UserGone) = {
      JsObject(List(
        JsString("userGone") ->
          JsObject(List(
            JsString("user") -> tojson(userGone.user)
          ))
      ))
    }
  }

}
