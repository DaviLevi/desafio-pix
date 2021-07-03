package br.com.zup.ot5.grpc

import br.com.zup.ot5.compartilhado.handlers.CustomExceptionHandler
import br.com.zup.ot5.compartilhado.handlers.DefaultCustomExceptionHandler
import br.com.zup.ot5.compartilhado.interceptors.GrpcExceptionResolver
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ExceptionHandlerResolverTest {


    private lateinit var illegalStateExceptionHandler : CustomExceptionHandler<IllegalStateException>
    private lateinit var grpcExceptionResolver: GrpcExceptionResolver
    private lateinit var illegalGrpcExceptionResolver: GrpcExceptionResolver
    private lateinit var defaultExceptionHandler: CustomExceptionHandler<Exception>

    @BeforeEach
    fun setup() {

        illegalStateExceptionHandler = object : CustomExceptionHandler<IllegalStateException>{
            override fun handle(e: IllegalStateException): CustomExceptionHandler.StatusWithDetails = TODO("Ok-Hey!")

            override fun supports(e: Exception): Boolean = e is IllegalStateException
        }

        defaultExceptionHandler = DefaultCustomExceptionHandler()

        grpcExceptionResolver = GrpcExceptionResolver(
            handlers = listOf(illegalStateExceptionHandler),
            defaultHandler = defaultExceptionHandler
        )

        illegalGrpcExceptionResolver = GrpcExceptionResolver(
            handlers = listOf(illegalStateExceptionHandler,illegalStateExceptionHandler),
            defaultHandler = defaultExceptionHandler
        )

    }

    @Test
    fun `deve retornar o ExceptionHandler especifico para o tipo de excecao`() {

        val resolver = grpcExceptionResolver.resolve(e = IllegalStateException("Ok-Hey!"))

        Assertions.assertEquals(illegalStateExceptionHandler, resolver)

    }

    @Test
    fun `deve retornar o ExceptionHandler padrao quando nenhum handler suportar o tipo da excecao`() {
        val resolver = grpcExceptionResolver.resolve(e = IllegalArgumentException("Ok-Hey!"))

        Assertions.assertEquals(defaultExceptionHandler, resolver)
    }

    @Test
    fun `deve lancar um erro caso encontre mais de um ExceptionHandler que suporte a mesma excecao`() {
        Assertions.assertThrows(IllegalStateException::class.java){
            illegalGrpcExceptionResolver.resolve(e = IllegalStateException("Ok-Hey!"))
        }
    }
}