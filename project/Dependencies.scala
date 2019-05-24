import sbt._

object Dependencies {

  object Versions {

    val shapelessVersion = "2.3.3"
  }

  object Libraries {

    import Versions._

    lazy val shapeless = "com.chuusai" %% "shapeless" % shapelessVersion withSources() withJavadoc()
  }
}
