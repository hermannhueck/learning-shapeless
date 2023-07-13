package object model {

  case class Person(name: String, age: Double)
  case class Book(title: String, author: String, year: Int)
  case class Country(name: String, population: Int, area: Double)
}
