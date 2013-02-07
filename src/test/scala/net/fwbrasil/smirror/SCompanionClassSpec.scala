package net.fwbrasil.smirror

class SCompanionClassSpecTestClass {
	def m1 = "a"
}
object SCompanionClassSpecTestClass extends SCompanionClassSpecTestClass {
	val m2 = 1
	var m3 = 2
}

class SCompanionClassSpec extends SMirrorSpec {

	"SCompanionClass" should "return methods" in
		test[SCompanionClassSpecTestClass] { (sClass, jClass) =>
			sClass.companionObjectOption.get.methods
				.map(m => (m.name, m.returnType)).toSet should
				equal(Set(("m1", sClassOf[String])))
		}

	it should "return vals" in
		test[SCompanionClassSpecTestClass] { (sClass, jClass) =>
			sClass.companionObjectOption.get
				.vals.map(m => (m.name, m.sClass)).toSet should
				equal(Set(("m2", sClassOf[Int])))
		}

	it should "return vars" in
		test[SCompanionClassSpecTestClass] { (sClass, jClass) =>
			sClass.companionObjectOption.get
				.vals.map(m => (m.name, m.sClass)).toSet should
				equal(Set(("m2", sClassOf[Int])))
		}

	it should "invoke method" in
		test[SCompanionClassSpecTestClass] { (sClass, jClass) =>
			sClass.companionObjectOption.get
				.methods.head.invoke() should
				equal("a")
		}

	it should "read val" in
		test[SCompanionClassSpecTestClass] { (sClass, jClass) =>
			sClass.companionObjectOption.get
				.vals.head.get should equal(1)
		}

	it should "read ans write var" in
		test[SCompanionClassSpecTestClass] { (sClass, jClass) =>
			val sVar = sClass.companionObjectOption.get.vars.head
			sVar.set(3)
			sVar.getter.invoke() should equal(3)
			sVar.setter.invoke(4)
			sVar.get should equal(4)
		}

}