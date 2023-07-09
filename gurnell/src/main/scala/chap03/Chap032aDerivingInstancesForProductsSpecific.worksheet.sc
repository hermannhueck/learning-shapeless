import shapeless.Generic.Aux
import shapeless.{::, Generic, HList, HNil}
import shapeless.{:+:, CNil, Coproduct, Inl, Inr}

// ===== 3.2 Deriving instances for products (specific instances)"

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
  def instance[A](func: A => List[String]): CsvEncoder[A] = new CsvEncoder[A] {
    def encode(value: A): List[String] = func(value)
  }

  // Globally visible type class instances

  implicit val stringEncoder: CsvEncoder[String] =
    instance(str => List(str))

  implicit val intEncoder: CsvEncoder[Int] =
    instance(num => List(num.toString))

  implicit val booleanEncoder: CsvEncoder[Boolean] =
    instance(bool => List(if (bool) "yes" else "no"))

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

  // Taken together, these five instances allow us to summon CsvEncoders for any HList involving Strings, Ints, and Booleans.
}

// ----- 3.2.1 Instances for HLists

val reprEncoder: CsvEncoder[String :: Int :: Boolean :: HNil] = implicitly

val encodedHList: List[String] =
  reprEncoder.encode("abc" :: 123 :: true :: HNil)

// ----- 3.2.2 Instances for concrete products

case class IceCream(name: String, numCherries: Int, inCone: Boolean)

val iceCreams: List[IceCream] = List(
  IceCream("Sundae", 1, false),
  IceCream("Cornetto", 0, true),
  IceCream("Banana Split", 0, false)
)

case class Employee(name: String, number: Int, manager: Boolean)

val employees: List[Employee] = List(
  Employee("Bill", 1, true),
  Employee("Peter", 2, false),
  Employee("Milton", 3, false)
)

val employeesWithIceCrams: List[(Employee, IceCream)] = employees zip iceCreams

implicit val iceCreamEncoder: CsvEncoder[IceCream] = {
  val gen: Aux[IceCream, String :: Int :: Boolean :: HNil] = Generic[IceCream]
  val enc: CsvEncoder[String :: Int :: Boolean :: HNil]    = CsvEncoder[gen.Repr]
  CsvEncoder.instance(iceCream => enc.encode(gen.to(iceCream)))
}

val encodedIceCreams: String = writeCsv(iceCreams)

implicit val employeeEncoder: CsvEncoder[Employee] = {
  val gen = Generic[Employee]
  val enc = CsvEncoder[gen.Repr]
  CsvEncoder.instance(employee => enc.encode(gen.to(employee)))
}

val encodedEmployees: String = writeCsv(employees)

implicit def pairEncoder[A, B](implicit aEncoder: CsvEncoder[A], bEncoder: CsvEncoder[B]): CsvEncoder[(A, B)] =
  CsvEncoder.instance {
    case (a, b) => aEncoder.encode(a) ++ bEncoder.encode(b)
  }

val encodedPairs: String = writeCsv(employeesWithIceCrams)
