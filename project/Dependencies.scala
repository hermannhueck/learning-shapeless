import sbt._

object Dependencies {

  object Versions {

    val shapelessVersion = "2.3.3"
    val scalazDerivingVersion = "1.0.0"
  }

  object Libraries {

    import Versions._

    lazy val shapeless = "com.chuusai" %% "shapeless" % shapelessVersion withSources() withJavadoc()
    lazy val scalazDerivingShapeless = "org.scalaz" %% "scalaz-deriving-shapeless" % scalazDerivingVersion withSources() withJavadoc()
  }
}
