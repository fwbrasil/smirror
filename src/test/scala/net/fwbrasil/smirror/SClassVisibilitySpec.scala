package net.fwbrasil.smirror

private[smirror] class VisibilitySpecsTestClass
class VisibilitySpecsTestClass2
private class VisibilitySpecsTestClass3
protected class VisibilitySpecsTestClass4
protected[smirror] class VisibilitySpecsTestClass5

class SClassVisibilitySpec extends SMirrorSpec {

	"private[smirror] SClass" should "reflect its visibility" in
		test[VisibilitySpecsTestClass] { (sClass, jClass) =>
			sClass.isPrivateWithin should equal(true)
			sClass.isPrivate should equal(false)
			sClass.isProtected should equal(false)
			sClass.isVisibleFrom(sClassOf[Any]) should equal(false)
			sClass.isVisibleFrom(sClassOf[VisibilitySpecsTestClass]) should equal(true)
			(sClass.isVisibleFrom(sClassOf[SClassVisibilitySpec])) should equal(true)
		}

	"public SClass" should "reflect its visibility" in
		test[VisibilitySpecsTestClass2] { (sClass, jClass) =>
			sClass.isPrivateWithin should equal(false)
			sClass.isPrivate should equal(false)
			sClass.isProtected should equal(false)
			sClass.isVisibleFrom(sClassOf[Any]) should equal(true)
			sClass.isVisibleFrom(sClassOf[VisibilitySpecsTestClass]) should equal(true)
			(sClass.isVisibleFrom(sClassOf[SClassVisibilitySpec])) should equal(true)
		}

	"private SClass" should "reflect its visibility" in
		test[VisibilitySpecsTestClass3] { (sClass, jClass) =>
			sClass.isPrivateWithin should equal(false)
			sClass.isPrivate should equal(true)
			sClass.isProtected should equal(false)
			sClass.isVisibleFrom(sClassOf[Any]) should equal(false)
			sClass.isVisibleFrom(sClassOf[VisibilitySpecsTestClass]) should equal(false)
			(sClass.isVisibleFrom(sClassOf[SClassVisibilitySpec])) should equal(false)
		}

	"protected SClass" should "reflect its visibility" in
		test[VisibilitySpecsTestClass4] { (sClass, jClass) =>
			sClass.isPrivateWithin should equal(false)
			sClass.isPrivate should equal(false)
			sClass.isProtected should equal(true)
			sClass.isVisibleFrom(sClassOf[Any]) should equal(false)
			sClass.isVisibleFrom(sClassOf[VisibilitySpecsTestClass]) should equal(true)
			(sClass.isVisibleFrom(sClassOf[SClassVisibilitySpec])) should equal(true)
		}

	"protected[smirror] SClass" should "reflect its visibility" ignore
		test[VisibilitySpecsTestClass4] { (sClass, jClass) =>
			sClass.isPrivateWithin should equal(true)
			sClass.isPrivate should equal(false)
			sClass.isProtected should equal(true)
			sClass.isVisibleFrom(sClassOf[Any]) should equal(false)
			sClass.isVisibleFrom(sClassOf[VisibilitySpecsTestClass]) should equal(true)
			(sClass.isVisibleFrom(sClassOf[SClassVisibilitySpec])) should equal(true)
		}
}