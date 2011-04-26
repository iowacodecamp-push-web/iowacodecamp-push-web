package code.central

import akka.actor.Actor
import Actor._
import akka.routing.Listen

object CentralServer {
  val PULL_PORT    = 5558
  val BROADCAST_PUBLISH_PORT = 5559
  val NEARBY_PUBLISH_PORT = 5560
  
  def main(args: Array[String]) {
    val zmqReceiver = actorOf(new ZMQSocketMessageReceiver(PULL_PORT)).start
    
    val broadcastPublisher = actorOf(new ZMQSocketBroadcastPublisher(BROADCAST_PUBLISH_PORT)).start
    val broadcastReceiver = actorOf(new CentralBroadcastReceiver(broadcastPublisher)).start
    
    val nearbyPublisher = actorOf(new ZMQSocketNearbyPublisher(NEARBY_PUBLISH_PORT)).start
    val nearbyReceiver = actorOf(new NearbyUsersBroadcast(nearbyPublisher)).start
    
    zmqReceiver ! Listen(broadcastReceiver)
    zmqReceiver ! Listen(nearbyReceiver)
    zmqReceiver ! ReceiveMessage
  }
}

