package net.fwbrasil.smirror

trait Dummy

class SFieldSpecTestClass[X <: Dummy](val m1: String) {
    val m2 = "b"
    var m3 = 1
    var x: Option[X] = _
}

class SFieldSpec extends SMirrorSpec {

    "SClass" should "return its fields" in
        test[SFieldSpecTestClass[_]] { (sClass, jClass) =>
            sClass.fields.map(_.name).toSet should
                equal(Set("m1", "m2", "m3"))
        }

    "Vals" should "reflected" in
        test[SFieldSpecTestClass[_]] { (sClass, jClass) =>
            val instance = new SFieldSpecTestClass("a")
            val vals = sClass.vals.map {
                field => (field.name, field.sClass, field.get(instance))
            }
            vals.toSet should equal(Set(
                ("m1", sClassOf[String], "a"),
                ("m2", sClassOf[String], "b")))
        }

    "Vars" should "reflected" in
        test[SFieldSpecTestClass[_]] { (sClass, jClass) =>
            val instance = new SFieldSpecTestClass("a")
            val vars = sClass.vars.map {
                field => (field.name, field.sClass, field.get(instance))
            }
            vars.toSet should equal(Set(
                ("m3", sClassOf[Int], 1)))
        }

    "Vars" should "be modified" in
        test[SFieldSpecTestClass[_]] { (sClass, jClass) =>
            val instance = new SFieldSpecTestClass("a")
            val field = sClass.vars.find(_.name == "m3").get
            field.set(instance, 2)
            field.get(instance) should equal(2)
            field.setter.invoke(instance, 3)
            field.getter.invoke(instance) should equal(3)
        }

    "Vars" should "reflect option type parameter to the upper bound class" in
        test[SFieldSpecTestClass[_]] { (sClass, jClass) =>
            val field = sClass.vars.find(_.name == "x").get
            field.typeArguments.head === sClassOf[Dummy]
        }
}