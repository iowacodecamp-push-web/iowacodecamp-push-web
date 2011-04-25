package code.comet

import net.liftweb.actor._
import net.liftweb.http._
import net.liftweb.util.Props
import net.liftweb.common.Logger
import code.model._
import code.protocol._
import org.zeromq.ZMQ

trait Sender {
  import ProtocolSerialization._
  import ZMQMultipart._
  def socket: ZMQ.Socket
  def ![A <: CaseClass: Manifest](msg: A) {
    writeTwoPartMessage(serializeToMessage(msg), socket)
  }
}

trait Receiver {
  import ProtocolDeserialization._
  import ZMQMultipart._
  def socket: ZMQ.Socket
  def blockingReceive() = ((blockingReadTwoPartMessage _) andThen (deserializeMessage _))(socket)
  //val msg = deserializeMessage(blockingReadTwoPartMessage(socket))
}

/*class Subscriber(val endpoint: String, val next: LiftActor) extends Receiver {
  
}*/

class Pusher(val endpoint: String) extends Sender {
  lazy val context: ZMQ.Context = ZMQ.context(1)
  lazy val socket: ZMQ.Socket = {
    val s = context.socket(ZMQ.PUSH)
    s.connect(endpoint)
    s
  }

  def close() {
    socket.close()
    context.term()
  }
}

sealed trait Message
case object Receive extends Message
case object Stop extends Message

object CentralPush extends LiftActor with Logger {
  lazy val pusher = new Pusher(Props.get("centralPushEndpoint", "tcp://localhost:5558"))

  override def messageHandler = {
    case u: LiftUser =>
      //TODO implicit User => UserAt
      for (l <- u.location) {
        val msg = UserAt(User(u.username), l)
        println("Sending " + msg + " to Central...")
        pusher ! msg
      }

    case g: UserGone =>
      println("Sending " + g + " to Central...")
      pusher ! g

    case Stop =>
      pusher.close()
  }
}

//create Subscriber with an endpoint and a LiftActor, it receives msg from 0MQ and !s it to LiftActor
//or should Subscriber just provide a receive(): Any method, and CentralSub calls it?

object CentralSub extends LiftActor with ListenerManager with Receiver with Logger {
  //lazy val subscriber = new Subscriber(Props.get("centralSubEndpoint", "tcp://localhost:5559"))

  var context: ZMQ.Context = ZMQ.context(1)
  var socket: ZMQ.Socket = {
    val s = context.socket(ZMQ.SUB)
    val endpoint = Props.get("centralSubEndpoint", "tcp://localhost:5559")
    s.subscribe("".getBytes)
    s.connect(endpoint)
    debug("Connected to SUB socket at " + endpoint)
    s
  }

  def createUpdate = "Registered" //do we need this?
  override def lowPriority = {
    case Receive => if (socket != null) {
      val msg = blockingReceive() //TODO we can't receive a Stop message while this is blocking...
      debug("Received " + msg)
      updateListeners(msg)
      this ! Receive
    } else warn("SUB socket already closed")

    case Stop =>
      socket.close()
      context.term()
      socket = null
      context = null
      debug("Closed SUB socket")
  }
}
