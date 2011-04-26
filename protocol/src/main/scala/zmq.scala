package code.protocol

import org.zeromq.ZMQ

object ZMQMultipart {
  def blockingReadTwoPartMessage(socket: ZMQ.Socket): TwoPartMessage = {
    val firstPart = new String(socket.recv(0))
    val secondPart = recvMore(socket, 0)

    (firstPart, secondPart)
  }

  /**
   * Does not block reading first part, but does block reading second part.
   * Returns None if no message is read, and Some if a message is read.
   */
  def nonBlockingReadTwoPartMessage(socket: ZMQ.Socket): Option[TwoPartMessage] = {
    val b1 = socket.recv(ZMQ.NOBLOCK)
    if (b1 != null) {
      val p1 = new String(b1)
      val p2 = recvMore(socket, 0)
      Some((p1, p2))
    } else None
  }

  def blockingReadFilterableMessage(socket: ZMQ.Socket): FilterableMessage = {
    val key = new String(socket.recv(0))
    val (className, msg) = if (socket.hasReceiveMore) blockingReadTwoPartMessage(socket)
    else throw new RuntimeException("Expected more")

    (key, className, msg)
  }

  /**
   * Does not block reading first part, but does block reading second & third parts.
   * Returns None if no message is read, and Some if a message is read.
   */
  def nonBlockingReadFilterableMessage(socket: ZMQ.Socket): Option[FilterableMessage] = {
    val b1 = socket.recv(ZMQ.NOBLOCK)
    if (b1 != null) {
      val p1 = new String(b1)
      val p2 = new String(recvMore(socket, 0))
      val p3 = recvMore(socket, 0)
      Some((p1, p2, p3))
    } else None
  }

  def recvMore(socket: ZMQ.Socket, flags: Int) = if (socket.hasReceiveMore)
    socket.recv(flags)
  else
    throw new RuntimeException("Expected more")

  def writeTwoPartMessage(message: TwoPartMessage, socket: ZMQ.Socket) {
    socket.send(message._1.getBytes, ZMQ.SNDMORE)
    socket.send(message._2, 0)
  }

  def writeFilterableMessage(message: FilterableMessage, socket: ZMQ.Socket) {
    socket.send(message._1.getBytes, ZMQ.SNDMORE)
    writeTwoPartMessage((message._2, message._3), socket)
  }
}
