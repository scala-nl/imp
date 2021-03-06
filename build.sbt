import ReleaseTransformations._

lazy val impSettings = Seq(
  organization := "org.spire-math",
  scalaVersion := "2.11.8",
  crossScalaVersions := Seq("2.10.6", "2.11.8", "2.12.0"),
  libraryDependencies ++= Seq(
    "org.scala-lang" % "scala-reflect" % scalaVersion.value % "provided",
    "org.scalatest" %%% "scalatest" % "3.0.0" % "test"
  ),
  scalacOptions ++= Seq(
    "-deprecation",
    "-unchecked",
    "-feature"
  ),
  licenses += ("MIT", url("http://opensource.org/licenses/MIT")),
  homepage := Some(url("http://github.com/non/imp")),

  // release stuff
  releaseCrossBuild := true,
  releasePublishArtifactsAction := PgpKeys.publishSigned.value,
  publishMavenStyle := true,
  publishArtifact in Test := false,
  pomIncludeRepository := Function.const(false),
  publishTo <<= (version).apply { v =>
    val nexus = "https://oss.sonatype.org/"
    if (v.trim.endsWith("SNAPSHOT"))
      Some("Snapshots" at nexus + "content/repositories/snapshots")
    else
      Some("Releases" at nexus + "service/local/staging/deploy/maven2")
  },
  pomExtra := (
    <scm>
      <url>git@github.com:non/imp.git</url>
      <connection>scm:git:git@github.com:non/imp.git</connection>
    </scm>
    <developers>
      <developer>
        <id>d_m</id>
        <name>Erik Osheim</name>
        <url>http://github.com/non/</url>
      </developer>
    </developers>
  ),
  releaseProcess := Seq[ReleaseStep](
    checkSnapshotDependencies,
    inquireVersions,
    runClean,
    ReleaseStep(action = Command.process("+test", _)),
    setReleaseVersion,
    commitReleaseVersion,
    tagRelease,
    publishArtifacts,
    setNextVersion,
    commitNextVersion,
    ReleaseStep(action = Command.process("sonatypeReleaseAll", _)),
    pushChanges))

lazy val noPublish = Seq(
  publish := {},
  publishLocal := {},
  publishArtifact := false)

lazy val root = project.in(file("."))
  .aggregate(impJS, impJVM)
  .settings(name := "imp-root")
  .settings(impSettings: _*)
  .settings(noPublish: _*)

lazy val imp = crossProject.in(file("."))
  .settings(name := "imp")
  .settings(impSettings: _*)

lazy val impJVM = imp.jvm
lazy val impJS = imp.js
