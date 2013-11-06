package org.pblue.asyncpools

import scala.slick.session.Session

case class Job[T](fn: Session => T)