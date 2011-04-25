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
  private var context: ZMQ.Context = ZMQ.context(1)
  private var socket: ZMQ.Socket = {
    val s = context.socket(ZMQ.PUSH)
    val endpoint = Props.get("centralPushEndpoint", "tcp://localhost:5555")
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

        //TODO type class?
        val baos = new ByteArrayOutputStream
        val encoder = EncoderFactory.get().binaryEncoder(baos, null)
        grater[UserAt].serialize(msg, encoder)
        socket.send(baos.toByteArray, 0)
      }

    case g: UserGone =>
      println("Sending " + g + " to Central...")
      //CentralSub ! g

      //TODO type class?
      val baos = new ByteArrayOutputStream
      val encoder = EncoderFactory.get().binaryEncoder(baos, null)
      grater[UserGone].serialize(g, encoder)
      socket.send(baos.toByteArray, 0)

    case Stop =>
      socket.close()
      context.term()
      debug("Closed PUSH socket")
  }
}

object CentralSub extends LiftActor with ListenerManager with Logger {
  private var context: ZMQ.Context = ZMQ.context(1)
  private var socket: ZMQ.Socket = {
    val s = context.socket(ZMQ.SUB)
    val endpoint = Props.get("centralSubEndpoint", "tcp://localhost:5556")
    s.subscribe("".getBytes)
    s.connect(endpoint)
    debug("Connected to SUB socket at " + endpoint)
    s
  }

  def createUpdate = "Registered" //do we need this?
  override def lowPriority = {
    //case ua: UserAt => updateListeners(ua)
    //case ug: UserGone => updateListeners(ug)
    case Receive => if (socket != null) {
      val bytes = socket.recv(ZMQ.NOBLOCK)
      if (bytes != null) {
        //TODO deserialize bytes into either UserAt or UserGone, then call updateListeners
        //how do we do that? try grater[UserAt], catch exception and then try grater[UserGone]?
      } else
        Thread.sleep(1)
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
