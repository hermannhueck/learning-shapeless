package chap08

import chap02.Chap023GenericCoproducts.{Amber, Green, Red}
import shapeless._

import util._

object Chap083RandomValueGenerator extends App {

  // ----------------------------------------
  prtTitle("8.3 Case study: random value generator")

  trait Random[A] {
    def get: A
  }

  def random[A](implicit r: Random[A]): A = r.get

  // ----------------------------------------
  prtSubTitle("8.3.1 Simple random values")

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
    createRandom(() => scala.util.Random.nextBoolean())

  for (i <- 1 to 3) println(random[Int])
  // 0
  // 8
  // 9
  println()

  for (i <- 1 to 3) println(random[Char])
  // V
  // N
  // J
  println()

  // ----------------------------------------
  prtSubTitle("8.3.2 Random products")

  implicit def genericRandom[A, R](
      implicit
      gen: Generic.Aux[A, R],
      random: Lazy[Random[R]]
  ): Random[A] =
    createRandom(() => {
      val randomValue: R = random.value.get
      val a: A           = gen.from(randomValue)
      a
    })

  implicit val hnilRandom: Random[HNil] =
    createRandom(() => HNil)

  implicit def hlistRandom[H, T <: HList](
      implicit
      hRandom: Lazy[Random[H]],
      tRandom: Random[T]
  ): Random[H :: T] =
    createRandom { () =>
      val randomHead: H = hRandom.value.get
      val randomTail: T = tRandom.get
      val hlist: H :: T = randomHead :: randomTail
      hlist
    }

  case class Cell(col: Char, row: Int)

  for (i <- 1 to 5) println(random[Cell])
  // Cell(H,1)
  // Cell(D,4)
  // Cell(D,7)
  // Cell(V,2)
  // Cell(R,4)

  // ----------------------------------------
  prtSubTitle("8.3.3 Random coproducts")

  sealed trait Light
  case object Red   extends Light
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
        val chooseH: Boolean = scala.util.Random.nextDouble() < 0.5
        val res: H :+: T     = if (chooseH) Inl(hRandom.value.get) else Inr(tRandom.get)
        res
      }

    // There problems with this implementation lie in the 50/50 choice in calculating chooseH.

    // !!!!!!!!!!!!!!!!! Our coproduct instances will throw excep􏰀ons 6.75% of the ti􏰀me!

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
        val length: Int      = 1 + tLengthAsInt()
        val chooseH: Boolean = scala.util.Random.nextDouble() < (1.0 / length)
        val res: H :+: T     = if (chooseH) Inl(hRandom.value.get) else Inr(tRandom.get)
        res
      }
    }

    for (i <- 1 to 5) println(random[Light])
    // Green
    // Red
    // Red
    // Red
    // Green

    println()
    val lights = (0 until 1000).toList.map(_ => random[Light])
    // println(lights)
    println("count all:   " + lights.length)
    println("count Red:   " + lights.filter(_ == Red).length)
    println("count Amber: " + lights.filter(_ == Amber).length)
    println("count Green: " + lights.filter(_ == Green).length)
  }

  prtLine()
}
