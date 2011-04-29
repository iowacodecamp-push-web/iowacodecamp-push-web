package code.api

import unfiltered.netty._
import unfiltered.request._
import unfiltered.response.{ ResponseString, Ok }

class ApiPlan extends channel.Plan {
  import Streams._
  
  def intent = {
    // GET /all
    case req @ GET(Path("/all")) =>
      allStream ! AddReq(req)
    
    // POST /:user/location
    // GET /:user/nearby_users (use actor registry to find actor and start one if necessary)
  }
}

object Api {
  def main(args: Array[String]) {
    Subs.centralSub ! ReceiveMessage
    
    val port = args.headOption.map(_.toInt).getOrElse(7979)
    Http(port).handler(new ApiPlan).run()
  }
}

