addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.14.5")
addSbtPlugin("com.typesafe.sbt" % "sbt-native-packager" % "1.2.2")
addSbtPlugin("io.spray" % "sbt-revolver" % "0.9.0")
addSbtPlugin("se.marcuslonnberg" % "sbt-docker" % "1.4.1")
addSbtPlugin("com.eed3si9n" % "sbt-buildinfo" % "0.7.0")
addSbtPlugin("org.scala-js" % "sbt-scalajs" % "0.6.20")

unmanagedSourceDirectories in Compile += {
  baseDirectory.value.getParentFile / "sbt-shared"
}

// addSbtPlugin("ch.epfl.scala" % "sbt-scalafix" % "0.5.0-RC2")

addSbtPlugin("ch.epfl.scala" % "sbt-scalajs-bundler" % "0.9.0")

addSbtPlugin("io.get-coursier" % "sbt-coursier" % "1.0.0-RC12")