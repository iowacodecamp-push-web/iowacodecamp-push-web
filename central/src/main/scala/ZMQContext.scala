package code.central

import org.zeromq.ZMQ

trait ZMQContext {
  lazy val context = ZMQ.context(1)
}

