!SLIDE
# Pushing the Web Around #

!SLIDE smbullets
# Overall idea
- we have easy-to-use technologies for making web apps more real-time & collaborative
- improves UX, makes new product ideas possible
- push events to users as they happen, no page reloads, no polling
 
!SLIDE smbullets
# Example apps
- Facebook chat
- Facebook stream (n new updates...?)
- Twitter web ui (n new tweets)
- Instagram demo http://demo.instagram.com
- Meetup rsvp api (is this live or demoable?)
- Color mobile app (assume they do some streaming api to get photos/ppl near you?)
- Google Wave, Novell Vibe
- Google Docs (how Luke & I wrote our submission abstract)
- Tweetdeck using Twitter Streaming API
 
!SLIDE smbullets
# Example APIs
- Twitter stream
- Meetup rsvp stream
- Instagram photo stream

!SLIDE smbullets
# Background on real-time web?
- http://www.readwriteweb.com/archives/explaining_the_real-time_web_in_100_words_or_less.php
- "The Real-Time Web is a paradigm based on pushing information to users as soon as it's available - instead of requiring that they or their software check a source periodically for updates. It can be enabled in many different ways and can require a different technical architecture. It's being implemented in social networking, search, news and elsewhere - making those experiences more like Instant Messaging and facilitating unpredictable innovations. Early benefits include increased user engagement ("flow") and decreased server loads, but these are early days. Real-time information delivery will likely become ubiquitous, a requirement for almost any website or service."
- "real-time web" is not "real-time computing" or "real-time OS" or...

!SLIDE smbullets
# General approach & specific technologies:
- User is using a web-connected application
- Desktop or mobile web app (ie browser)
- Any app that can use streaming web API
- events from user to system: ajax
- events from system to user: comet or websockets or long http response or pubsubhubbub
- events between system components: 0mq
- events within a system component: actors/akka/lift

!SLIDE full-page
![blah](ICCArchitecture1a.png)

!SLIDE full-page
![blah](ICCArchitecture2a.png)

!SLIDE full-page
![blah](ICCArchitecture3a.png)

!SLIDE full-page
![blah](ICCArchitecture4a.png)

!SLIDE smbullets
# WTF is X?
- What is ajax?
- What is comet?
- What is long http response?
- What is 0mq?
- What is akka?
- What is lift?

!SLIDE smbullets
# Other specific technologies to reference:
- websockets?
- node.js?
- mobile push?

!SLIDE smbullets
# Example app: nearby users
- Low-level UserAt events
    - Browser sends UserAt event to Web (every n secs): jQuery ajax post to Lift rest handler
    - Web sends UserAt event to Central: 0mq push socket sends msg to 0mq pull socket
    - Central sends UserAt events to all Webs: 0mq pub socket sends msg to 0mq sub sockets
    - Web sends UserAt event to Browser: Lift comet calls JS function, adds markers to map
- Low-level UserGone events (same as low-level UserAt)
- High-level UserNearby events
    - Browser sends UserAt event to Web
    - Web sends UserAt event to Central
    - Central sends UserNearby events to all Webs
    - Web filters out UserNearby events not relevant to signed-in user
    - Web adds newly nearby users to Browser: Lift comet calls jQuery functions to add & fade in <li>
- High-level UserNotNearby events
    - On UserGone, Central sends UserNoLongerNearby for all relevant users to all Webs
    - Web removes user from list in Browser: Lift comet calls jQuery functions to fade out & remove <li>
   
Include API in the above...

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

