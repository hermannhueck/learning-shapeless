package wiki

import shapeless._

/*
  https://github.com/milessabin/shapeless/wiki/Feature-overview:-shapeless-2.0.0#boilerplate-free-lenses-for-arbitrary-case-classes
 */
object App11BoilerplateFreeLensesForArbitraryCaseClasses extends App {

  println("\n===== Boilerplate-free lenses for arbitrary case classes =======")


  // A combination of LabelledGeneric and singleton-typed Symbol literals supports boilerplate-free lens creation for arbitrary case classes,

  // A pair of ordinary case classes ...
  case class Address(street : String, city : String, postcode : String)
  case class Person(name : String, age : Int, address : Address)

  // Some lenses over Person/Address ...
  val nameLens     = lens[Person] >> Symbol("name")
  val ageLens      = lens[Person] >> Symbol("age")
  val addressLens  = lens[Person] >> Symbol("address")
  val streetLens   = lens[Person] >> Symbol("address") >> Symbol("street")
  val cityLens     = lens[Person] >> Symbol("address") >> Symbol("city")
  val postcodeLens = lens[Person] >> Symbol("address") >> Symbol("postcode")

  println("\n>>> person:")
  val person = Person("Joe Grey", 37, Address("Southover Street", "Brighton", "BN2 9UA"))
  // person: Person = Person(Joe Grey,37,Address(Southover Street,Brighton,BN2 9UA))
  println(person)

  println("\n>>> ageLens.get(person):")
  val age1 = ageLens.get(person)               // Read field, note inferred type
  // age1: Int = 37
  println(age1)

  println("\n>>> ageLens.set(person)(38):")
  val person2 = ageLens.set(person)(38)        // Update field
  // person2: Person = Person(Joe Grey,38,Address(Southover Street,Brighton,BN2 9UA))
  println(person2)

  println("\n>>> ageLens.modify(person2)(_ + 1):")
  val person3 = ageLens.modify(person2)(_ + 1) // Transform field
  // person3: Person = Person(Joe Grey,39,Address(Southover Street,Brighton,BN2 9UA))
  println(person2)

  println("\n>>> streetLens.get(person3):")
  val street = streetLens.get(person3)         // Read nested field
  // street: String = Southover Street
  println(street)

  println("\n>>> streetLens.set(person3)(\"Montpelier Road\"):")
  val person4 = streetLens.set(person3)("Montpelier Road")  // Update nested field
  // person4: Person = Person(Joe Grey,39,Address(Montpelier Road,Brighton,BN2 9UA))
  println(person4)


  println("============\n")
}
