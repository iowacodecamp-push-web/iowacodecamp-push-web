package code.central

import akka.actor.Actor
import Actor._

object CentralServer {
  val PULL_PORT    = 5558
  val PUBLISH_PORT = 5559
  
  def main(args: Array[String]) {
    val publisher = actorOf(new ZMQSocketMessagePublisher(PUBLISH_PORT)).start
    val receiver = actorOf(new CentralReceiver(publisher)).start
    val zmqReceiver = actorOf(new ZMQSocketMessageReceiver(receiver, PULL_PORT)).start
    
    zmqReceiver ! ReceiveMessage
  }
}

