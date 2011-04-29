package code.api

import dispatch.json.JsValue
import org.jboss.netty.buffer.ChannelBuffers
import org.jboss.netty.channel.{ChannelFutureListener, ChannelFuture}
import org.jboss.netty.channel.group.DefaultChannelGroup
import org.jboss.netty.handler.codec.http.{HttpHeaders, DefaultHttpChunk}
import unfiltered.netty.ReceivedMessage
import unfiltered.request.HttpRequest
import unfiltered.response.{TransferEncoding, JsonContent, Connection}

case class AddReq(req: HttpRequest[ReceivedMessage])

trait ChunkedJsonChannelSupport {
  import Headers._
  val clients = new DefaultChannelGroup

  def chunk(json: JsValue) =
    new DefaultHttpChunk(ChannelBuffers.copiedBuffer((json + "\n").getBytes("utf-8")))

  def writeChunk(json: JsValue) = clients.write(chunk(json))
  
  def channelManagement: PartialFunction[Any, Unit] = {
    case AddReq(req) =>
      val ch = req.underlying.event.getChannel
      val initial = req.underlying.defaultResponse(ChunkedJson)
      ch.write(initial).addListener { () =>
        clients add ch
      }
  }

  implicit def block2listener[T](block: () => T): ChannelFutureListener =
    new ChannelFutureListener {
      def operationComplete(future: ChannelFuture) { block() }
   }
}

object Headers {
  val ChunkedJson =
    Connection(HttpHeaders.Values.CLOSE) ~>
    TransferEncoding(HttpHeaders.Values.CHUNKED) ~>
    JsonContent
}

