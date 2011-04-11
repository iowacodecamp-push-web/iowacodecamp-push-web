type Position = Pair[Double, Double]

case class UserAt(username: String, position: Position)
case class UserGone(username: String)
