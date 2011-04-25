package code.protocol

import org.zeromq.ZMQ

object ZMQMultipart {
  def blockingReadTwoPartMessage(socket: ZMQ.Socket): TwoPartMessage = {
    val firstPart = new String(socket.recv(0))
    val secondPart = if (socket.hasReceiveMore) {
      socket.recv(0)
    } else throw new RuntimeException("Expected more")

    (firstPart, secondPart)
  }

  def blockingReadFilterableMessage(socket: ZMQ.Socket): FilterableMessage = {
    val key = new String(socket.recv(0))
    val (className, msg) = if (socket.hasReceiveMore) blockingReadTwoPartMessage(socket)
    else throw new RuntimeException("Expected more")

    (key, className, msg)
  }

  def writeTwoPartMessage(message: TwoPartMessage, socket: ZMQ.Socket) {
    socket.send(message._1.getBytes, ZMQ.SNDMORE)
    socket.send(message._2, 0)
  }

  def writeFilterableMessage(message: FilterableMessage, socket: ZMQ.Socket) {
    socket.send(message._1.getBytes, ZMQ.SNDMORE)
    writeTwoPartMessage((message._2, message._3), socket)
  }
}
