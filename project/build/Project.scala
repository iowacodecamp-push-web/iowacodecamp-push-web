import sbt._

class Project(info: ProjectInfo) extends ParentProject(info) {
  lazy val web      = project("web", "Web", new WebProject(_)) 
  lazy val protocol = project("protocol", "Protocol", new ProtocolProject(_)) 
  lazy val central  = project("central", "Central", new CentralProject(_)) 
  lazy val api      = project("api", "API", new ApiProject(_))

  class WebProject(info: ProjectInfo) extends DefaultWebProject(info) {
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
  class ProtocolProject(info: ProjectInfo) extends DefaultProject(info)
  class CentralProject(info: ProjectInfo) extends DefaultProject(info)
  class ApiProject(info: ProjectInfo) extends DefaultProject(info)
}

