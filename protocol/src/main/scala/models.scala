package code.protocol

case class Location(latitude: Double, longitude: Double) {
  def within5km(other: Location): Boolean = {
    LocationMath.distance(this, other) <= 5
  }
}

case class User(username: String)

// sent on login/logout, and then published out
case class UserAt(user: User, location: Location)
case class UserGone(user: User)

// REQ REP for nearby users
case class NearbyUsersRequest(user: User)
case class NearbyUsers(users: List[UserAt])

// for 3-part message to send out notifications to users when nearby
case class UserNearby(target: User, whoNearby: UserAt)
case class UserNoLongerNearby(target: User, whoLeft: UserGone)
