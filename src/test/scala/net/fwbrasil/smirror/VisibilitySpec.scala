package net.fwbrasil.smirror

class VisibilitySpec extends SMirrorSpec {

    def testPrivatePackage(subject: Visibility[_]) {
        subject.isPrivateWithin should equal(true)
        subject.isPrivate should equal(false)
        subject.isProtected should equal(false)
        subject.isVisibleFrom(sClassOf[Any]) should equal(false)
        subject.isVisibleFrom(sClassOf[VisibilitySpecsTestClass]) should equal(true)
        subject.isVisibleFrom(sClassOf[SClassVisibilitySpec]) should equal(true)
    }

    def testPublic(subject: Visibility[_]) {
        subject.isPrivateWithin should equal(false)
        subject.isPrivate should equal(false)
        subject.isProtected should equal(false)
        subject.isVisibleFrom(sClassOf[Any]) should equal(true)
        subject.isVisibleFrom(sClassOf[VisibilitySpecsTestClass]) should equal(true)
        subject.isVisibleFrom(sClassOf[SClassVisibilitySpec]) should equal(true)
    }

    def testPrivate(subject: Visibility[_]) {
        subject.isPrivateWithin should equal(false)
        subject.isPrivate should equal(true)
        subject.isProtected should equal(false)
        subject.isVisibleFrom(sClassOf[Any]) should equal(false)
        subject.isVisibleFrom(sClassOf[VisibilitySpecsTestClass]) should equal(false)
        subject.isVisibleFrom(sClassOf[SClassVisibilitySpec]) should equal(false)
    }

    def testProtected(subject: Visibility[_]) {
        subject.isPrivateWithin should equal(false)
        subject.isPrivate should equal(false)
        subject.isProtected should equal(true)
        subject.isVisibleFrom(sClassOf[Any]) should equal(false)
        subject.isVisibleFrom(sClassOf[VisibilitySpecsTestClass]) should equal(true)
        subject.isVisibleFrom(sClassOf[SClassVisibilitySpec]) should equal(true)
    }

    def testProtectedPackage(subject: Visibility[_]) {
        subject.isPrivateWithin should equal(true)
        subject.isPrivate should equal(false)
        subject.isProtected should equal(true)
        subject.isVisibleFrom(sClassOf[Any]) should equal(false)
        subject.isVisibleFrom(sClassOf[VisibilitySpecsTestClass]) should equal(true)
        subject.isVisibleFrom(sClassOf[SClassVisibilitySpec]) should equal(true)
    }
}