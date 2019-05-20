import sbt._

object Dependencies {

  object Versions {

    val shapelessVersion = "2.3.3"
    val catsVersion = "2.0.0-M1"
  }

  object Libraries {

    import Versions._

    lazy val shapeless = "com.chuusai" %% "shapeless" % shapelessVersion withSources() withJavadoc()
    lazy val catsCore = "org.typelevel" %% "cats-core" % catsVersion withSources() withJavadoc()
  }
}
