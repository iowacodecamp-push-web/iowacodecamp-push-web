package code.zeromq

import code.protocol._
import org.zeromq.ZMQ
import net.liftweb.actor.LiftActor
import net.liftweb.common.Logger

sealed trait Message
case object Receive extends Message
case object Stop extends Message

trait Sender {
  import ProtocolSerialization._
  import ZMQMultipart._
  def socket: ZMQ.Socket
  def ![A <: CaseClass: Manifest](msg: A) {
    writeTwoPartMessage(serializeToMessage(msg), socket)
  }

  def ![A <: CaseClass : Manifest](t: (String, A)) {
    writeFilterableMessage(serializeToMessage(t._1, t._2), socket)
  }
}

trait Receiver {
  import ProtocolDeserialization._
  import ZMQMultipart._
  def socket: ZMQ.Socket
  def blockingReceive() = ((blockingReadTwoPartMessage _) andThen (deserializeMessage _))(socket)
  //def blockingReceive() = deserializeMessage(blockingReadTwoPartMessage(socket))
  def blockingReceiveFilterable() = ((blockingReadFilterableMessage _) andThen (deserializeMessage _))(socket)
}

class FilteredSubscriber(endpoint: String, next: LiftActor, filter: String = "") extends AbstractSubscriber(endpoint, next, filter) {
  def receive() = blockingReceiveFilterable() //TODO we can't receive a Stop message while this is blocking...
}
class Subscriber(endpoint: String, next: LiftActor) extends AbstractSubscriber(endpoint, next) {
  def receive() = blockingReceive() //TODO we can't receive a Stop message while this is blocking...
}
abstract class AbstractSubscriber(endpoint: String, next: LiftActor, filter: String = "") extends LiftActor with Receiver with Logger {
  var context: ZMQ.Context = ZMQ.context(1)
  var socket: ZMQ.Socket = {
    val s = context.socket(ZMQ.SUB)
    s.subscribe(filter.getBytes)
    s.connect(endpoint)
    debug("Connected to SUB socket at " + endpoint)
    s
  }

  def receive(): Any

  override def messageHandler = {
    case Receive => if (socket != null) {
      debug("Receiving...")
      val msg = receive()
      debug("Received " + msg)
      next ! msg
      this ! Receive
    } else warn("SUB socket already closed")

    case Stop =>
      socket.close()
      context.term()
      socket = null
      context = null
      debug("Closed SUB socket at " + endpoint)
  }
}

class Pusher(val endpoint: String) extends Sender with Logger {
  val context: ZMQ.Context = ZMQ.context(1)
  val socket: ZMQ.Socket = {
    val s = context.socket(ZMQ.PUSH)
    s.connect(endpoint)
    debug("Connected to PUSH socket at " + endpoint)
    s
  }

  def close() {
    socket.close()
    context.term()
    debug("Closed PUSH socket at " + endpoint)
  }
}

class Publisher(val endpoint: String) extends Sender with Logger {
  val context: ZMQ.Context = ZMQ.context(1)
  val socket: ZMQ.Socket = {
    val s = context.socket(ZMQ.PUB)
    s.bind(endpoint)
    debug("Bound to PUB socket at " + endpoint)
    s
  }

  def close() {
    socket.close()
    context.term()
    debug("Closed PUB socket at " + endpoint)
  }
}
