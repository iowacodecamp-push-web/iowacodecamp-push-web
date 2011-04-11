import sbt._

class Project(info: ProjectInfo) extends ParentProject(info) {
  lazy val web      = project("web", "Web", new WebProject(_)) 
  lazy val protocol = project("protocol", "Protocol", new ProtocolProject(_)) 
  lazy val central  = project("central", "Central", new CentralProject(_)) 
  lazy val api      = project("api", "API", new ApiProject(_))

  class WebProject(info: ProjectInfo) extends DefaultWebProject(info)
  class ProtocolProject(info: ProjectInfo) extends DefaultProject(info)
  class CentralProject(info: ProjectInfo) extends DefaultProject(info)
  class ApiProject(info: ProjectInfo) extends DefaultProject(info)
}

