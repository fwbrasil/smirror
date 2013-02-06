package net.fwbrasil.smirror

case class SConstructorSpecTestClass(m1: String, m2: Int, m3: Boolean) {
	def this(m4: String)(m5: Int) = this(m4, m5, false)
	def this() = this("a")(1)
}

class SConstructorSpec extends SMirrorSpec {

	"SConstructor" should "return its SClass as the return type" in
		test[SConstructorSpecTestClass] { (sClass, jClass) =>
			sClass.constructors.map(_.returnType).toSet should
				equal(Set(sClass))
		}

	it should "return its parameters" in
		test[SConstructorSpecTestClass] { (sClass, jClass) =>
			sClass.constructors.map(_.parametersGroups.map(_.map(_.name))).toSet should
				equal(Set(List(List()), List(List("m4"), List("m5")), List(List("m1", "m2", "m3"))))
		}

	it should "return its name as <init>" in
		test[SConstructorSpecTestClass] { (sClass, jClass) =>
			sClass.constructors.map(_.name).toSet should
				equal(Set("<init>"))
		}

	"SConstructor invocation" should "work with empty constructor" in
		test[SConstructorSpecTestClass] { (sClass, jClass) =>
			val constructors = sClass.constructors
			val constructor =
				constructors.find(_.parameters.isEmpty).get
			val instance = constructor.invoke()
			instance.m1 should equal("a")
			instance.m2 should equal(1)
			instance.m3 should equal(false)
		}

	it should "work with curried parameters" in
		test[SConstructorSpecTestClass] { (sClass, jClass) =>
			val constructors = sClass.constructors
			val constructor =
				constructors.find(_.parameters.size == 2).get
			val instance = constructor.invoke("z", 99)
			instance.m1 should equal("z")
			instance.m2 should equal(99)
			instance.m3 should equal(false)
		}

	it should "work with primary constructor" in
		test[SConstructorSpecTestClass] { (sClass, jClass) =>
			val constructors = sClass.constructors
			val constructor =
				constructors.find(_.parameters.size == 3).get
			val instance = constructor.invoke("z", 99, true)
			instance.m1 should equal("z")
			instance.m2 should equal(99)
			instance.m3 should equal(true)
		}

}