!SLIDE
# Pushing the Web Around 

!SLIDE bullets
# Overall
- we have easy-to-use technologies for making web apps more real-time & collaborative
- improves UX, makes new product ideas possible
- push events to users as they happen, no page reloads, no polling
 
!SLIDE smbullets incremental
# Example apps
- Facebook chat
- Facebook stream
- Twitter web ui (n new tweets)
- Instagram PubSubHubBub
- Meetup RSVP Stream
- Google Wave, Novell Vibe
- Google Docs (how we wrote our submission abstract)
- Tweetdeck using Twitter Streaming API
 
!SLIDE
# Example APIs

!SLIDE small
    curl -u IowaCodeCamp1:iowacodecamp -i \ 
    http://stream.twitter.com/1/statuses/sample.json

!SLIDE 
    curl -i http://stream.meetup.com/2/rsvps

!SLIDE smbullets
# Instagram Real Time
- [Demo](http://demo.instagram.com/)

!SLIDE smbullets
# Background on real-time web?
- [In a hundred words (Read Write Web)](http://www.readwriteweb.com/archives/explaining_the_real-time_web_in_100_words_or_less.php)
- "The Real-Time Web is a paradigm based on pushing information to users as soon as it's available - instead of requiring that they or their software check a source periodically for updates. It can be enabled in many different ways and can require a different technical architecture. It's being implemented in social networking, search, news and elsewhere - making those experiences more like Instant Messaging and facilitating unpredictable innovations. Early benefits include increased user engagement ("flow") and decreased server loads, but these are early days. Real-time information delivery will likely become ubiquitous, a requirement for almost any website or service."
- "real-time web" is not "real-time computing" or "real-time OS" or...

!SLIDE smbullets incremental
# General approach & specific technologies:
- User is using a web-connected application
- Desktop or mobile web app (ie browser)
- Any app that can use streaming web API
- events from user to system: ajax or websockets
- events from system to user: comet or websockets or long http response or pubsubhubbub
- events between system components: 0mq or PubSubHubBub or amqp or XMPP or JMS
- events within a system component: akka/lift

!SLIDE smbullets incremental
# WTF is X?
- What is ajax?
- What is comet?
- What is long http response?
- What is 0mq?
- What is akka?
- What is lift?

!SLIDE smbullets incremental
# Other specific technologies to reference:
- websockets
- XMPP / JMS
- [node.js](http://nodejs.org/)
- [netty](http://www.jboss.org/netty/)
- [pusher](http://pusher.com/)
- mobile push

!SLIDE smbullets
# Example app: nearby users

!SLIDE
![blah](ICCArchitecture1a.png)

!SLIDE
![blah](ICCArchitecture2a.png)

!SLIDE
![blah](ICCArchitecture3a.png)

!SLIDE
![blah](ICCArchitecture4a.png)

!SLIDE smbullets incremental
# Low-level UserAt events
- Browser sends UserAt event to Web (every n secs): jQuery ajax post to Lift rest handler
- Web sends UserAt event to Central: 0mq push socket sends msg to 0mq pull socket
- Central sends UserAt events to all Webs: 0mq pub socket sends msg to 0mq sub sockets
- Web sends UserAt event to Browser: Lift comet calls JS function, adds markers to map

!SLIDE
# Low-level UserGone events (same as low-level UserAt)

!SLIDE small
    @@@ javascript
    jQuery(window).ready(function() {
        if (navigator.geolocation) {
       	    navigator.geolocation.watchPosition(onLocation, 
                                                onError);
        } else {
       	    alert("Sorry, no navigator");
        }
    });
    
!SLIDE small     
    @@@ javascript
    function onLocation(position) {
        latitude = position.coords.latitude;
        longitude = position.coords.longitude;
        sendLocation();
        if (!scheduled) {
             setInterval("sendLocation()", delay);
             scheduled = true;
        }
    }

!SLIDE small
    @@@ scala
    object RestApi extends RestHelper {
      serve {
        case Post(List("location"), _) =>
          for {
            user <- LiftUser.signedIn
            lat <- S param "latitude" map { _.toDouble }
            lng <- S param "longitude" map { _.toDouble }
          } {
            user.location = Full(Location(lat, lng))
            CentralPush ! UserAt(User(user.username), 
                                 Location(lat, lng))
          }
          OkResponse()
      }
    }

!SLIDE small
    @@@ scala
    object CentralPush 
    extends Push(Props.get("centralPushEndpoint", 
                           "tcp://localhost:5558")) 
    with Connect

    
!SLIDE small
    @@@ scala
    case class UserAt(user: User, location: Location)
    
    case class UserGone(user: User)
    
    case class UserNearby(target: User, whoNearby: UserAt)
    
    case class UserNoLongerNearby(target: User, 
                                  whoLeft: UserGone)
                                  
!SLIDE smaller
    @@@ scala
    class ZMQSocketMessageReceiver(port: Int) 
    extends Actor with ZMQContext with Listeners {
      import ProtocolDeserialization._
      import ZMQMultipart._
      
      lazy val pullSocket = {
        val pullSocket = context.socket(ZMQ.PULL)
        pullSocket.bind("tcp://*:" + port)
        pullSocket
      }
    
      def receive = listenerManagement orElse {
        case ReceiveMessage =>
          val message = ((blockingReadTwoPartMessage _) 
                         andThen 
                         (deserializeMessage _))(pullSocket)
          gossip(message)
          self ! ReceiveMessage
      }
    }
                                      

!SLIDE small
    @@@ scala
    class CentralBroadcastReceiver(centralPublisher: ActorRef) 
    extends Actor {
      def receive = {
        case msg @ UserAt(user, location) =>
          log.info("User " + user + " is at: " + location)
          centralPublisher forward msg
        case msg @ UserGone(who) =>
          log.info(who + " has left")
          centralPublisher forward msg
        case msg => log.info("ignoring message " + msg)
      }
    }

!SLIDE smaller
    @@@ scala
    class ZMQSocketBroadcastPublisher(val port: Int) 
    extends Actor with ZMQContext with ZMQPubSocket {
    
      import ProtocolSerialization._
      import ZMQMultipart._
      
      def receive = {
        case msg @ UserAt(user, location) =>
          log.info("User " + user + " is at: " + location)
          writeTwoPartMessage(serializeToMessage(msg), 
                              pubSocket)
        case msg @ UserGone(who) =>
          log.info(who + " has left")
          writeTwoPartMessage(serializeToMessage(msg), 
                              pubSocket)
      }
    }

!SLIDE smaller
    @@@ scala
    class NearbyUsers extends CometActor with Logger {
      val subscribe = new FilterableSubscribe(Props.get("centralNearbySubEndpoint", 
                                                        "tcp://localhost:5560"), 
                                                        this, Set(key))
    
      def render = NodeSeq.Empty
      
      override def lowPriority = {
        case UserNearby(_, UserAt(other, _)) => 
          if (!(users contains other)) {
            users += other
            debug(other + " is now nearby")
            partialUpdate(PrependAndFade(containerId, 
                                         render(other), 
                                         id(other)))
          }
        case UserNoLongerNearby(_, UserGone(other)) => 
          if (users contains other) {
            users -= other
            debug(other + " is no longer nearby")
            partialUpdate(FadeAndRemove(id(other)))
          }
      }
    }
                

!SLIDE smbullets
# High-level UserNearby events
- Browser sends UserAt event to Web
- Web sends UserAt event to Central
- Central sends UserNearby events to all Webs
- Web filters out UserNearby events not relevant to signed-in user
- Web adds newly nearby users to Browser: Lift comet calls jQuery functions to add & fade in <li>

!SLIDE smbullets
# High-level UserNotNearby events
    - On UserGone, Central sends UserNoLongerNearby for all relevant users to all Webs
    - Web removes user from list in Browser: Lift comet calls jQuery functions to fade out & remove <li>
    
!SLIDE smbullets
# Streaming API    
   
!SLIDE smbullets
# What else could you build?
- Chat built on nearby users app: chat rooms form for nearby users
- Geo events on nearby users app: tweets, instagram photos, 4sq checkins, Facebook...
- Zaarly
   - I'll pay $_ for _ in next _ hours within _ miles
   - Their demo video shows SMS for event delivery, no provider UI shown (lame)
   - Mobile web & apps for pushing events between customers & providers
- Auction site: real-time aunction updates, no page reloads
- Collaboration software: multiple people editing same UI at same time
- Pongr: new photos, likes, notifications, comments, trends...

!SLIDE
- Any questions?
