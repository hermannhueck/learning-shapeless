import shapeless._

// ===== 8.4 Other operations involving Nat

val hlist = 123 :: "foo" :: true :: 'x' :: HNil
println("--- Original HList:")
println(hlist)

// returns the element at position 1
// using a Nat as a type parameter
val pos1 = hlist.apply[Nat._1]

// returns the element at position 3
// using a Nat as a value parameter
val pos3 = hlist(Nat._3)

// like take and drop on List with Nat instead of Int
// grabs the 2nd and 3rd element of the HList
hlist
  .take(Nat._3)
  .drop(Nat._1)
hlist
  .take[Nat._3]
  .drop[Nat._1]

// like updatedAt on List with Nat instead of Int
// updates the 2nd and 3rd element of the HList
hlist
  .updatedAt(Nat._1, "bar")
  .updatedAt(Nat._2, "baz")
hlist
  .updatedAt[Nat._1]("bar")
  .updatedAt[Nat._2]("baz")
