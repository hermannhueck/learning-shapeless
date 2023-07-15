import shapeless._
import shapeless.ops.{coproduct, hlist, nat}

// ===== 8.2 Length of generic representations

val hlistLength =
  hlist.Length[String :: Int :: Boolean :: HNil]

val coproductLength =
  coproduct.Length[Double :+: Char :+: CNil]

Nat.toInt[hlistLength.Out]

Nat.toInt[coproductLength.Out]

// ----- sizeOf[Product]

trait SizeOf[A] {
  def value: Int
}

def sizeOf[A](implicit size: SizeOf[A]): Int = size.value

// To create an instance of SizeOf we need three things:
//  1. a Generic to calculate the corresponding HList type
//  2. a Length to calculate the length of the HList as a Nat
//  3. a ToInt to convert the Nat to an Int.

implicit def genericSizeOf[A, L <: HList, N <: Nat](
    implicit
    generic: Generic.Aux[A, L],
    size: hlist.Length.Aux[L, N],
    sizeToInt: nat.ToInt[N]
): SizeOf[A] =
  new SizeOf[A] {
    val value: Int = sizeToInt.apply()
  }

case class IceCream(name: String, numCherries: Int, inCone: Boolean)

sizeOf[IceCream]
