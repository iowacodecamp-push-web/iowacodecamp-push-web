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

  def writeTwoPartMessage(message: TwoPartMessage, socket: ZMQ.Socket) {
    socket.send(message._1.getBytes, ZMQ.SNDMORE)
    socket.send(message._2, 0)
  }
}
