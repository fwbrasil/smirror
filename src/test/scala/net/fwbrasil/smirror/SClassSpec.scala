package net.fwbrasil.smirror

import org.scalatest.Tag
import org.scalatest.Ignore

class SClassSpecTestClass(val m1: String, var m2: String)
        extends Serializable {
    def this(m3: String) = this(m3, m3)
    def m4 = 1
    def m5 = 2
    var m6 = "a"
    val m7 = "b"
}

class SClassSpec extends SMirrorSpec {

    "A SClass" should "return name" in
        test[SClassSpecTestClass] { (sClass, jClass) =>
            sClass.name should equal(jClass.getName)
        }

    it should "return simple name" in
        test[SClassSpecTestClass] { (sClass, jClass) =>
            sClass.simpleName should equal(jClass.getSimpleName)
        }

    it should "return constructors" in
        test[SClassSpecTestClass] { (sClass, jClass) =>
            sClass.constructors.map(_.parametersGroups.flatten.map(_.name)).toSet should
                equal(Set(List("m1", "m2"), List("m3")))
        }

    it should "return methods" in
        test[SClassSpecTestClass] { (sClass, jClass) =>
            sClass.methods.map(_.name) should equal(List("m4", "m5"))
        }

    it should "return vars" in
        test[SClassSpecTestClass] { (sClass, jClass) =>
            sClass.vars.map(_.name) should equal(List("m2", "m6"))
        }

    it should "return vals" in
        test[SClassSpecTestClass] { (sClass, jClass) =>
            sClass.vals.map(_.name) should equal(List("m1", "m7"))
        }

    it should "return if is assignable from Serializable" in
        test[SClassSpecTestClass] { (sClass, jClass) =>
            sClass.isAssignableFrom(sClassOf[Serializable])
        }

    it should "return the baseClasses" in
        test[SClassSpecTestClass] { (sClass, jClass) =>
            sClass.baseClassesHierarchyInLinearizationOrder should
                equal(List(sClassOf[Serializable], sClassOf[java.io.Serializable],
                    sClassOf[Object], sClassOf[Any]).reverse)
        }

    "A SClass of a AnyVal" should "return name" in {
        test[Int] { (sClass, jClass) =>
            sClass.name should equal("scala.Int")
        }
    }

}