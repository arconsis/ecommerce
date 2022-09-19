package com.arconsis.common.errors

class ChainAbortedException(internal val reason: FailureReason?) : RuntimeException() {
	override fun toString(): String {
		return "ChainAbortedException(reason = '$reason')"
	}
}

inline fun <reified T : FailureReason> abort(reason: T?): Nothing = throw ChainAbortedException(reason)

fun <T : FailureReason> ChainAbortedException.reason(): T? {
	@Suppress("UNCHECKED_CAST")
	return reason as? T
}

