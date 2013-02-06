package net.fwbrasil.smirror

class InnerSClassSpecTestClass {
	class InnerClass
}
object InnerSClassSpecTestClass {
	def a = "a"
}

class InnerSClassSpec extends SMirrorSpec {

	"A SClass" should "return name" in
		test[InnerSClassSpecTestClass] { (sClass, jClass) =>
			val members = sClass.members
			println(members)
		}

}