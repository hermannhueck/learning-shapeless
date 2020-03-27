package blog

import scala.collection.mutable
import util._

/*
  http://milessabin.com/blog/2011/07/16/fundeps-in-scala/
 */
@com.github.ghik.silencer.silent("deprecated")
object App03FunDepsInScala extends App {

  // ----------------------------------------
  prtTitle("Functional Dependencies in Scala")

  def error(msg: String) = throw new UnsupportedOperationException

  trait Matrix // Dummy definitions for expository purposes
  trait Vector

  {
    trait MultDep[A, B, C]

    implicit object mmm extends MultDep[Matrix, Matrix, Matrix]
    implicit object mvv extends MultDep[Matrix, Vector, Vector]
    implicit object mim extends MultDep[Matrix, Int, Matrix]
    implicit object imm extends MultDep[Int, Matrix, Matrix]

    // OK: Matrix * Matrix -> Matrix
    implicitly[MultDep[Matrix, Matrix, Matrix]]
    // res0: MultDep[Matrix,Matrix,Matrix] = mmm$@1ddeda2

    // OK: Matrix * Vector -> Vector
    implicitly[MultDep[Matrix, Vector, Vector]]
    // res1: MultDep[Matrix,Vector,Vector] = mvv$@1a8fb1b

    // Error
    /*
      implicitly[MultDep[Matrix, Vector, Matrix]]
      <console>:15: error: could not find implicit value for
        parameter e: MultDep[Matrix,Vector,Matrix]
     */

    def mult[A, B, C](a: A, b: B)(implicit instance: MultDep[A, B, C]): C =
      error("TODO")

    // Type annotations solely to verify that the correct result type
    // has been inferred

    val r1: Matrix = mult(new Matrix    {}, new Matrix {}) // Compiles
    val r2: Vector = mult(new Matrix    {}, new Vector {}) // Compiles
    val r3: Matrix = mult(new Matrix    {}, 2) // Compiles
    val r4: Matrix = mult(2, new Matrix {}) // Compiles

    // This next one doesn't compile ...
    // val r5: Matrix = mult(new Matrix {}, new Vector{})

    // Notice how the third type parameter, C, isn’t used in the first (explicit) parameter list.
    // If it weren’t for its appearance in the second (implicit) parameter list the compiler would normally infer it as Nothing
    // — not what we want at all. But the combination of type inference and implicit search saves the day.
    // The first two type parameters A and B are inferred from the explicit arguments to mult(), then implicit search kicks in,
    // attempting to locate an implicit definition of MultDep[_, _, _] consistent with those two types.
    // By construction, it will only ever find one, and that is sufficient to uniquely determine C for use as the result type of the function.
  }

  {
    trait MultDep[A, B, C] {
      def apply(a: A, b: B): C
    }

    implicit object mmm extends MultDep[Matrix, Matrix, Matrix] {
      override def apply(m1: Matrix, m2: Matrix): Matrix = error("TODO")
    }

    implicit object mvv extends MultDep[Matrix, Vector, Vector] {
      override def apply(m1: Matrix, v2: Vector): Vector = error("TODO")
    }

    implicit object mim extends MultDep[Matrix, Int, Matrix] {
      override def apply(m1: Matrix, i2: Int): Matrix = error("TODO")
    }

    implicit object imm extends MultDep[Int, Matrix, Matrix] {
      override def apply(i1: Int, m2: Matrix): Matrix = error("TODO")
    }

    def mult[A, B, C](a: A, b: B)(implicit instance: MultDep[A, B, C]): C =
      instance(a, b)

    val r1: Matrix = mult(new Matrix    {}, new Matrix {}) // Compiles
    val r2: Vector = mult(new Matrix    {}, new Vector {}) // Compiles
    val r3: Matrix = mult(new Matrix    {}, 2) // Compiles
    val r4: Matrix = mult(2, new Matrix {}) // Compiles

    // This next one doesn't compile ...
    // val r5: Matrix = mult(new Matrix {}, new Vector{})
  }

  {
    trait ExtractDep[A, B] {
      def apply(a: A): B
    }

    implicit def ep[A, B]: ExtractDep[(A, B), A] = new ExtractDep[(A, B), A] {
      def apply(p: (A, B)): A = p._1
    }

    def extract[A, B](a: A)(implicit instance: ExtractDep[A, B]): B =
      instance(a)

    // Type annotation solely to verify that the correct result type
    // has been inferred
    val c: Char = extract(('x', 3))
  }

  {
    import scala.collection.generic._

    implicitly[CanBuildFrom[List[Int], String, List[String]]]
    // res0: CanBuildFrom[List[Int],String,List[String]] = ...

    implicitly[CanBuildFrom[Set[String], Double, Set[Double]]]
    // res1: CanBuildFrom[Set[String],Double,Set[Double]] = ...

    // These two don't compile ...
    // implicitly[CanBuildFrom[List[String], Int, List[Double]]]
    // <console>:11: error: ...

    // implicitly[CanBuildFrom[List[String], Int, Set[Int]]]
    // <console>:11: error: ...

    /*
    trait TraversibleLike[Repr, +A] {
      def map[B, That](f: A => B)(implicit bf: CanBuildFrom[Repr, B, That]): That = {
        val repr: Repr = ???
        val b: mutable.Builder[B, That] = bf(repr)
        b.sizeHint(this)
        for (x <- this) b += f(x)
        b.result
      }
    }
   */
  }

  prtLine()
}
