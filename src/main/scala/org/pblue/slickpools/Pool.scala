package org.pblue.slickpools

import scala.concurrent.Future
import scala.reflect.ClassTag

import akka.util.Timeout

import scala.slick.session.Session

trait Pool {
	def execute[T : ClassTag](fn: Session => T)(implicit timeout: Timeout): Future[T]
}