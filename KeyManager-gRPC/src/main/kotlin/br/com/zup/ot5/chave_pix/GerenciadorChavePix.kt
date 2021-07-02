package br.com.zup.ot5.chave_pix

import br.com.zup.ot5.chave_pix.cria_chave.CriaChavePixRequestValidavel
import br.com.zup.ot5.integracoes.sistema_erp_itau.SistemaERPItauClient
import io.micronaut.validation.Validated
import javax.inject.Singleton
import javax.transaction.Transactional
import javax.validation.Valid

@Singleton
@Validated
class GerenciadorChavePix(
    private val chavePixRepository: ChavePixRepository,
    private val sistemaERPItauClient: SistemaERPItauClient
){

    @Transactional
    fun criaChave(@Valid chavePixValidavel: CriaChavePixRequestValidavel) : ChavePix{

        // verifica se a chave ja foi utilizada ( duplicada )
        if(chavePixRepository.existsByChave(chavePixValidavel.valor!!)) throw ChavePixDuplicadaException(chavePixValidavel.valor)


        // buscar dados da Conta na API do sistema ERP do Itau
        val response = sistemaERPItauClient.buscaContaPorTipo(chavePixValidavel.idTitular!!, chavePixValidavel.tipoConta!!.name)
        val conta = response.body()?.paraConta() ?: throw IllegalStateException("Esta tentando se associar uma chave pix a uma conta inexistente")

        val novaChavePix = chavePixValidavel.paraPix(conta)

        chavePixRepository.save(novaChavePix)

        return novaChavePix
    }

}