// see: https://meta.plasm.us/posts/2015/11/08/type-classes-and-generic-derivation/

import model._

trait Parser[A] {
  def apply(s: String): Option[A]
}

object Parser {
  def apply[A: Parser](s: String): Option[A] = implicitly[Parser[A]].apply(s)
}

import scala.util.Try

implicit val stringParser: Parser[String] = s => Some(s)
implicit val intParser: Parser[Int]       = s => Try(s.toInt).toOption
implicit val doubleParser: Parser[Double] = s => Try(s.toDouble).toOption

import shapeless._

implicit val hnilParser: Parser[HNil] = s => if (s.isEmpty) Some(HNil) else None

@annotation.nowarn("msg=match may not be exhaustive")
implicit def hconsParser[H: Parser, T <: HList: Parser]: Parser[H :: T] =
  _.split(",").toList match {
    case cell +: rest =>
      for {
        head <- implicitly[Parser[H]].apply(cell)
        tail <- implicitly[Parser[T]].apply(rest.mkString(","))
      } yield head :: tail
  }

implicit def caseClassParser[A, R <: HList](
    implicit
    gen: Generic.Aux[A, R],
    reprParser: Lazy[Parser[R]]
): Parser[A] =
  s =>
    reprParser
      .value
      .apply(s)
      .map(gen.from)

Parser[Person]("Amy,54.2")

Parser[Person]("Fred,23")

Parser[Book]("Hamlet,Shakespeare,1600")

Parser[Country]("Finland,4500000,338424")

Parser[Book]("Hamlet,Shakespeare")

trait Foo

// Parser[Foo]("Hamlet,Shakespeare")
// >> error: could not find implicit value for parameter parser: Parser[Foo]
//        Parser[Foo]("Hamlet,Shakespeare")
