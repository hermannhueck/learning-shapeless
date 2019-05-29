package wiki

import shapeless._

/*
  https://github.com/milessabin/shapeless/wiki/Feature-overview:-shapeless-2.0.0#hlist-style-operations-on-standard-scala-tuples
 */
object App03HListStyleOperationsOnScalaTuples extends App {

  println("\n===== HList-style operations on standard Scala tuples =======")


  // shapeless allows standard Scala tuples to be manipulated in exactly the same ways as HLists,

  import syntax.std.tuple._


  println("\n>>> head, tail, take, drop, split:")
  println((23, "foo", true).head )
  // res0: Int = 23

  println((23, "foo", true).tail )
  // res1: (String, Boolean) = (foo,true)

  println((23, "foo", true).drop(2) )
  // res2: (Boolean,) = (true,)

  println((23, "foo", true).take(2) )
  // res3: (Int, String) = (23,foo)

  println((23, "foo", true).split(1) )
  // res4: ((Int,), (String, Boolean)) = ((23,),(foo,true))


  println("\n>>> prepend, append, concatenate:")
  println(23 +: ("foo", true) )
  // res5: (Int, String, Boolean) = (23,foo,true)

  println((23, "foo") :+ true )
  // res6: (Int, String, Boolean) = (23,foo,true)

  println((23, "foo") ++ (true, 2.0) )
  // res7: (Int, String, Boolean, Double) = (23,foo,true,2.0)


  println("\n>>> map, flatMap:")

  import poly._

  object option extends (Id ~> Option) {
    def apply[T](t: T) = Option(t)
  }

  println((23, "foo", true) map option )
  // res8: (Option[Int], Option[String], Option[Boolean]) = (Some(23),Some(foo),Some(true))

  println(((23, "foo"), (), (true, 2.0)) flatMap identity )
  // res9: (Int, String, Boolean, Double) = (23,foo,true,2.0)


  println("\n>>> fold:")

  object addSize extends Poly2 {

    object size extends Poly1 {

      implicit def caseInt: Case.Aux[Int, Int] =
        at[Int](x => 1)

      implicit def caseString: Case.Aux[String, Int] =
        at[String](_.length)

      implicit def caseTuple[T, U](implicit st : Case.Aux[T, Int], su : Case.Aux[U, Int]): Case.Aux[(T, U), Int] =
        at[(T, U)](t => size(t._1) + size(t._2))
    }

    implicit def default[T](implicit st: size.Case.Aux[T, Int]): Case.Aux[Int, T, Int] =
      at[Int, T] { (acc, t) => acc + size(t) }
  }

  println((23, "foo", (13, "wibble")).foldLeft(0)(addSize) )
  // res10: Int = 11

  println("\n>>> conversion to `HList`s and ordinary Scala `List`s:")
  println((23, "foo", true).productElements )
  // res11: Int :: String :: Boolean :: HNil = 23 :: foo :: true :: HNil

  println((23, "foo", true).toList )
  // res12: List[Any] = List(23, foo, true)

  println("\n>>> zipper:")
  import syntax.zipper._

  println((23, ("foo", true), 2.0).toZipper.right.down.put("bar").root.reify )
  // res13: (Int, (String, Boolean), Double) = (23,(bar,true),2.0)


  println("============\n")
}
