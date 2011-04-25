package code.central

import akka.actor.Actor
import Actor._
import akka.routing.Listen

object CentralServer {
  val PULL_PORT    = 5558
  val PUBLISH_PORT = 5559
  
  def main(args: Array[String]) {
    val zmqReceiver = actorOf(new ZMQSocketMessageReceiver(PULL_PORT)).start
    
    val publisher = actorOf(new ZMQSocketMessagePublisher(PUBLISH_PORT)).start
    val receiver = actorOf(new CentralBroadcastReceiver(publisher)).start
    
    zmqReceiver ! Listen(receiver)
    zmqReceiver ! ReceiveMessage
  }
}

