package wiki

import scala.collection.immutable
import util._

/*
  https://github.com/milessabin/shapeless/wiki/Feature-overview:-shapeless-2.0.0#collections-with-statically-known-sizes
 */
object App14CollectionsWithStaticallyKnownSizes extends App {

  // ----------------------------------------
  prtTitle("Collections with statically known sizes")

  // shapeless provides collection types with statically known sizes. These can prevent runtime errors such as those
  // that would result from attempting to take the head of an empty list, and can also verify more complex relationships.
  //
  // In the example below we define a method csv whose signature guarantees at compile time that there are exactly as many column headers provided as colums,

  def row(cols: Seq[String]) =
    cols.mkString("\"", "\", \"", "\"\n")

  import shapeless.{Nat, Sized}

  def csv[N <: Nat](hdrs: Sized[Seq[String], N], rows: List[Sized[Seq[String], N]]): immutable.Seq[String] =
    row(hdrs) :: rows.map(row(_))

  val hdrs = Sized("Title", "Author")

  val rows = List(
    Sized("Types and Programming Languages", "Benjamin Pierce"),
    Sized("The Implementation of Functional Programming Languages", "Simon Peyton-Jones")
  )

  // hdrs and rows statically known to have the same number of columns
  val formatted = csv(hdrs, rows) // Compiles
  println(formatted)

  // extendedHdrs has the wrong number of columns for rows
  val extendedHdrs = Sized("Title", "Author", "ISBN")
  //val badFormatted = csv(extendedHdrs, rows)             // Does not compile

  prtLine()
}
