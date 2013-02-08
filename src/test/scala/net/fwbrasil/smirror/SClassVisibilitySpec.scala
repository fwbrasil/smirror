package net.fwbrasil.smirror

private[smirror] class VisibilitySpecsTestClass
private[smirror] object VisibilitySpecsTestClass
class VisibilitySpecsTestClass2
private class VisibilitySpecsTestClass3
protected class VisibilitySpecsTestClass4
protected[smirror] class VisibilitySpecsTestClass5

class SClassVisibilitySpec extends VisibilitySpec {

    "private[smirror] SClass" should "reflect its visibility" in
        test[VisibilitySpecsTestClass] { (sClass, jClass) =>
            testPrivatePackage(sClass)
        }

    "public SClass" should "reflect its visibility" in
        test[VisibilitySpecsTestClass2] { (sClass, jClass) =>
            testPublic(sClass)
        }

    "private SClass" should "reflect its visibility" in
        test[VisibilitySpecsTestClass3] { (sClass, jClass) =>
            testPrivate(sClass)
        }

    "protected SClass" should "reflect its visibility" in
        test[VisibilitySpecsTestClass4] { (sClass, jClass) =>
            testProtected(sClass)
        }

    "protected[smirror] SClass" should "reflect its visibility" ignore
        test[VisibilitySpecsTestClass4] { (sClass, jClass) =>
            testProtectedPackage(sClass)
        }
}