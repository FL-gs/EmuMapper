package dev.emumapper.app.data.ini

sealed class WriteResult {

    data object Success : WriteResult()

    data class Failure(
        val emulatorId: String,
        val reason: String,
        val throwable: Throwable? = null
    ) : WriteResult()

    data class PartialFailure(
        val failures: List<Failure>
    ) : WriteResult() {
        init {
            require(failures.isNotEmpty()) {
                "PartialFailure must contain at least one failure"
            }
        }
    }
}