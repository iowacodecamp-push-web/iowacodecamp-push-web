package code.protocol

import scala.math

object LocationMath {
  val EARTH_RADIUS = 6371
  
  // return distance in kilometers
  def distance(l1: Location, l2: Location): Double = {
    val (l1LatR, l1LongR) = (math.toRadians(l1.latitude), math.toRadians(l1.longitude))
    val (l2LatR, l2LongR) = (math.toRadians(l2.latitude), math.toRadians(l2.longitude))
    val dLat = l2LatR - l1LatR
    val dLong = l2LongR - l1LongR
    val a = math.pow(math.sin(dLat /2), 2) +
            math.cos(l1LatR) * math.cos(l2LatR) *
            math.pow(math.sin(dLong / 2), 2)
    val c = 2 * math.atan2(math.sqrt(a), math.sqrt(1 - a))
    EARTH_RADIUS * c
  }
  
}
