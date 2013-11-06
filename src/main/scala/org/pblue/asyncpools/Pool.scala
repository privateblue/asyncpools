package org.pblue.asyncpools

import scala.concurrent.Future

import akka.util.Timeout

import scala.slick.session.Session

trait Pool {
	def execute[T](fn: Session => T)(implicit timeout: Timeout): Future[T]
}