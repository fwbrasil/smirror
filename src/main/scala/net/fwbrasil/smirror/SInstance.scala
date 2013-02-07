package net.fwbrasil.smirror

case class SInstance[C](sClass: SType[C], instance: C) {
	val methods = sClass.methods.map(SInstanceMethod(_, instance))
	val vals = sClass.vals.map(SInstanceVal(_, instance))
	val vars = sClass.vars.map(SInstanceVar(_, instance))
}

case class SInstanceMethod[C](sMethod: SMethod[C], instance: C) {
	lazy val returnType = sMethod.returnType
	val name = sMethod.name
	def invoke(params: Any*) =
		sMethod.invoke(instance, params: _*)
}

trait SInstanceField[C] {
	val sField: SField[C]
	val instance: C
	lazy val sClass = sField.sClass
	val name = sField.name
	val getter = SInstanceMethod(sField.getter, instance)
	def get = sField.get(instance)
}

case class SInstanceVal[C](sField: SVal[C], instance: C)
		extends SInstanceField[C] {
	def sVal = sField
}

case class SInstanceVar[C](sField: SVar[C], instance: C)
		extends SInstanceField[C] {
	def sVar = sField
	val setter = SInstanceMethod(sField.setter, instance)
	def set(value: Any) =
		sVar.set(instance, value)
}