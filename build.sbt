val projectName = "learning-shapeless"

val shapeless = "com.chuusai" %% "shapeless" % "2.3.3" withSources () withJavadoc ()
val scalazDerivingShapeless = "org.scalaz" %% "scalaz-deriving-shapeless" % "2.0.0-M1" withSources () withJavadoc ()

val scala212 = "2.12.10"
val scala213 = "2.13.1"
val supportedScalaVersions = List(scala212, scala213)

Global / onChangedBuildSource := ReloadOnSourceChanges

inThisBuild(
  Seq(
    scalaVersion := scala213,
    crossScalaVersions := supportedScalaVersions,
    version := "0.1.0",
    publish / skip := true,
    scalacOptions ++= Seq(
      "-encoding",
      "UTF-8", // source files are in UTF-8
      "-deprecation", // warn about use of deprecated APIs
      "-unchecked", // warn about unchecked type parameters
      "-feature" // warn about misused language features
      // "-language:higherKinds",  // suppress warnings when using higher kinded types
      // "-Ypartial-unification",  // (removed in scala 2.13) allow the compiler to unify type constructors of different arities
      // "-Xlint",                 // enable handy linter warnings
      // "-Xfatal-warnings",       // turn compiler warnings into errors
    ),
    libraryDependencies ++= Seq(
      shapeless
    ),
    initialCommands := s"""
      import shapeless._
      import shapeless.labelled._
      import shapeless.ops._
      import shapeless.syntax._
      import shapeless.syntax.singleton._
      println
      """.stripMargin // initialize REPL
  )
)

lazy val root = (project in file("."))
  .aggregate(gurnell, wiki)
  .settings(
    name := projectName
  )

lazy val gurnell = (project in file("gurnell"))
  .dependsOn(util)
  .settings(
    name := "gurnell",
    description := "Code from Dave Gurnells book: The Type Astronaut's Guide to Shapeless"
  )

lazy val wiki = (project in file("shapeless-wiki"))
  .dependsOn(util)
  .settings(
    name := "shapeless-wiki",
    description := "Code snippets from the Shapeless wiki: https://github.com/milessabin/shapeless/wiki/Feature-overview:-shapeless-2.0.0",
    libraryDependencies ++= Seq(
      scalazDerivingShapeless
    )
  )

lazy val util = (project in file("util"))
  .settings(
    name := "util",
    description := "Utilities"
  )

addCommandAlias("grm", "gurnell/runMain")
addCommandAlias("wrm", "wiki/runMain")
