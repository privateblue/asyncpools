package org.pblue.slickpools

import scala.slick.session.Session

case class Payload[T](fn: Session => T)