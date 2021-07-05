package br.com.zup.ot5.chave_pix.exclui_chave_pix

import br.com.zup.ot5.ExcluiChavePixRequest
import br.com.zup.ot5.KeyManagerExcluiServiceGrpc
import br.com.zup.ot5.chave_pix.ChavePix
import br.com.zup.ot5.chave_pix.ChavePixRepository
import br.com.zup.ot5.chave_pix.TipoChave
import br.com.zup.ot5.integracoes.sistema_erp_itau.ContaResponse
import br.com.zup.ot5.integracoes.sistema_erp_itau.InstituicaoResponse
import br.com.zup.ot5.integracoes.sistema_erp_itau.TipoContaResponse
import br.com.zup.ot5.integracoes.sistema_erp_itau.TitularResponse
import br.com.zup.ot5.integracoes.sistema_pix_bcb.*
import io.grpc.ManagedChannel
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
import java.time.LocalDateTime
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton


@MicronautTest(transactional = false)
class ExcluiChavePixEndpointTest(
    private val chavePixRepository: ChavePixRepository,
    private val clientePixGrpc: KeyManagerExcluiServiceGrpc.KeyManagerExcluiServiceBlockingStub
){

    @Inject
    lateinit var bcbClient: SistemaPixBcbClient

    companion object {
        val CLIENTE_ID: UUID = UUID.randomUUID()
        val CLIENTE_ID_DIVERGENTE = UUID.randomUUID()
        val PIX_ID_INEXISTENTE = UUID.randomUUID()
        const val CPF_VALIDO = "63657520325"
        const val INSTITUICAO_VALIDA = "ITAÚ UNIBANCO S.A."
        const val ISBP = "60701190"
        const val NOME_TITULAR = "Rafael Ponte"
        const val AGENCIA = "1218"
        const val NUMERO = "291900"
    }

    @BeforeEach
    fun limpaSujeiraDoBanco(){
        chavePixRepository.deleteAll()
    }


    @Test
    fun `Deve excluir uma chave pix existente do mesmo titular`(){
        // cenario
        val chavePixSalva = chavePixRepository.save(
            ChavePix(
                tipoChave = TipoChave.CPF,
                chave = CPF_VALIDO,
                conta = dadosDaContaAssociadaResponse().paraConta()
            )
        )

        `when`(
            bcbClient.excluiPix(
                key = chavePixSalva.chave,
                request = DeletePixKeyRequest(
                    key = chavePixSalva.chave,
                    participant = chavePixSalva.conta.ispb
                )
        )).thenReturn(HttpResponse.ok(
            DeletePixKeyResponse(
                key = chavePixSalva.chave,
                participant = chavePixSalva.conta.ispb,
                deletedAt = LocalDateTime.now()
            )
        ))

        // exec
        val resposta = clientePixGrpc.excluiChavePix(
            ExcluiChavePixRequest.newBuilder()
                .setIdTitular(CLIENTE_ID.toString())
                .setPixId(chavePixSalva.id.toString())
                .build()
        )

        Assertions.assertNotNull(resposta)
        Assertions.assertEquals(0, chavePixRepository.count())
    }

    @Test
    fun `Nao deve excluir uma chave pix inexistente`(){
        // cenario
        chavePixRepository.save(
            ChavePix(
                tipoChave = TipoChave.CPF,
                chave = CPF_VALIDO,
                conta = dadosDaContaAssociadaResponse().paraConta()
            )
        )

        // exec
        Assertions.assertThrows(StatusRuntimeException::class.java){
            clientePixGrpc.excluiChavePix(
            ExcluiChavePixRequest.newBuilder()
                .setIdTitular(CLIENTE_ID.toString())
                .setPixId(PIX_ID_INEXISTENTE.toString())
                .build()
        )}

        Assertions.assertEquals(1, chavePixRepository.count())
    }

    @Test
    fun `Nao deve excluir uma chave pix existente de um outro titular`(){
        // cenario
        val chavePixSalva = chavePixRepository.save(
            ChavePix(
                tipoChave = TipoChave.CPF,
                chave = CPF_VALIDO,
                conta = dadosDaContaAssociadaResponse().paraConta()
            )
        )

        // exec
        Assertions.assertThrows(StatusRuntimeException::class.java){
            clientePixGrpc.excluiChavePix(
                ExcluiChavePixRequest.newBuilder()
                    .setIdTitular(CLIENTE_ID_DIVERGENTE.toString())
                    .setPixId(chavePixSalva.id.toString())
                    .build()
            )}

        Assertions.assertEquals(1, chavePixRepository.count())
    }

    private fun dadosDaContaAssociadaResponse() : ContaResponse{
        return ContaResponse(
            tipo = TipoContaResponse.CONTA_CORRENTE,
            instituicao = instituicaoContaResponse(),
            agencia = AGENCIA,
            numero = NUMERO,
            titular = titularContaResponse()
        )
    }

    private fun instituicaoContaResponse() : InstituicaoResponse{
        return InstituicaoResponse(
            nome = INSTITUICAO_VALIDA,
            ispb = ISBP
        )
    }

    private fun titularContaResponse() : TitularResponse{
        return TitularResponse(
            id = CLIENTE_ID,
            nome = NOME_TITULAR,
            cpf = CPF_VALIDO
        )
    }

    @MockBean(SistemaPixBcbClient::class)
    fun sistemaPixBcbClient() : SistemaPixBcbClient{
        return Mockito.mock(SistemaPixBcbClient::class.java)
    }

    @Factory
    class Clients {

        @Singleton
        fun excluiPixGrpcClient(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel)
                : KeyManagerExcluiServiceGrpc.KeyManagerExcluiServiceBlockingStub{
            return KeyManagerExcluiServiceGrpc.newBlockingStub(channel)
        }
    }
}