val projectName = "learning-shapeless"

val shapeless               = "com.chuusai" %% "shapeless"                 % "2.3.10"
val scalazDerivingShapeless = "org.scalaz"  %% "scalaz-deriving-shapeless" % "3.0.0-M7"

val scalaTest          = "org.scalatest"     %% "scalatest"       % "3.2.16" % Test
val scalaCheck         = "org.scalacheck"    %% "scalacheck"      % "1.17.0" % Test
val scalaTestPlusCheck = "org.scalatestplus" %% "scalacheck-1-14" % "3.2.2.0"

val scala212               = "2.12.11"
val scala213               = "2.13.11"
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
      "UTF-8",                // source files are in UTF-8
      "-deprecation",         // warn about use of deprecated APIs
      "-unchecked",           // warn about unchecked type parameters
      "-feature",             // warn about misused language features
      "-language:higherKinds" // suppress warnings when using higher kinded types
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
      import shapeless.syntax.std.tuple._
      import shapeless.poly._
      println
      """.stripMargin // initialize REPL
  )
)

lazy val root = (project in file("."))
  .aggregate(gurnell, wiki, examples, rowparser)
  .settings(
    name := projectName
  )

lazy val gurnell = (project in file("gurnell"))
  .dependsOn(util)
  .settings(
    name := "gurnell",
    description := "Code from Dave Gurnells book: The Type Astronaut's Guide to Shapeless",
    libraryDependencies ++= Seq(
      scalaTest,
      scalaCheck,
      scalaTestPlusCheck
    )
  )

lazy val wiki = (project in file("shapeless-wiki"))
  .dependsOn(util)
  .settings(
    name := "shapeless-wiki",
    description := "Code snippets from the Shapeless wiki: https://github.com/milessabin/shapeless/wiki/Feature-overview:-shapeless-2.0.0"
  )

lazy val examples = (project in file("shapeless-examples"))
  .dependsOn(util)
  .settings(
    name := "shapeless-examples",
    description := "Shapeless ode examples (copied from): https://github.com/milessabin/shapeless/tree/master/examples/src/main/scala/shapeless/examples",
    libraryDependencies ++= Seq(
      "org.scala-lang"         % "scala-compiler"            % scalaVersion.value,
      "org.scala-lang"         % "scala-reflect"             % scalaVersion.value,
      "org.scala-lang.modules" %% "scala-parser-combinators" % "2.3.0",
      "com.novocode"           % "junit-interface"           % "0.11" % Test
    ),
    scalacOptions += "-language:implicitConversions"
  )

lazy val rowparser = (project in file("rowparser"))
// .dependsOn(util)
  .settings(
    name := "rowparser",
    description := "RowParser code examples (copied from): https://meta.plasm.us/posts/2015/11/08/type-classes-and-generic-derivation/"
  )

lazy val util = (project in file("util"))
  .settings(
    name := "util",
    description := "Utilities"
  )

addCommandAlias("grm", "gurnell/runMain")
addCommandAlias("wrm", "wiki/runMain")
addCommandAlias("erm", "examples/runMain")
