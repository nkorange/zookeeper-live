import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "zookeeper-live"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    // Add your project dependencies here,
    "mysql" % "mysql-connector-java" % "5.1.18",
    "commons-lang" % "commons-lang" % "2.6",
    "org.apache.httpcomponents" % "httpclient" % "4.3.2",
    "org.apache.httpcomponents" % "httpasyncclient" % "4.0.2",
    "commons-collections" % "commons-collections" % "3.2.1",
    "redis.clients" % "jedis" % "2.5.1",
    "com.google.guava" % "guava" % "r08",
    ("org.apache.zookeeper" % "zookeeper" % "3.4.0")
    .exclude("com.sun.jmx", "jmxri")
    .exclude("javax.jms", "jms")
    .exclude("log4j", "log4j")
    .exclude("org.slf4j", "slf4j-log4j12")
    .exclude("com.sun.jdmk", "jmxtools"),
    "com.101tec" % "zkclient" % "0.3",
    javaCore,
    javaJdbc,
    javaEbean
  )

  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here      
  )

}
