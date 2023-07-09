import chap03.Chap032aDerivingInstancesForProductsSpecific.CsvEncoder
import shapeless.Generic.Aux
import shapeless.{:+:, ::, CNil, Coproduct, Generic, HList, HNil, Inl, Inr, Lazy}

// ===== 3.4 Deriving instance for recursive types")

// The type class: a trait with at least one type parameter:
// Turn a value of type A into a row of cells in a CSV file:
trait CsvEncoder[A] {
  def encode(value: A): List[String]
}

def writeCsv[A](values: List[A])(implicit encoder: CsvEncoder[A]): String =
  values
    .map { value => encoder.encode(value).mkString(",") }
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
    instance(d => List(d.toString))

  // for Products / HLists
  implicit val hnilEncoder: CsvEncoder[HNil] =
    instance(hnil => Nil)

  // needs Lazy for recursive structures like Tree
  implicit def hlistEncoder[H, T <: HList](
      implicit
      hEncoder: Lazy[CsvEncoder[H]], // wrap in Lazy
      tEncoder: CsvEncoder[T]
  ): CsvEncoder[H :: T] = instance {
    case h :: t =>
      hEncoder.value.encode(h) ++ tEncoder.encode(t)
  }

  // for Coproducts
  implicit val cnilEncoder: CsvEncoder[CNil] =
    instance(cnil => throw new Exception("Inconceivable!"))

  // needs Lazy for recursive structures like Tree
  implicit def coproductEncoder[H, T <: Coproduct](
      implicit
      hEncoder: Lazy[CsvEncoder[H]], // wrap in Lazy
      tEncoder: CsvEncoder[T]
  ): CsvEncoder[H :+: T] = instance {
    case Inl(h) => hEncoder.value.encode(h)
    case Inr(t) => tEncoder.encode(t)
  }

  // generic CsvEncoder simplified using the Aux pattern
  // needs Lazy for recursive structures like Tree
  implicit def genericEncoder[A, R](
      implicit
      gen: Generic.Aux[A, R],
      rEncoder: Lazy[CsvEncoder[R]] // wrap in Lazy
  ): CsvEncoder[A] = instance { value => rEncoder.value.encode(gen.to(value)) }
}

sealed trait Tree[A]
case class Branch[A](left: Tree[A], right: Tree[A]) extends Tree[A]
case class Leaf[A](value: A)                        extends Tree[A]

// ------ 3.4.1 Implicit divergence without Lazy

// without Lazy:
// CsvEncoder[Tree[Int]]
// <console>:23: error: could not find implicit value for parameter encoder: CsvEncoder[Tree[Int]]
//        CsvEncoder[Tree[Int]]
//                  ^

// ------ 3.4.2 No implicit divergence with Lazy

val treeEncoder = CsvEncoder[Tree[Int]]

val intTree: Tree[Int] = Branch(Branch(Leaf(3), Leaf(7)), Leaf(5))

val encodedIntTree: String = writeCsv(List(intTree))
