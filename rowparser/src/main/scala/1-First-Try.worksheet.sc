// see: https://meta.plasm.us/posts/2015/11/08/type-classes-and-generic-derivation/

import model._

trait RowParser[A] {
  def apply(s: String): A
}

val personParser: RowParser[Person] = new RowParser[Person] {
  @annotation.nowarn("msg=match may not be exhaustive")
  def apply(s: String): Person = s.split(",").toList match {
    case List(name, age) => Person(name, age.toDouble)
  }
}

val bookParser: RowParser[Book] = new RowParser[Book] {
  @annotation.nowarn("msg=match may not be exhaustive")
  def apply(s: String): Book = s.split(",").toList match {
    case List(title, author, year) => Book(title, author, year.toInt)
  }
}

personParser("Amy,54.2")

bookParser("Hamlet,Shakespeare,1600")

// bookParser("Hamlet,Shakespeare")
// >> scala.MatchError: List(Hamlet, Shakespeare) (of class scala.collection.immutable.$colon$colon
