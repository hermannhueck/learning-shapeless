import shapeless.Generic

// ===== 4.2 Dependent Types

def getRepr[A](value: A)(implicit gen: Generic[A]) = gen.to(value)

case class Vec(x: Int, y: Int)
case class Rect(origin: Vec, size: Vec)

getRepr(Vec(1, 2))
getRepr(Rect(Vec(0, 0), Vec(5, 5)))
