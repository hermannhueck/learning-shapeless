import sbt.url

val shapeless = "com.chuusai" %% "shapeless" % "2.3.3" withSources() withJavadoc()
val scalazDerivingShapeless = "org.scalaz" %% "scalaz-deriving-shapeless" % "2.0.0-M1" withSources() withJavadoc()

val projectName = "learning-shapeless"

inThisBuild(
    version := "0.1.0",
    scalaVersion := "2.13.0",
    turbo := true,
    onChangedBuildSource := ReloadOnSourceChanges,

    scalacOptions ++= Seq(
      "-encoding", "UTF-8", // source files are in UTF-8
      "-deprecation", // warn about use of deprecated APIs
      "-unchecked", // warn about unchecked type parameters
      "-feature", // warn about misused language features
      //"-language:higherKinds",  // suppress warnings when using higher kinded types
      //"-Ypartial-unification",  // (removed in scala 2.13) allow the compiler to unify type constructors of different arities
      //"-Xlint",                 // enable handy linter warnings
      //"-Xfatal-warnings",       // turn compiler warnings into errors
    ),

    libraryDependencies ++= Seq(
      shapeless,
    ),

    initialCommands := s"""
        import shapeless._
        import shapeless.labelled._
        import shapeless.ops._
        import shapeless.syntax._
        println
        """.stripMargin // initialize REPL
  )
)

lazy val root = (project in file("."))
  .aggregate(gurnell, wiki)
  .settings(
    name := projectName,
  )

lazy val gurnell = (project in file("gurnell"))
  .settings(
    name := "gurnell",
    description := "Code from Dave Gurnells book: The Type Astronaut's Guide to Shapeless",
  )

lazy val wiki = (project in file("shapeless-wiki"))
  .settings(
    name := "shapeless-wiki",
    description := "Code snippets from the Shapeless wiki: https://github.com/milessabin/shapeless/wiki/Feature-overview:-shapeless-2.0.0",
    libraryDependencies ++= Seq(
      scalazDerivingShapeless,
    ),
  )

addCommandAlias("grm", "gurnell/runMain")
addCommandAlias("wrm", "wiki/runMain")

/*
libraryDependencies += {
  "com.lihaoyi" % "ammonite" % "1.6.3" % "test" cross CrossVersion.full
}

sourceGenerators in Test += Def.task {
  val file = (sourceManaged in Test).value / "amm.scala"
  IO.write(file, """object amm extends App { ammonite.Main.main(args) }""")
  Seq(file)
}.taskValue

// Optional, required for the `source` command to work
(fullClasspath in Test) ++= {
  (updateClassifiers in Test).value
    .configurations
    .find(_.configuration == Test.name)
    .get
    .modules
    .flatMap(_.artifacts)
    .collect{case (a, f) if a.classifier == Some("sources") => f}
}

addCommandAlias("amm", s"test:runMain amm --predef ammonite-init.sc")
*/

