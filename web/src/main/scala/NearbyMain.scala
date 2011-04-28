package code.app

import code.protocol._
import code.comet._
import org.salvero.core.FilterablePublish

object NearbyMain {
  def main(args: Array[String]) {
    val publish = new FilterablePublish("tcp://*:5560")
    val max = 5
    for (i <- 1 to max) {
      Thread.sleep(3000)
      val msg = UserNearby(User("Zach"), UserAt(User("User" + i), Location(1.1, 2.2)))
      publish ! ("Zach", msg)
      println("Published " + msg)
    }

    for (i <- 1 to max) {
      Thread.sleep(3000)
      val msg = UserNoLongerNearby(User("Zach"), UserGone(User("User" + i)))
      publish ! ("Zach", msg)
      println("Published " + msg)
    }
  }
}
