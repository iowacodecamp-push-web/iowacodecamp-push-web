package code.central

import code.protocol._
import org.zeromq.ZMQ

object Push extends ZMQContext {
  import ProtocolSerialization._
  import ZMQMultipart._
  
  def main(args: Array[String]) {
    val pushSocket = context.socket(ZMQ.PUSH)
    pushSocket.connect("tcp://localhost:5558")

    writeTwoPartMessage(serializeToMessage(UserAt(User("Faraway"), Location(4.0, 5.0))), pushSocket)
    writeTwoPartMessage(serializeToMessage(UserAt(User("Lou"), Location(2.0, 3.0))), pushSocket)
    
    writeTwoPartMessage(serializeToMessage(UserAt(User("Luke"), Location(2.02, 3.02))), pushSocket)
    writeTwoPartMessage(serializeToMessage(UserGone(User("Luke"))), pushSocket)
    
  }
}
