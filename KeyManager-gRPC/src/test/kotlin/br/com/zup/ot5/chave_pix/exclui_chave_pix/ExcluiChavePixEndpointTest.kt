package br.com.zup.ot5.chave_pix.exclui_chave_pix

import br.com.zup.ot5.ExcluiChavePixRequest
import br.com.zup.ot5.KeyManagerExcluiServiceGrpc
import br.com.zup.ot5.chave_pix.ChavePix
import br.com.zup.ot5.chave_pix.ChavePixRepository
import br.com.zup.ot5.chave_pix.TipoChave
import br.com.zup.ot5.chave_pix.cria_chave_pix.CriaChavePixEndpointTest
import br.com.zup.ot5.integracoes.sistema_erp_itau.ContaResponse
import br.com.zup.ot5.integracoes.sistema_erp_itau.InstituicaoResponse
import br.com.zup.ot5.integracoes.sistema_erp_itau.TipoContaResponse
import br.com.zup.ot5.integracoes.sistema_erp_itau.TitularResponse
import io.grpc.ManagedChannel
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*
import javax.inject.Singleton


@MicronautTest(transactional = false)
class ExcluiChavePixEndpointTest(
    private val chavePixRepository: ChavePixRepository,
    private val clientePixGrpc: KeyManagerExcluiServiceGrpc.KeyManagerExcluiServiceBlockingStub
){

    companion object {
        val CLIENTE_ID = UUID.randomUUID()
        val CLIENTE_ID_DIVERGENTE = UUID.randomUUID()
        val PIX_ID_INEXISTENTE = UUID.randomUUID()
        val CPF_VALIDO = "01606156233"
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
            id = CLIENTE_ID,
            nome = "Rafael Ponte",
            cpf = "63657520325"
        )
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