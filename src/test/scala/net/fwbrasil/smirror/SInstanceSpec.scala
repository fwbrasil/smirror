package net.fwbrasil.smirror

class SInstanceSpecTestClass {
    def m1 = "a"
    val m2 = 1
    var m3 = 2
}

class SInstanceSpec extends SMirrorSpec {

    def sInstance = (new SInstanceSpecTestClass).reflect

    "SInstance" should "return methods" in lock.synchronized {
        sInstance.methods.map(m => (m.name, m.returnType)).toSet should
            equal(Set(("m1", sClassOf[String])))
    }

    it should "return vals" in lock.synchronized {
        sInstance.vals.map(m => (m.name, m.sClass)).toSet should
            equal(Set(("m2", sClassOf[Int])))
    }

    it should "return vars" in lock.synchronized {
        sInstance.vals.map(m => (m.name, m.sClass)).toSet should
            equal(Set(("m2", sClassOf[Int])))
    }

    it should "invoke method" in lock.synchronized {
        sInstance.methods.head.invoke() should
            equal("a")
    }

    it should "read val" in lock.synchronized {
        sInstance.vals.head.get should equal(1)
    }

    it should "read ans write var" in lock.synchronized {
        val sVar = sInstance.vars.head
        sVar.get should equal(2)
        sVar.set(3)
        sVar.getter.invoke() should equal(3)
        sVar.setter.invoke(4)
        sVar.get should equal(4)
    }
}