// see: https://meta.plasm.us/posts/2015/11/08/type-classes-and-generic-derivation/

import model._

import scala.util.Try

trait SaferRowParser[A] {
  def apply(s: String): Option[A]
}

object SaferRowParser {
  def apply[A](s: String)(implicit parser: SaferRowParser[A]): Option[A] = parser(s)

  implicit val personParser: SaferRowParser[Person] = new SaferRowParser[Person] {
    def apply(s: String): Option[Person] = s.split(",").toList match {
      case List(name, age) => Try(age.toDouble).map(Person(name, _)).toOption
      case _               => None
    }
  }

  implicit val bookParser: SaferRowParser[Book] = new SaferRowParser[Book] {
    def apply(s: String): Option[Book] = s.split(",").toList match {
      case List(title, author, year) =>
        Try(year.toInt).map(Book(title, author, _)).toOption
      case _ => None
    }
  }

  implicit val countryParser: SaferRowParser[Country] = new SaferRowParser[Country] {
    def apply(s: String): Option[Country] = s.split(",").toList match {
      case List(name, population, area) =>
        Try(population.toInt).flatMap { pop => Try(area.toDouble).map(Country(name, pop, _)) }.toOption
      case _ => None
    }
  }
}

SaferRowParser[Person]("Amy,54.2")

SaferRowParser[Book]("Hamlet,Shakespeare,1600")

SaferRowParser[Book]("Hamlet,Shakespeare")

trait Foo

// SaferRowParser[Foo]("Hamlet,Shakespeare,1600")
// >> could not find implicit value for parameter parser: SaferRowParser[Foo]
