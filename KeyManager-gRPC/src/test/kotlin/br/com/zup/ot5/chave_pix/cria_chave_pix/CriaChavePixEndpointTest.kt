package br.com.zup.ot5.chave_pix.cria_chave_pix

import br.com.zup.ot5.KeyManagerRegistraServiceGrpc
import br.com.zup.ot5.RegistraChavePixRequest
import br.com.zup.ot5.chave_pix.ChavePix
import br.com.zup.ot5.chave_pix.ChavePixRepository
import br.com.zup.ot5.chave_pix.TipoChave
import br.com.zup.ot5.integracoes.sistema_erp_itau.*
import br.com.zup.ot5.integracoes.sistema_pix_bcb.*
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
import java.time.LocalDateTime
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton


@MicronautTest(transactional = false)
class CriaChavePixEndpointTest(
    private val chavePixRepository: ChavePixRepository,
    private val clientePixGrpc: KeyManagerRegistraServiceGrpc.KeyManagerRegistraServiceBlockingStub
){

    @Inject
    lateinit var itauClient: SistemaERPItauClient

    @Inject
    lateinit var bcbClient: SistemaPixBcbClient

    companion object {
        val CLIENTE_ID: UUID = UUID.randomUUID()
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
    fun `Deve registrar uma chave pix valida`(){
        // cenario
        val chavePixValida = chavePixValida()

        `when`(itauClient.buscaContaPorTipo(clienteId = CLIENTE_ID.toString(), tipo = "CONTA_CORRENTE"))
            .thenReturn(HttpResponse.ok(dadosDaContaAssociadaResponse()))

        `when`(bcbClient.registraPix(request = CreatePixKeyRequest(
            keyType = TipoChaveBcb.CPF,
            key = CPF_VALIDO,
            owner = TitularBcbRequest(type = TipoPessoaBcb.NATURAL_PERSON,  name = "Rafael Ponte", taxIdNumber = "63657520325"),
            bankAccount = ContaBcbRequest(participant = ISBP, branch = AGENCIA, accountNumber = NUMERO, accountType = TipoContaBcb.CACC)
        ))).thenReturn(HttpResponse.created(
            CreatePixKeyResponse(
                keyType = TipoChaveBcb.CPF,
                key = CPF_VALIDO,
                bankAccount = ContaBcbResponse(participant = ISBP, branch = AGENCIA, accountNumber = NUMERO, accountType = TipoContaBcb.CACC),
                owner = TitularBcbResponse(type = TipoPessoaBcb.NATURAL_PERSON,  name = "Rafael Ponte", taxIdNumber = "63657520325"),
                createdAt = LocalDateTime.now()
            )
        ))

        // exec
        val chavePixCriada = clientePixGrpc.registra(
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
                chave = CPF_VALIDO,
                conta = dadosDaContaAssociadaResponse().paraConta()
            )
        )
        val chavePixDuplicada = chavePixValida()

        // exec
        // validacao
        val excecao = Assertions.assertThrows(StatusRuntimeException::class.java){
            clientePixGrpc.registra(
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
            clientePixGrpc.registra(
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
            clientePixGrpc.registra(
                chavePixSemContaAssociada
            )
        }

        Assertions.assertEquals(Status.FAILED_PRECONDITION.code, excecao.status.code)
        Assertions.assertEquals(0, chavePixRepository.count())
    }

    @Test
    fun `Nao deve registrar uma chave pix caso a mesma seja valida e esteja presente no sistema BCB`(){
        // cenario
        val chavePixExistenteNoSistemaBcb = chavePixValida()

        `when`(itauClient.buscaContaPorTipo(clienteId = CLIENTE_ID.toString(), tipo = "CONTA_CORRENTE"))
            .thenReturn(HttpResponse.notFound())

        `when`(bcbClient.registraPix(request = CreatePixKeyRequest(
            keyType = TipoChaveBcb.CPF,
            key = CPF_VALIDO,
            owner = TitularBcbRequest(type = TipoPessoaBcb.NATURAL_PERSON,  name = NOME_TITULAR, taxIdNumber = CPF_VALIDO),
            bankAccount = ContaBcbRequest(participant = ISBP, branch = AGENCIA, accountNumber = NUMERO, accountType = TipoContaBcb.CACC)
        ))).thenReturn(HttpResponse.unprocessableEntity())

        // exec
        // validacao
        val excecao = Assertions.assertThrows(StatusRuntimeException::class.java){
            clientePixGrpc.registra(
                chavePixExistenteNoSistemaBcb
            )
        }

        Assertions.assertEquals(Status.FAILED_PRECONDITION.code, excecao.status.code)
        Assertions.assertEquals(0, chavePixRepository.count())
    }


    @MockBean(SistemaERPItauClient::class)
    fun sistemaErpItauClient() : SistemaERPItauClient{
        return Mockito.mock(SistemaERPItauClient::class.java)
    }

    @MockBean(SistemaPixBcbClient::class)
    fun sistemaPixBcbClient() : SistemaPixBcbClient{
        return Mockito.mock(SistemaPixBcbClient::class.java)
    }

    @Factory
    class Clients {

        @Singleton
        fun chavePixGrpcClient(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel)
                : KeyManagerRegistraServiceGrpc.KeyManagerRegistraServiceBlockingStub{
            return KeyManagerRegistraServiceGrpc.newBlockingStub(channel)
        }
    }

    private fun chavePixValida() : RegistraChavePixRequest {
        return RegistraChavePixRequest.newBuilder()
                    .setIdTitular(CLIENTE_ID.toString())
                    .setValorChave(CPF_VALIDO)
                    .setTipoChave(RegistraChavePixRequest.TipoChave.CPF)
                    .setTipoConta(RegistraChavePixRequest.TipoConta.CONTA_CORRENTE)
                .build()
    }

    private fun chavePixTotalmenteInvalida() : RegistraChavePixRequest{
        return RegistraChavePixRequest.newBuilder().build()
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


}