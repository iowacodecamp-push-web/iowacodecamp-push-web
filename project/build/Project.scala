import sbt._

class Project(info: ProjectInfo) extends ParentProject(info) {
  lazy val protocol = project("protocol", "Protocol", new ProtocolProject(_)) 
  lazy val web      = project("web", "Web", new WebProject(_)) 
  lazy val central  = project("central", "Central", new CentralProject(_)) 
  lazy val api      = project("api", "API", new ApiProject(_))
  
  class ProtocolProject(info: ProjectInfo) extends DefaultProject(info)

  class WebProject(info: ProjectInfo) extends DefaultWebProject(info) {
    //JRebel & html/css changes without restarts
    override def jettyWebappPath = webappPath
    override def scanDirectories = Nil

    val protocolDep = protocol
    
    val liftVersion = "2.3"
    override def libraryDependencies = Set(
      "net.liftweb" %% "lift-webkit" % liftVersion % "compile",
      "net.liftweb" %% "lift-mapper" % liftVersion % "compile",
      "org.mortbay.jetty" % "jetty" % "6.1.22" % "test",
      "junit" % "junit" % "4.5" % "test",
      "ch.qos.logback" % "logback-classic" % "0.9.26",
      "org.scala-tools.testing" %% "specs" % "1.6.6" % "test",
      "com.h2database" % "h2" % "1.2.138"
    ) ++ super.libraryDependencies
  }
  
  class CentralProject(info: ProjectInfo) extends DefaultProject(info) with AkkaProject {
    val protocolDep = protocol    
  }
  
  class ApiProject(info: ProjectInfo) extends DefaultProject(info) with AkkaProject {
    val protocolDep = protocol
    
    lazy val unfilteredNetty = "net.databinder" %% "unfiltered-netty" % "0.3.2"
    lazy val sjson = "net.debasishg" %% "sjson" % "0.10"

    lazy val jboss = "jboss repo" at  "http://repository.jboss.org/nexus/content/groups/public-jboss/"
  }
}

