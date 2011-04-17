package protocol

//compiler error: expected class or object definition
//type Position = Pair[Double, Double]
case class Location(latitude: Double, longitude: Double)

case class UserAt(username: String, location: Location)
case class UserGone(username: String)
