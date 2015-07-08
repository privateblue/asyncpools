package org.pblue.asyncpools

import scala.util.Try

final case class JobCompletion[Result](result: Try[Result], durationMs: Long)