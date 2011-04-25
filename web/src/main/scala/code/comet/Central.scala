package code.comet

import net.liftweb.actor._
import net.liftweb.http._
import net.liftweb.util.Props
import net.liftweb.common.Logger
import code.model._
import code.protocol._
import org.zeromq.ZMQ

import com.banno.salat.avro._
import global._
import java.io._
import org.apache.avro.io._

sealed trait Message
case object Receive extends Message
case object Stop extends Message

object CentralPush extends LiftActor with Logger {
  import ProtocolSerialization._
  import ZMQMultipart._
  lazy val context: ZMQ.Context = ZMQ.context(1)
  lazy val socket: ZMQ.Socket = {
    val s = context.socket(ZMQ.PUSH)
    val endpoint = Props.get("centralPushEndpoint", "tcp://localhost:5558")
    s.connect(endpoint)
    debug("Connected to PUSH socket at " + endpoint)
    s
  }

  override def messageHandler = {
    case u: User =>
      //TODO implicit User => UserAt
      for (l <- u.location) {
        val msg = UserAt(u.username, l)
        println("Sending " + msg + " to Central...")
        //CentralSub ! msg
        writeTwoPartMessage(serializeToMessage(msg), socket)
      }

    case g: UserGone =>
      println("Sending " + g + " to Central...")
      //CentralSub ! g
      writeTwoPartMessage(serializeToMessage(g), socket)

    case Stop =>
      socket.close()
      context.term()
      debug("Closed PUSH socket")
  }
}

object CentralSub extends LiftActor with ListenerManager with Logger {
  import ProtocolDeserialization._
  import ZMQMultipart._
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
      //TODO we can't receive a Stop message while this is blocking...
      val msg = ((blockingReadTwoPartMessage _) andThen (deserializeMessage _))(socket)
      //val msg = deserializeMessage(blockingReadTwoPartMessage(socket))
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
