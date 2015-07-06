package org.pblue.asyncpools

/**
 * Is able to create [[Resource]]s and check the integrity of those.
 * @tparam Resource The type of resources [[Worker]]s will operate on.
 */
trait Factory[Resource] {
	/**
	 * Create a new [[Resource instance]].
	 * @return
	 */
	def create: Resource

	/**
	 * Will be called by [[Worker]]s if the [[Job]] they're handling results in an error.
	 * If this method returns a [[Throwable]], it will be thrown, and the [[Worker]] will be restarted. as a result.
	 * @param resource The [[Resource]] to check.
	 * @return
	 */
	def check(resource: Resource): Option[Throwable]
}
