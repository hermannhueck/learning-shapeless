package wiki

import shapeless._

object App02HeterogeniousLists extends App {

  println("\n===== Heterogenous lists =======")


  println("----- HList#map -----")

  import poly._

  // The same definition of choose as above
  object choose extends (Set ~> Option) {
    def apply[T](s : Set[T]) = s.headOption
  }

  val sets = Set(1) :: Set("foo") :: HNil
  // sets: Set[Int] :: Set[String] :: HNil = Set(1) :: Set(foo) :: HNil
  println(sets)

  val opts = sets map choose   // map selects cases of choose for each HList element
  // opts: Option[Int] :: Option[String] :: HNil = Some(1) :: Some(foo) :: HNil
  println(opts)


  println("----- HList#flatMap -----")

  import poly.identity

  val l = (23 :: "foo" :: HNil) :: HNil :: (true :: HNil) :: HNil
  // l: ((Int :: String :: HNil) :: HNil :: (Boolean :: HNil) :: HNil= (23 :: foo :: HNil) :: HNil :: (true :: HNil) :: HNil
  println(l)

  val res0 = l flatMap identity           // flatten
  // res0: Int :: String :: Boolean :: HNil = 23 :: foo :: true :: HNil
  println(res0)


  println("----- HList#fold -----")

  object size extends Poly1 {

    implicit def caseInt =
      at[Int](x => 1)

    implicit def caseString =
      at[String](_.length)

    implicit def caseTuple[T, U](implicit st : Case.Aux[T, Int], su : Case.Aux[U, Int]) =
      at[(T, U)](t => size(t._1) + size(t._2))
  }

  object addSize extends Poly2 {
    implicit def default[T](implicit st: size.Case.Aux[T, Int]): Case.Aux[Int, T, Int] =
      at[Int, T] { (acc, t) => acc + size(t) }
  }

  val l2 = 23 :: "foo" :: (13, "wibble") :: HNil
  // l2: Int :: String :: (Int, String) :: HNil = 23 :: foo :: (13,wibble) :: HNil
  println(l2)

  val res1 = l2.foldLeft(0)(addSize)
  // res1: Int = 11
  println(res1)


  println("----- HList#toZipper -----")

  import syntax.zipper._

  val l3 = 1 :: "foo" :: 3.0 :: HNil
  // l3: Int :: String :: Double :: HNil = 1 :: foo :: 3.0 :: HNil
  println(l3)

  val res2 = l3.toZipper.right.put(("wibble", 45)).reify
  // res2: Int :: (String, Int) :: Double :: HNil = 1 :: (wibble,45) :: 3.0 :: HNil
  println(res2)

  val res3 = l3.toZipper.right.delete.reify
  // res3: Int :: Double :: HNil = 1 :: 3.0 :: HNil
  println(res3)

  val res4 = l3.toZipper.last.left.insert("bar").reify
  // res4: Int :: String :: String :: Double :: HNil = 1 :: foo :: bar :: 3.0 :: HNil
  println(res4)


  println("----- HList is covariant. -----")

  trait Fruit
  case class Apple() extends Fruit
  case class Pear() extends Fruit

  type FFFF = Fruit :: Fruit :: Fruit :: Fruit :: HNil
  type APAP = Apple :: Pear :: Apple :: Pear :: HNil

  val a : Apple = Apple()
  val p : Pear = Pear()

  val apap : APAP = a :: p :: a :: p :: HNil
  println(s"Apples and Pears: $apap")
  val ffff : FFFF = apap          // covariant: APAP <: FFFF
  println(s"Fruits: $ffff")


  println("----- HList#unify -----")

  val unified = apap.unify
  // unified: Fruit :: Fruit :: Fruit :: Fruit :: HNil = Apple() :: Pear() :: Apple() :: Pear() :: HNil
  println(unified)


  println("----- HList#toList -----")

  val list = apap.toList
  // list: List[Fruit] = List(Apple(), Pear(), Apple(), Pear())
  println(list)


  println("----- HList has a Typeable type class instance. -----")

  import syntax.typeable._

  val precise: Option[APAP] = ffff.cast[APAP]
  // precise: Option[APAP] = Some(Apple() :: Pear() :: Apple() :: Pear() :: HNil)
  println("Typeable allows type-safe downcast: ")
  println(s"value:    $precise")
  println(s"has type: Option[${Typeable[APAP].describe}]")

  println(s">>> Typeable[APAP].describe: ${Typeable[APAP].describe}")
  println(s">>> Typeable[APAP].toString: ${Typeable[APAP].toString}")


  println("============\n")
}
