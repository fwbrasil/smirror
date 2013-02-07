package net.fwbrasil.smirror

class SClassInnerSpecTestClass {
	class InnerClass(val m1: Int)
	def newInner = new InnerClass(1)
}

class SClassInnerSpec extends SMirrorSpec {

	val instance = new SClassInnerSpecTestClass
	val inner = instance.newInner

	"A inner SClass" should "invoke constructor" in
		test[SClassInnerSpecTestClass#InnerClass] { (sClass, jClass) =>
			sClass.constructors.head.invoke(instance, 2).m1 should
				equal(2)
		}

	"A inner SClass" should "return val" in
		test[SClassInnerSpecTestClass#InnerClass] { (sClass, jClass) =>
			sClass.vals.head.get(inner) should
				equal(1)
		}

}