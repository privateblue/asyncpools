package org.pblue.asyncpools.slick

class SessionFactory(
	val url: String,
	val user: String,
	val password: String,
	val driver: String) extends AbstractSessionFactory