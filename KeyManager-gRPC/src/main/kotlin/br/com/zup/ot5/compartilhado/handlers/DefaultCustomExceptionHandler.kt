package br.com.zup.ot5.compartilhado.handlers


import io.grpc.Status

class DefaultCustomExceptionHandler : CustomExceptionHandler<Exception> {

    override fun handle(e: Exception): CustomExceptionHandler.StatusWithDetails {
        val status = when (e) {
            is IllegalArgumentException -> Status.INVALID_ARGUMENT.withDescription(e.message)
            is IllegalStateException -> Status.FAILED_PRECONDITION.withDescription(e.message)
            else -> Status.UNKNOWN
        }
        return CustomExceptionHandler.StatusWithDetails(status.withCause(e))
    }

    override fun supports(e: Exception): Boolean {
        return true
    }

}