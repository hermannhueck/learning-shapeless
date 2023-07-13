// see: https://meta.plasm.us/posts/2015/11/08/type-classes-and-generic-derivation/

import model._

import scala.util.Try

trait SaferRowParser[A] {
  def apply(s: String): Option[A]
}

val personParser: SaferRowParser[Person] = new SaferRowParser[Person] {
  def apply(s: String): Option[Person] = s.split(",").toList match {
    case List(name, age) => Try(age.toDouble).map(Person(name, _)).toOption
    case _               => None
  }
}

val bookParser: SaferRowParser[Book] = new SaferRowParser[Book] {
  def apply(s: String): Option[Book] = s.split(",").toList match {
    case List(title, author, year) =>
      Try(year.toInt).map(Book(title, author, _)).toOption
    case _ => None
  }
}

val countryParser: SaferRowParser[Country] = new SaferRowParser[Country] {
  def apply(s: String): Option[Country] = s.split(",").toList match {
    case List(name, population, area) =>
      Try(population.toInt).flatMap { pop => Try(area.toDouble).map(Country(name, pop, _)) }.toOption
    case _ => None
  }
}

personParser("Amy,54.2")

bookParser("Hamlet,Shakespeare,1600")

bookParser("Hamlet,Shakespeare")
