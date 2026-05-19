class RetryPolicy(private val maxRetries: Int) {
    fun canRetry(attempt: Int): Boolean {
        return attempt < maxRetries
    }
}