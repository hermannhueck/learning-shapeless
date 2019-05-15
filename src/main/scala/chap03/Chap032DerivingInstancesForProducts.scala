package chap03

import shapeless.Generic.Aux
import shapeless.{::, Generic, HList, HNil}
import shapeless.{Coproduct, :+:, CNil, Inl, Inr}

object Chap032DerivingInstancesForProducts extends App {

  println("\n===== 3.2 Deriving instance for products =====")

  // The type class: a trait with at least one type parameter:
  // Turn a value of type A into a row of cells in a CSV file:
  trait CsvEncoder[A] {
    def encode(value: A): List[String]
  }

  object CsvEncoder {

    // "Summoner" method
    def apply[A](implicit encoder: CsvEncoder[A]): CsvEncoder[A] = encoder

    // "Constructor" method
    def instance[A](func: A => List[String]): CsvEncoder[A] = new CsvEncoder[A] {
      def encode(value: A): List[String] = func(value)
    }

    // Globally visible type class instances

    implicit val stringEncoder: CsvEncoder[String] =
      instance(str => List(str))

    implicit val intEncoder: CsvEncoder[Int] =
      instance(num => List(num.toString))

    implicit val booleanEncoder: CsvEncoder[Boolean] =
      instance(bool => List(if(bool) "yes" else "no"))

    implicit val hnilEncoder: CsvEncoder[HNil] =
      instance(hnil => Nil)

    implicit def hlistEncoder[H, T <: HList](implicit hEncoder: CsvEncoder[H], tEncoder: CsvEncoder[T]): CsvEncoder[H :: T] =
      instance { case h :: t =>
          hEncoder.encode(h) ++ tEncoder.encode(t)
      }

    // Taken together, these five instances allow us to summon CsvEncoders for any HList involving Strings, Ints, and Booleans.
  }

  def writeCsv[A](values: List[A])(implicit encoder: CsvEncoder[A]): String =
    values
      .map { value => encoder.encode(value).mkString(",") }
      .mkString("\n")


  println("\n----- 3.2.1 Instances for HLists -----")

  val reprEncoder: CsvEncoder[String :: Int :: Boolean :: HNil] = implicitly

  val encodedHList: List[String] = reprEncoder.encode("abc" :: 123 :: true :: HNil)
  // res9: List[String] = List(abc, 123, yes)
  println(encodedHList)


  println("\n----- 3.2.2 Instances for concrete products -----")

  case class IceCream(name: String, numCherries: Int, inCone: Boolean)

  implicit val iceCreamEncoder: CsvEncoder[IceCream] = {
    val gen: Aux[IceCream, String :: Int :: Boolean :: HNil] = Generic[IceCream]
    val enc: CsvEncoder[String :: Int :: Boolean :: HNil] = CsvEncoder[gen.Repr]
    CsvEncoder.instance(iceCream => enc.encode(gen.to(iceCream)))
  }

  val iceCreams: List[IceCream] = List(
    IceCream("Sundae", 1, false),
    IceCream("Cornetto", 0, true),
    IceCream("Banana Split", 0, false)
  )

  val encodedIceCreams: String = writeCsv(iceCreams)
  println(encodedIceCreams)


  println

  case class Employee(name: String, number: Int, manager: Boolean)

  implicit val employeeEncoder: CsvEncoder[Employee] = {
    val gen = Generic[Employee]
    val enc = CsvEncoder[gen.Repr]
    CsvEncoder.instance(employee => enc.encode(gen.to(employee)))
  }

  val employees: List[Employee] = List(
    Employee("Bill", 1, true),
    Employee("Peter", 2, false),
    Employee("Milton", 3, false)
  )

  val encodedEmployees: String = writeCsv(employees)
  println(encodedEmployees)



  println

  implicit def pairEncoder[A, B](implicit aEncoder: CsvEncoder[A], bEncoder: CsvEncoder[B]): CsvEncoder[(A, B)] =
    CsvEncoder.instance {
      case (a, b) => aEncoder.encode(a) ++ bEncoder.encode(b)
    }

  val pairs: List[(Employee, IceCream)] = employees zip iceCreams
  val encodedPairs: String = writeCsv(pairs)
  println(encodedPairs)


  println("\n----- Same thing in a generic way -----")

/*
  implicit def genericEncoder[A](
                                  implicit
                                  gen: Generic[A],
                                  enc: CsvEncoder[gen.Repr]
                                ): CsvEncoder[A] = CsvEncoder.instance(a => enc.encode(gen.to(a)))
  // <console>:24: error:
  // illegal dependent method type: parameter may only be referenced in a subsequent parameter section
  //          gen: Generic[A],
  //          ^
*/

/*
  // this CsvEncoder can handle any case class
  implicit def genericEncoder[A, R](
                                     implicit
                                     gen: Generic[A] { type Repr = R },
                                     encoder: CsvEncoder[R]
                                   ): CsvEncoder[A] =
    CsvEncoder.instance(a => encoder.encode(gen.to(a)))
*/

  // CsvEncoder simplified using the Aux pattern
  implicit def genericEncoder[A, R](
                                     implicit
                                     gen: Generic.Aux[A, R],
                                     encoder: CsvEncoder[R]
                                   ): CsvEncoder[A] =
    CsvEncoder.instance(a => encoder.encode(gen.to(a)))

  println(
    writeCsv(iceCreams)(
      genericEncoder(
        Generic[IceCream],
        CsvEncoder.hlistEncoder(CsvEncoder.stringEncoder,
          CsvEncoder.hlistEncoder(CsvEncoder.intEncoder,
            CsvEncoder.hlistEncoder(CsvEncoder.booleanEncoder,
              CsvEncoder.hnilEncoder)))))
  ) // is the same as:
  println
  println(writeCsv(iceCreams))
  println
  println(writeCsv(employees))
  println
  println(writeCsv(employees zip iceCreams))


  println("\n----- 3.2.3 So what are the downsides? -----")

  //
  class Foo(bar: String, baz: Int)
  // writeCsv(List(new Foo("abc", 123)))
  // <console>:26: error:
  // could not find implicit value for parameter encoder: CsvEncoder[Foo]
  //        writeCsv(List(new Foo("abc", 123)))
  //                ^

  // Foo should be a case class


  import java.util.Date
  case class Booking(room: String, date: Date)
  // writeCsv(List(Booking("Lecture hall", new Date())))
  // <console>:28: error:
  // could not find implicit value for parameter encoder: CsvEncoder[Booking]
  // writeCsv(List(Booking("Lecture hall", new Date())))
  //         ^

  // needs an implicit CsvEncoder for java.util.Date


  println("\n----- 3.3 Deriving instances for coproducts -----")

  sealed trait Shape
  final case class Rectangle(width: Double, height: Double) extends Shape
  final case class Circle(radius: Double) extends Shape

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

  implicit val doubleEncoder: CsvEncoder[Double] =
    CsvEncoder.instance(d => List(d.toString))

  val shapes: List[Shape] = List(
    Rectangle(3.0, 4.0),
    Circle(1.0)
  )

  println(writeCsv(shapes))



  println("==========\n")
}
