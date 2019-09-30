package chap03

import shapeless.{:+:, ::, CNil, Coproduct, Generic, HList, HNil, Inl, Inr}

import util._

object Chap033DerivingInstancesForCoproductsGeneric extends App {

  // ----------------------------------------
  prtTitle("3.3 Deriving instances for coproducts (generic instances)")

  // The type class: a trait with at least one type parameter:
  // Turn a value of type A into a row of cells in a CSV file:
  trait CsvEncoder[A] {
    def encode(value: A): List[String]
  }

  def writeCsv[A](values: List[A])(implicit encoder: CsvEncoder[A]): String =
    values
      .map { value =>
        encoder.encode(value).mkString(",")
      }
      .mkString("\n")

  object CsvEncoder {

    // "Summoner" method
    def apply[A](implicit encoder: CsvEncoder[A]): CsvEncoder[A] = encoder

    // "Constructor" method
    def instance[A](func: A => List[String]): CsvEncoder[A] =
      new CsvEncoder[A] {
        def encode(value: A): List[String] = func(value)
      }

    // Globally visible type class instances ...

    // for primitive types
    implicit val stringEncoder: CsvEncoder[String] =
      instance(str => List(str))

    implicit val intEncoder: CsvEncoder[Int] =
      instance(num => List(num.toString))

    implicit val booleanEncoder: CsvEncoder[Boolean] =
      instance(bool => List(if (bool) "yes" else "no"))

    implicit val doubleEncoder: CsvEncoder[Double] =
      CsvEncoder.instance(d => List(d.toString))

    // for Products / HLists
    implicit val hnilEncoder: CsvEncoder[HNil] =
      instance(hnil => Nil)

    implicit def hlistEncoder[H, T <: HList](
        implicit hEncoder: CsvEncoder[H],
        tEncoder: CsvEncoder[T]
    ): CsvEncoder[H :: T] =
      instance {
        case h :: t =>
          hEncoder.encode(h) ++ tEncoder.encode(t)
      }

    // for Coproducts
    implicit val cnilEncoder: CsvEncoder[CNil] =
      CsvEncoder.instance(cnil => throw new Exception("Inconceivable!"))

    implicit def coproductEncoder[H, T <: Coproduct](
        implicit
        hEncoder: CsvEncoder[H],
        tEncoder: CsvEncoder[T]
    ): CsvEncoder[H :+: T] = CsvEncoder.instance {
      case Inl(h) => hEncoder.encode(h)
      case Inr(t) => tEncoder.encode(t)
    }

    // generic CsvEncoder simplified using the Aux pattern
    implicit def genericEncoder[A, R](
        implicit
        gen: Generic.Aux[A, R],
        encoder: CsvEncoder[R]
    ): CsvEncoder[A] =
      CsvEncoder.instance(a => encoder.encode(gen.to(a)))
  }

  sealed trait Shape
  final case class Rectangle(width: Double, height: Double) extends Shape
  final case class Circle(radius: Double)                   extends Shape

  val shapes: List[Shape] = List(
    Rectangle(3.0, 4.0),
    Circle(1.0)
  )

  println(writeCsv(shapes))

  prtLine()
}
