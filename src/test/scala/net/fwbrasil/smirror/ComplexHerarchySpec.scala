package net.fwbrasil.smirror

trait C1 {
	val v1: String
	def m1 = "a"
}
abstract class C2 extends C1 {
	var r1 = 1
	val v2 = 2
	val v3: String
	def m2: Int
}
abstract class C3(var r2: Int) extends C2 {
	val v3 = "d"
	val v4 = "b"
	val v5 = "c"
	def m3 = 1
}
trait C4 {
	this: C2 =>
	def v6: List[Int]
	override def m2 = 3
	override val v3 = "e"
	def m4 = "f"
	var r3: String
}
class ComplexHerarchySpecTestClass extends C3(1) with C4 {
	val v1 = "g"
	var r3 = "i"
	var r4 = "h"
	val v6 = List()
	def m5 = 4
}

class ComplexHerarchySpec extends SMirrorSpec {

	val instance = new ComplexHerarchySpecTestClass

	"SMirror with complexy hierarchy" should "reflect vals" in
		test[ComplexHerarchySpecTestClass] { (sClass, jClass) =>
			sClass.vals.map(v => (v.name, v.sClass, v.get(instance))).toSet should
				equal(Set(
					("v1", sClassOf[String], "g"),
					("v2", sClassOf[Int], 2),
					("v3", sClassOf[String], "e"),
					("v4", sClassOf[String], "b"),
					("v5", sClassOf[String], "c"),
					("v6", sClassOf[List[_]], List())))
		}

	it should "reflect vars" in
		test[ComplexHerarchySpecTestClass] { (sClass, jClass) =>
			sClass.vars.map(v => (v.name, v.sClass, v.get(instance))).toSet should
				equal(Set(
					("r1", sClassOf[Int], 1),
					("r2", sClassOf[Int], 1),
					("r3", sClassOf[String], "i"),
					("r4", sClassOf[String], "h")))
		}

	it should "modify vars" in
		test[ComplexHerarchySpecTestClass] { (sClass, jClass) =>
			sClass.vars.zip(List(2, 3, "x", "y")).foreach {
				tuple =>
					val (sVar, value) = tuple
					sVar.set(instance, value)
					sVar.get(instance) should equal(value)
			}
		}
}