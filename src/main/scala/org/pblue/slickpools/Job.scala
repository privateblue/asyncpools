package org.pblue.slickpools

import scala.slick.session.Session

case class Job[T](fn: Session => T)