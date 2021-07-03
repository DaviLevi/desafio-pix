package br.com.zup.ot5.compartilhado.interceptors

import br.com.zup.ot5.compartilhado.handlers.CustomExceptionHandler
import br.com.zup.ot5.compartilhado.handlers.DefaultCustomExceptionHandler
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GrpcExceptionResolver(
    @Inject private val handlers: List<CustomExceptionHandler<*>>,
) {

    private var defaultHandler: CustomExceptionHandler<Exception> = DefaultCustomExceptionHandler()


    constructor(handlers: List<CustomExceptionHandler<*>>, defaultHandler: CustomExceptionHandler<Exception>) : this(handlers) {
        this.defaultHandler = defaultHandler
    }

    fun resolve(e: Exception): CustomExceptionHandler<*> {
        val foundHandlers = handlers.filter { it.supports(e) }

        if (foundHandlers.size > 1)
            throw IllegalStateException("Mais de um handler supportando a mesma exceção '${e.javaClass.name}': $foundHandlers")

        return foundHandlers.firstOrNull() ?: defaultHandler
    }

}