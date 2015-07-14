package org.pblue.asyncpools

/**
 * Is able to create [[Resource]]s and check the integrity of those.
 * @tparam Resource The type of resources [[Worker]]s will operate on.
 */
trait Manager[Resource] {
	/**
	 * Create a new [[Resource]] instance.
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

	/**
	 * Free system resources by destroying the [[Resource]] instance. Will be called when a [[Worker]] is shut down.
	 * Should handle exceptions internally to prevent them from bubbling up to the ActorSystem.
	 * @param resource The object which's handles should be released.
	 * @return
	 */
	def destroy(resource: Resource): Unit
}
