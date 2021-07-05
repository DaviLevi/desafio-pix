package br.com.zup.ot5.chave_pix

import br.com.zup.ot5.chave_pix.cria_chave.CriaChavePixRequestValidavel
import br.com.zup.ot5.chave_pix.exclui_chave.ExcluiChavePixRequestValidavel
import br.com.zup.ot5.integracoes.sistema_erp_itau.SistemaERPItauClient
import br.com.zup.ot5.integracoes.sistema_pix_bcb.CreatePixKeyRequest
import br.com.zup.ot5.integracoes.sistema_pix_bcb.DeletePixKeyRequest
import br.com.zup.ot5.integracoes.sistema_pix_bcb.SistemaPixBcbClient
import io.grpc.Status
import io.micronaut.validation.Validated
import java.util.*
import javax.inject.Singleton
import javax.transaction.Transactional
import javax.validation.Valid

@Singleton
@Validated
class GerenciadorChavePix(
    private val chavePixRepository: ChavePixRepository,
    private val sistemaERPItauClient: SistemaERPItauClient,
    private val sistemaPixBcbClient: SistemaPixBcbClient
){

    @Transactional
    fun criaChave(@Valid chavePixValidavel: CriaChavePixRequestValidavel) : ChavePix{

        // verifica se a chave ja foi utilizada ( duplicada )
        if(chavePixRepository.existsByChave(chavePixValidavel.valor!!)) throw ChavePixDuplicadaException(chavePixValidavel.valor)

        // buscar dados da Conta na API do sistema ERP do Itau
        val response = sistemaERPItauClient.buscaContaPorTipo(chavePixValidavel.idTitular!!, chavePixValidavel.tipoConta!!.name)
        val conta = response.body()?.paraConta() ?: throw IllegalStateException("Esta tentando se associar uma chave pix a uma conta inexistente")

        val novaChavePix = chavePixValidavel.paraPix(conta)

        // registrando a chave no Banco Central do Brasil
        val respostaBcb = sistemaPixBcbClient.registraPix(CreatePixKeyRequest.of(novaChavePix))
        val chavePixRegistadaBcb = respostaBcb.body()?: throw IllegalStateException("Esta chave pix já foi registrada anteriormente no BCB!")

        novaChavePix.chave = chavePixRegistadaBcb.key

        chavePixRepository.save(novaChavePix)

        return novaChavePix
    }

    @Transactional
    fun excluiPix(@Valid excluiChavePixRequestValidavel: ExcluiChavePixRequestValidavel){

        // verifica se a chave existe (NOT FOUND)
        val chavePixASerExcluida = chavePixRepository.findById(UUID.fromString(excluiChavePixRequestValidavel.pixId))
                                                     .orElseThrow{throw ChavePixInexistenteException(excluiChavePixRequestValidavel.pixId)}

        val idTitularSolicitante = UUID.fromString(excluiChavePixRequestValidavel.idTitular)

        // verifica que o dono da chave seja o solicitante da remoção (PERMISSION_DENIED)
        if(chavePixASerExcluida.naoPertenceAoTitular(idTitularSolicitante)) throw TitularChavePixDivergenteException()

        // exclui a chave pix do sistema BCB
        sistemaPixBcbClient.excluiPix(chavePixASerExcluida.chave, DeletePixKeyRequest(
            key = chavePixASerExcluida.chave,
            participant = chavePixASerExcluida.conta.ispb
        )).body() ?: throw IllegalStateException("Acao proibida ou chave pix não encontrada no sistema BCB")

        chavePixRepository.deleteById(chavePixASerExcluida.id)
    }

}