package org.pblue.asyncpools

import scala.util.Try

/**
 * The result of a completed job and the time it took to complete it.
 * @param result A Success or a Failure
 * @param durationMs The time it took to build the result in milliseconds
 * @tparam Result The type of result to expect
 */
final case class JobCompletion[Result](result: Try[Result], durationMs: Long)