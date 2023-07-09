// ===== 5.1 Literal Types

// ----- Singleton Types
object Foo
// defined object Foo

Foo
// res0: Foo.type = Foo$@34b22273

val s1 = "hello"
// s1: String = hello

val s2 = ("hello": String)
// s2: String = hello

// ----- <literal>.narrow

import shapeless.syntax.singleton._

val x1 = 42.narrow

var x2 = 42.narrow
// x2: Int(42) = 42

// x2 = 43
// <console>:16: error: type mismatch:
//  found   : Int(43)
//  required: Int(42)
//        x = 43
// ^

val y = x1 + 1
// y: Int = 43

// We can use narrow on any literal in Scala:
1.narrow
// res4: Int(1) = 1
true.narrow
// res5: Boolean(true) = true
"hello".narrow
// res6: String("hello") = hello
// and so on...

// However, we can’t use it on compound expressions:
// math.sqrt(4).narrow
// <console>:17: error:
// Expression scala.math.`package`.sqrt(4.0) does not evaluate to a constant or a stable reference value
//        math.sqrt(4.0).narrow
//                  ^
// <console>:17: error: value narrow is not a member of Double
// math.sqrt(4.0).narrow
//                ^
