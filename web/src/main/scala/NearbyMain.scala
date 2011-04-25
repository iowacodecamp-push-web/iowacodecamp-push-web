package code.app

import code.protocol._
import code.comet._

object NearbyMain {
  def main(args: Array[String]) {
    val publisher = new Publisher("tcp://*:5555")
    var count = 0
    while (true) {
      count += 1
      Thread.sleep(3000)
      val msg = UserNearby(User("Zach"), UserAt(User("User" + count), Location(1.1, 2.2)))
      publisher ! ("Zach", msg)
      println("Published " + msg)
    }
  }
}
