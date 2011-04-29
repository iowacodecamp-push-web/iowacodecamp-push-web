package code.central

import akka.actor.Actor
import Actor._
import akka.routing.Listen
import org.salvero.core.{Publish, FilterablePublish, Bind}
import org.salvero.akka.{Pull, Start}

object CentralServer {
  val PULL_PORT    = 5558
  val BROADCAST_PUBLISH_PORT = 5559
  val NEARBY_PUBLISH_PORT = 5560
  
  def main(args: Array[String]) {
    val handler = actorOf[Handler].start
    val zmqReceiver = actorOf(new Pull("tcp://*:" + PULL_PORT, handler) with Bind).start
    
    val broadcastPublisher = new Publish("tcp://*:" + BROADCAST_PUBLISH_PORT)
    val broadcastReceiver = actorOf(new CentralBroadcastReceiver(broadcastPublisher)).start
    
    val nearbyPublisher = new FilterablePublish("tcp://*:" + NEARBY_PUBLISH_PORT)
    val nearbyReceiver = actorOf(new NearbyUsersBroadcast(nearbyPublisher)).start
    
    handler ! Listen(broadcastReceiver)
    handler ! Listen(nearbyReceiver)
    zmqReceiver ! Start
  }
}

