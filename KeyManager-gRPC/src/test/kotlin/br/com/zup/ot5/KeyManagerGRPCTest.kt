package br.com.zup.ot5
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Factory
import io.micronaut.core.annotation.Introspected
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.runtime.EmbeddedApplication
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import io.micronaut.validation.Validated
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import javax.inject.Inject
import javax.inject.Singleton
import javax.validation.Valid
import javax.validation.constraints.NotEmpty

@MicronautTest(transactional = false)
class KeyManagerGRPCTest(
    private val client : KeyManagerGRPCServiceGrpc.KeyManagerGRPCServiceBlockingStub
){

//    @Test
//    fun testItWorks() {
//        // cenario
//        val request = CriaChavePixRequest.newBuilder().build()
//
//        try{
//            println("Oi")
//            client.criaChavePix(request)
//        }catch (e: StatusRuntimeException){
//            println("Exception")
//            println("""
//			${e.message}
//		""".trimIndent())
//        }
//    }

//    @Test
//    fun testItWorks() {
//        // cenario
//        val beanValidationObject = DtoQualquer(texto = "")
//
//        metodoValidado(dto = beanValidationObject)
//    }
//
//
//    fun metodoValidado(@Valid dto: DtoQualquer){
//        println(dto.texto)
//        println("Entrou no metodo validado")
//    }
//
//    @Introspected
//    @Validated
//    class DtoQualquer(
//        @NotEmpty val texto: String
//    ){
//
//    }

    @Factory
    class Clients {

        @Singleton
        fun carroGrpcClient(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel)
                : KeyManagerGRPCServiceGrpc.KeyManagerGRPCServiceBlockingStub{
            return KeyManagerGRPCServiceGrpc.newBlockingStub(channel)
        }
    }

}
