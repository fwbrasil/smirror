package net.fwbrasil.smirror

class TypeParametersSpecTestClass(m1: List[Int]) {
	def m2(m3: Array[String]) = null
	val m4 = Map[String, Int]()
	var m5 = Map[List[Int], String]()
}

class TypeParametersSpec extends SMirrorSpec {

	"Constructor parameter" should "return its generic" in
		test[TypeParametersSpecTestClass] { (sClass, jClass) =>
			sClass.constructors.head.parameters.head.typeArguments should
				equal(List(sClassOf[Int]))
		}

	"Method parameter" should "return its generic" in
		test[TypeParametersSpecTestClass] { (sClass, jClass) =>
			sClass.methods.find(_.name == "m2")
				.get.parameters.head.typeArguments should
				equal(List(sClassOf[String]))
		}

	"Field" should "return its generic" in
		test[TypeParametersSpecTestClass] { (sClass, jClass) =>
			sClass.vals.head.typeArguments should
				equal(List(sClassOf[String], sClassOf[Int]))
		}

	"Field with nested generic" should "return its first level generic" in
		test[TypeParametersSpecTestClass] { (sClass, jClass) =>
			sClass.vars.head.typeArguments should
				equal(List(sClassOf[List[Any]], sClassOf[String]))
		}
}