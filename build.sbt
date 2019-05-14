import Dependencies._

name := "learning-shapeless"

lazy val root = (project in file(".")).
  settings(
    inThisBuild(List(
      
      version := "1.0.0",
      scalaVersion := "2.13.0-M5",
      
      addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.9"),
      
      scalacOptions ++= Seq(
        "-encoding", "UTF-8",     // source files are in UTF-8
        "-deprecation",           // warn about use of deprecated APIs
        "-unchecked",             // warn about unchecked type parameters
        "-feature",               // warn about misused language features
        "-language:higherKinds",  // suppress warnings when using higher kinded types
        //"-Ypartial-unification",  // allow the compiler to unify type constructors of different arities
        //"-Xlint",                 // enable handy linter warnings
        //"-Xfatal-warnings",        // turn compiler warnings into errors
      ),
      
      libraryDependencies ++= Seq(
        Libraries.shapeless,
      ),
      
      initialCommands := s"""
        import shapeless._
        import scala.language.higherKinds
      """ // initialize REPL
    ))
  )

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

