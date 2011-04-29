package code.api

import akka.actor._
import Actor._
import akka.routing.Listen
import code.protocol._
import dispatch.json.JsValue
import sjson.json.JsonSerialization

object Streams {
  lazy val allStream = {
    val actor = actorOf[AllStreamActor].start
    Subs.centralSub ! Listen(actor)
    actor
  }
}

class AllStreamActor extends Actor with ChunkedJsonChannelSupport {
  import JsonSerialization._
  import JsonProtocols._
  
  def receive = channelManagement orElse {
    case msg: UserAt => writeChunk(tojson(msg))
    case msg: UserGone => writeChunk(tojson(msg))
  }
}

