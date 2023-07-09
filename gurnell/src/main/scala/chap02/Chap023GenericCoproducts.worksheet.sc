import shapeless.Generic.Aux
import shapeless.{:+:, ::, CNil, Generic, HList, HNil, Inl, Inr}

// ===== 2.3 Generic coproducts

case class Red()
case class Amber()
case class Green()
type Light = Red :+: Amber :+: Green :+: CNil

val red: Light = Inl(Red())

val green: Light = Inr(Inr(Inl(Green())))

// ----- 2.3.1 Switching encodings using Generic

import chap02._

val gen = Generic[Shape]
// gen: shapeless.Generic[Shape]{type Repr = Rectangle :+: Circle :+: shapeless.CNil} = anon$macro$1$1@5c6286ac

val coprRect = gen.to(Rectangle(3.0, 4.0))

val coprCircle = gen.to(Circle(1.0))
