package code.central

import org.zeromq.ZMQ

object Pull {
  def main(args: Array[String]) {
    val context = ZMQ.context(1)
    val pullSocket = context.socket(ZMQ.PULL)
    pullSocket.bind("tcp://*:5558")
    val name = new String(pullSocket.recv(0))
    println("hello " + name)
  }
}

object Push {
  def main(args: Array[String]) {
    val context = ZMQ.context(1)
    val pushSocket = context.socket(ZMQ.PUSH)
    pushSocket.connect("tcp://localhost:5558")

    pushSocket send ("Luke".getBytes, 0)
  }
}
