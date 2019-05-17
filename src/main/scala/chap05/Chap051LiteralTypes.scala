package chap05

object Chap051LiteralTypes extends App {

  println("\n===== 5.1 Literal types =====")

  object Foo
  // defined object Foo

  Foo
  // res0: Foo.type = Foo$@34b22273


  "hello"
  // res1: String = hello

  ("hello" : String)
  // res2: String = hello


  import shapeless.syntax.singleton._

  // var x = 42.narrow
  // x: Int(42) = 42

  // x = 43
  // <console>:16: error: type mismatch:
  //  found   : Int(43)
  //  required: Int(42)
  //        x = 43
  // ^

  // x+1
  // res3: Int = 43

/*
  1.narrow
  // res4: Int(1) = 1
  true.narrow
  // res5: Boolean(true) = true
  "hello".narrow
  // res9: String("hello") = hello
  // and so on...
*/

  // math.sqrt(4).narrow
  // <console>:17: error:
  // Expression scala.math.`package`.sqrt(4.0) does not evaluate to a constant or a stable reference value
  //        math.sqrt(4.0).narrow
  //                  ^
  // <console>:17: error: value narrow is not a member of Double
  // math.sqrt(4.0).narrow
  //                ^


  println("==========\n")
}
