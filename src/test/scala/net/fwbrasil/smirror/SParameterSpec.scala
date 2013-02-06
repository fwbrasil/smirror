package net.fwbrasil.smirror

class SParameterSpecTestClass(
		val m1: Int,
		m2: String,
		m3: Array[Int],
		m4: Object,
		m5: Any,
		m6: String = "a") {

	def m7(m8: Exception, m9: Any = 1, m10: Int = m1) = null
}

class SParameterSpec extends SMirrorSpec {

	"Contructor parameters" should "be reflected" in
		test[SParameterSpecTestClass] { (sClass, jClass) =>
			val params = sClass.constructors.head.parameters.map {
				parameter =>
					(parameter.name,
						parameter.sClass,
						parameter.defaultValueOption)
			}
			params.toSet should equal(Set(
				("m1", sClassOf[Int], None),
				("m2", sClassOf[String], None),
				("m3", sClassOf[Array[Int]], None),
				("m4", sClassOf[Object], None),
				("m5", sClassOf[Any], None),
				("m6", sClassOf[String], Some("a"))))

		}

	"Method parameters" should "be reflected" in
		test[SParameterSpecTestClass] { (sClass, jClass) =>
			val instance = new SParameterSpecTestClass(99, "b", Array(1), new Object, "c")
			val params = sClass.methods.head.parameters.map {
				parameter =>
					(parameter.name,
						parameter.sClass,
						parameter.defaultValueOption(instance))
			}
			params.toSet should equal(Set(
				("m8", sClassOf[Exception], None),
				("m9", sClassOf[Any], Some(1)),
				("m10", sClassOf[Int], Some(99))))

		}
}