package org.pblue.asyncpools

/**
 * Jobs use [[Resource]]s to produce [[Result]]s.
 * @param fn The function to execute.
 * @tparam Resource The type of resource to leverage.
 * @tparam Result The expected result type.
 */
private[asyncpools] final case class Job[Resource, Result](fn: Resource => Result)