package org.pblue.asyncpools.testpool

import org.pblue.asyncpools.Manager

class ConverterIsDownException extends Exception("Tough luck!")

class ConvertingFailedException extends Exception("Whatever.")

class Converter {
	var isUp: Boolean = true
	def convert(str: String) =
		if (isUp && str != "fail")
			str.toUpperCase
		else
			throw new ConvertingFailedException()
}

class ConverterManager extends Manager[Converter] {
	override def create: Converter = new Converter

	override def check(resource: Converter): Option[Throwable] =
		if (!resource.isUp)
			Some(new ConverterIsDownException)
		else
			None

	override def destroy(resource: Converter): Unit = ()
}