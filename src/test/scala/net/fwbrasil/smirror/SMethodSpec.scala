package net.fwbrasil.smirror

class SMethodSpecTestClass {
    def m1 = "a"
    def m2(m3: Int) = m3
    def m4(m5: String, m6: Int) = m5
    def m7(m8: SMethodSpecTestClass) = m8
    def m9(m10: Array[Int])(m11: String) = m10
    def m11() = "b"
}

trait Foo {
    @deprecated("foo is a terrible name! what were you thinking?", "0.1")
    def foo(i: Int) = i
    
    def abs: Unit
}

class Bar extends Foo {
	def abs = {}
}

class SMethodSpec extends SMirrorSpec {

    val instance = new SMethodSpecTestClass

    "SMethod" should "reflect string return type" in
        test[SMethodSpecTestClass] { (sClass, jClass) =>
            val method = sClass.methods.find(_.name == "m1").get
            method.returnType should equal(sClassOf[String])
        }

    it should "reflect int return type" in
        test[SMethodSpecTestClass] { (sClass, jClass) =>
            val method = sClass.methods.find(_.name == "m2").get
            method.returnType should equal(sClassOf[Int])
        }

    it should "reflect SMethodSpecTestClass return type" in
        test[SMethodSpecTestClass] { (sClass, jClass) =>
            val method = sClass.methods.find(_.name == "m7").get
            method.returnType should equal(sClassOf[SMethodSpecTestClass])
        }
    
    it should "reflect Array[Int] return type" in
        test[SMethodSpecTestClass] { (sClass, jClass) =>
            val method = sClass.methods.find(_.name == "m9").get
            method.returnType should equal(sClassOf[Array[Int]])
        }
    
    it should "reflect the java method" in
        test[SMethodSpecTestClass] { (sClass, jClass) =>
            val method = sClass.methods.find(_.name == "m11").get
            method.javaMethodOption.get === classOf[SMethodSpecTestClass].getMethod("m11")
        }

    it should "be invoked without parameter list" in
        test[SMethodSpecTestClass] { (sClass, jClass) =>
            val method = sClass.methods.find(_.name == "m1").get
            method.invoke(instance) should equal("a")
        }

    it should "be invoked with empty parameter list" in
        test[SMethodSpecTestClass] { (sClass, jClass) =>
            val method = sClass.methods.find(_.name == "m11").get
            method.invoke(instance) should equal("b")
        }

    it should "be invoked with one int parameter" in
        test[SMethodSpecTestClass] { (sClass, jClass) =>
            val method = sClass.methods.find(_.name == "m2").get
            method.invoke(instance, 1) should equal(1)
        }

    it should "be invoked with two parameters" in
        test[SMethodSpecTestClass] { (sClass, jClass) =>
            val method = sClass.methods.find(_.name == "m4").get
            method.invoke(instance, "x", 1) should equal("x")
        }

    it should "be invoked with one object" in
        test[SMethodSpecTestClass] { (sClass, jClass) =>
            val method = sClass.methods.find(_.name == "m7").get
            method.invoke(instance, instance) should equal(instance)
        }

    it should "be invoked with curried parameters" in
        test[SMethodSpecTestClass] { (sClass, jClass) =>
            val method = sClass.methods.find(_.name == "m9").get
            method.invoke(instance, Array(1), "a") should equal(Array(1))
        }

    it should "reflect parameters" in
        test[SMethodSpecTestClass] { (sClass, jClass) =>
            sClass.methods.map(_.parametersGroups.map(_.map(_.name))).toSet should
                equal(Set(List(), List(List("m3")), List(List("m5", "m6")), List(List("m8")),
                    List(List("m10"), List("m11")), List(List())))
        }

    it should "reflect annotations" in {
        val jMethod = classOf[Bar].getMethod("foo", classOf[Int])
        sMethod(jMethod).symbol.annotations.map(ann => sClassOf(ann.tpe).name) should
            equal(List("scala.deprecated"))
    }
    
    it should "reflect isAbstract" in {
        test[Foo] { (sClass, jClass) =>
            sClass.methods.filter(_.isAbstract).map(_.name) should equal(List("abs"))
        }
        test[Bar] { (sClass, jClass) =>
            sClass.methods.filter(_.isAbstract) should equal(List())
        }
    }
}