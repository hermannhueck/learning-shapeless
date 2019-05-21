package chap08

import chap02.Chap023GenericCoproducts.{Amber, Green, Red}
import shapeless._

object Chap083RandomValueGenerator extends App {

  println("\n===== 8.3 Case study: random value generator =====")


  trait Random[A] {
    def get: A
  }

  def random[A](implicit r: Random[A]): A = r.get


  println("\n----- 8.3.1 Simple random values -----")

  // Instance constructor:
  def createRandom[A](func: () => A): Random[A] =
    new Random[A] {
      def get: A = func()
    }

  // Random numbers from 0 to 9:
  implicit val intRandom: Random[Int] =
    createRandom(() => scala.util.Random.nextInt(10))

  // Random characters from A to Z:
  implicit val charRandom: Random[Char] =
    createRandom(() => ('A'.toInt + scala.util.Random.nextInt(26)).toChar)

  // Random booleans:
  implicit val booleanRandom: Random[Boolean] =
    createRandom(() => scala.util.Random.nextBoolean)


  for(i <- 1 to 3) println(random[Int])
  // 0
  // 8
  // 9
  println

  for(i <- 1 to 3) println(random[Char])
  // V
  // N
  // J
  println


  println("\n----- 8.3.2 Random products -----")

  implicit def genericRandom[A, R](
                                    implicit
                                    gen: Generic.Aux[A, R],
                                    random: Lazy[Random[R]]
                                  ): Random[A] =
    createRandom(() => gen.from(random.value.get))

  implicit val hnilRandom: Random[HNil] =
    createRandom(() => HNil)

  implicit def hlistRandom[H, T <: HList](
                                           implicit
                                           hRandom: Lazy[Random[H]],
                                           tRandom: Random[T]
                                         ): Random[H :: T] =
    createRandom(() => hRandom.value.get :: tRandom.get)


  case class Cell(col: Char, row: Int)

  for(i <- 1 to 5) println(random[Cell])
  // Cell(H,1)
  // Cell(D,4)
  // Cell(D,7)
  // Cell(V,2)
  // Cell(R,4)


  println("\n----- 8.3.3 Random coproducts -----")

  sealed trait Light
  case object Red extends Light
  case object Amber extends Light
  case object Green extends Light

  type Lights = Red :+: Amber :+: Green :+: CNil

  {
    // falsy implementation

    implicit val cnilRandom: Random[CNil] =
      createRandom(() => throw new Exception("Inconceivable!"))

    implicit def coproductRandom[H, T <: Coproduct](
                                                     implicit
                                                     hRandom: Lazy[Random[H]],
                                                     tRandom: Random[T]
                                                   ): Random[H :+: T] =
      createRandom { () =>
        val chooseH = scala.util.Random.nextDouble < 0.5
        if (chooseH) Inl(hRandom.value.get) else Inr(tRandom.get)
      }

    // There problems with this implementation lie in the 50/50 choice in calculating chooseH.

    // Our coproduct instances will throw excep􏰀ons 6.75% of the 􏰀me!

    // for (i <- 1 to 100) random[Light]
    // java.lang.Exception: Inconceivable!
    //   ...
  }

  {
    // correct impl

    import shapeless.ops.coproduct
    import shapeless.ops.nat.ToInt

    implicit val cnilRandom: Random[CNil] =
      createRandom(() => throw new Exception("Inconceivable!"))

    implicit def coproductRandom[H, T <: Coproduct, L <: Nat](
                                                               implicit
                                                               hRandom: Lazy[Random[H]],
                                                               tRandom: Random[T],
                                                               tLength: coproduct.Length.Aux[T, L],
                                                               tLengthAsInt: ToInt[L]
                                                             ): Random[H :+: T] = {
      createRandom { () =>
        val length = 1 + tLengthAsInt()
        val chooseH = scala.util.Random.nextDouble < (1.0 / length)
        if(chooseH) Inl(hRandom.value.get) else Inr(tRandom.get)
      }
    }

    for(i <- 1 to 5) println(random[Light])
    // Green
    // Red
    // Red
    // Red
    // Green
  }

  println("==========\n")
}
