package br.com.zup.ot5.compartilhado.interceptors

import br.com.zup.ot5.compartilhado.handlers.CustomExceptionHandler
import io.grpc.BindableService
import io.grpc.stub.StreamObserver
import io.micronaut.aop.MethodInterceptor
import io.micronaut.aop.MethodInvocationContext
import org.slf4j.LoggerFactory
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class GlobalGrpcEndpointInterceptor(@Inject private val resolver: GrpcExceptionResolver) : MethodInterceptor<BindableService, Any?> {

    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun intercept(context: MethodInvocationContext<BindableService, Any?>): Any? {
        return try {
            context.proceed()
        } catch (e: Exception) {

            logger.error("Capturada a excecao $e durante a execucao do metodo ${context.executableMethod}", e)

            @Suppress("UNCHECKED_CAST")
            val handler = resolver.resolve(e) as CustomExceptionHandler<Exception>
            val status = handler.handle(e)

            GrpcEndpointArguments(context).response()
                .onError(status.asRuntimeException())
        }
    }


    private class GrpcEndpointArguments(val context : MethodInvocationContext<BindableService, Any?>) {

        fun response(): StreamObserver<*> {
            return context.parameterValues[1] as StreamObserver<*>
        }

    }
}