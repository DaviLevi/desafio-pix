package br.com.zup.ot5.chave_pix

import br.com.zup.ot5.CriaChavePixRequest
import br.com.zup.ot5.KeyManagerGRPCServiceGrpc
import br.com.zup.ot5.compartilhado.model.Conta
import br.com.zup.ot5.integracoes.sistema_erp_itau.*
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.http.HttpResponse
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.validation.ConstraintViolationException
import javax.validation.Valid
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull


@MicronautTest(transactional = false)
class CriaChavePixEndpointTest(
    private val chavePixRepository: ChavePixRepository,
    private val clientePixGrpc: KeyManagerGRPCServiceGrpc.KeyManagerGRPCServiceBlockingStub
){

    @Inject
    lateinit var itauClient: SistemaERPItauClient;

    companion object {
        val CLIENTE_ID = UUID.randomUUID()
        val CPF_VALIDO = "01606156233"
        val CPF_INVALIDO = "123-bla-45633"
    }

    @BeforeEach
    fun limpaSujeiraDoBanco(){
        chavePixRepository.deleteAll()
    }


    @Test
    fun `Deve registrar uma chave pix valida`(){
        // cenario
        val chavePixValida = chavePixValida()

        `when`(itauClient.buscaContaPorTipo(clienteId = CLIENTE_ID.toString(), tipo = "CONTA_CORRENTE"))
            .thenReturn(HttpResponse.ok(dadosDaContaAssociadaResponse()))

        // exec
        val chavePixCriada = clientePixGrpc.criaChavePix(
            chavePixValida
        )

        // validacao
        Assertions.assertNotNull(chavePixCriada)
        Assertions.assertNotNull(chavePixCriada.pixId)
        Assertions.assertNotNull(chavePixCriada.idTitular)
        Assertions.assertEquals(chavePixCriada.idTitular, chavePixValida.idTitular)
        Assertions.assertEquals(1, chavePixRepository.count())
    }

    @Test
    fun `Nao deve registrar uma chave pix duplicada`(){
        // cenario
        chavePixRepository.save(
            ChavePix(
                tipoChave = TipoChave.CPF,
                idTitular = CLIENTE_ID,
                chave = CPF_VALIDO,
                conta = dadosDaContaAssociadaResponse().paraConta()
            )
        )
        val chavePixDuplicada = chavePixValida()

        `when`(itauClient.buscaContaPorTipo(clienteId = CLIENTE_ID.toString(), tipo = "CONTA_CORRENTE"))
            .thenReturn(HttpResponse.ok(dadosDaContaAssociadaResponse()))

        // exec
        // validacao
        val excecao = Assertions.assertThrows(StatusRuntimeException::class.java){
            clientePixGrpc.criaChavePix(
                chavePixDuplicada
            )
        }

        Assertions.assertEquals(Status.ALREADY_EXISTS.code, excecao.status.code)
        Assertions.assertEquals(1, chavePixRepository.count())
    }

    @Test
    fun `Nao deve registrar uma chave pix com parametros invalidos`(){
        // cenario
        val chavePixInvalida = chavePixTotalmenteInvalida()

        // exec
        // validacao
        val excecao = Assertions.assertThrows(StatusRuntimeException::class.java){
            clientePixGrpc.criaChavePix(
                chavePixInvalida
            )
        }

        Assertions.assertEquals(Status.INVALID_ARGUMENT.code, excecao.status.code)
        Assertions.assertEquals(0, chavePixRepository.count())
    }

    @Test
    fun `Nao deve registrar uma chave pix caso nao sejam encontrados dados da conta associada a ela`(){
        // cenario
        val chavePixSemContaAssociada = chavePixValida()

        `when`(itauClient.buscaContaPorTipo(clienteId = CLIENTE_ID.toString(), tipo = "CONTA_CORRENTE"))
            .thenReturn(HttpResponse.notFound())

        // exec
        // validacao
        val excecao = Assertions.assertThrows(StatusRuntimeException::class.java){
            clientePixGrpc.criaChavePix(
                chavePixSemContaAssociada
            )
        }

        Assertions.assertEquals(Status.FAILED_PRECONDITION.code, excecao.status.code)
        Assertions.assertEquals(0, chavePixRepository.count())
    }


    @MockBean(SistemaERPItauClient::class)
    fun sistemaErpItauClient() : SistemaERPItauClient{
        return Mockito.mock(SistemaERPItauClient::class.java)
    }

    @Factory
    class Clients {

        @Singleton
        fun chavePixGrpcClient(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel)
                : KeyManagerGRPCServiceGrpc.KeyManagerGRPCServiceBlockingStub{
            return KeyManagerGRPCServiceGrpc.newBlockingStub(channel)
        }
    }

    fun chavePixValida() : CriaChavePixRequest{
        return CriaChavePixRequest.newBuilder()
                    .setIdTitular(CLIENTE_ID.toString())
                    .setValorChave(CPF_VALIDO)
                    .setTipoChave(CriaChavePixRequest.TipoChave.CPF)
                    .setTipoConta(CriaChavePixRequest.TipoConta.CONTA_CORRENTE)
                .build()
    }

    fun chavePixTotalmenteInvalida() : CriaChavePixRequest{
        return CriaChavePixRequest.newBuilder().build()
    }

    fun dadosDaContaAssociadaResponse() : ContaResponse{
        return ContaResponse(
            tipo = TipoContaResponse.CONTA_CORRENTE,
            instituicao = instituicaoContaResponse(),
            agencia = "1218",
            numero = "291900",
            titular = titularContaResponse()
        )
    }

    fun instituicaoContaResponse() : InstituicaoResponse{
        return InstituicaoResponse(
            nome = "ITAÚ UNIBANCO S.A.",
            ispb = "60701190"
        )
    }

    fun titularContaResponse() : TitularResponse{
        return TitularResponse(
            id = CLIENTE_ID.toString(),
            nome = "Rafael Ponte",
            cpf = "63657520325"
        )
    }


}