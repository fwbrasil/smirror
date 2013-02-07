package net.fwbrasil.smirror

import scala.reflect.runtime.universe._

trait Visibility[C] {

	def symbol: Symbol
	def ownerSType: SType[C]
	lazy val privateWithin =
		symbol.privateWithin match {
			case NoSymbol =>
				None
			case symbol =>
				Option(symbol.fullName)
		}
	lazy val isProtected =
		symbol.isProtected
	lazy val isPrivate =
		symbol.isPrivate
	lazy val isPrivateWithin =
		privateWithin.isDefined
	def isVisibleFrom(source: SType[_]) = {
		val sourcePackage = source.owner.fullName.toString.trim
		if (privateWithin.isDefined) {
			val targetPackage = privateWithin.get
			sourcePackage.startsWith(targetPackage)
		} else if (isProtected)
			ownerSType.packageName == source.packageName ||
				source.isAssignableFrom(ownerSType)
		else if (isPrivate)
			ownerSType == source
		else
			true
	}
}