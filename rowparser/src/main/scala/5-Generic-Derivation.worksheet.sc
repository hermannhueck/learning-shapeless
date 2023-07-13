// see: https://meta.plasm.us/posts/2015/11/08/type-classes-and-generic-derivation/

import model._

trait Parser[A] {
  def apply(s: String): Option[A]
}

object Parser {
  def apply[A](s: String)(implicit parser: Parser[A]): Option[A] = parser(s)
}

import scala.util.Try

implicit val stringParser: Parser[String] = new Parser[String] {
  def apply(s: String): Option[String] = Some(s)
}

implicit val intParser: Parser[Int] = new Parser[Int] {
  def apply(s: String): Option[Int] = Try(s.toInt).toOption
}

implicit val doubleParser: Parser[Double] = new Parser[Double] {
  def apply(s: String): Option[Double] = Try(s.toDouble).toOption
}

import shapeless._

implicit val hnilParser: Parser[HNil] = new Parser[HNil] {
  def apply(s: String): Option[HNil] = if (s.isEmpty) Some(HNil) else None
}

implicit def hconsParser[H: Parser, T <: HList: Parser]: Parser[H :: T] = new Parser[H :: T] {
  @annotation.nowarn("msg=match may not be exhaustive")
  def apply(s: String): Option[H :: T] = s.split(",").toList match {
    case cell +: rest =>
      for {
        head <- implicitly[Parser[H]].apply(cell)
        tail <- implicitly[Parser[T]].apply(rest.mkString(","))
      } yield head :: tail
  }
}

implicit def caseClassParser[A, R <: HList](
    implicit
    gen: Generic[A] { type Repr = R },
    reprParser: Parser[R]
): Parser[A] = new Parser[A] {
  def apply(s: String): Option[A] = reprParser.apply(s).map(gen.from)
}

Parser[Person]("Amy,54.2")

Parser[Person]("Fred,23")

Parser[Book]("Hamlet,Shakespeare,1600")

Parser[Country]("Finland,4500000,338424")

Parser[Book]("Hamlet,Shakespeare")

trait Foo

// Parser[Foo]("Hamlet,Shakespeare")
// >> error: could not find implicit value for parameter parser: Parser[Foo]
//        Parser[Foo]("Hamlet,Shakespeare")
