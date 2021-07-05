package br.com.zup.ot5.integracoes.sistema_erp_itau

import br.com.zup.ot5.compartilhado.model.Conta
import br.com.zup.ot5.compartilhado.model.TipoConta
import br.com.zup.ot5.compartilhado.model.TitularConta
import java.util.*

enum class TipoContaResponse{
    CONTA_CORRENTE, CONTA_POUPANCA
}

data class InstituicaoResponse(
    val nome: String,
    val ispb: String
)

data class TitularResponse(
    val id: UUID,
    val nome: String,
    val cpf: String
)

data class ContaResponse(
    val tipo: TipoContaResponse,
    val instituicao: InstituicaoResponse,
    val agencia: String,
    val numero: String,
    val titular: TitularResponse
){
    fun paraConta(): Conta {
        return Conta(
            instituicao = instituicao.nome,
            ispb = instituicao.ispb,
            agencia = agencia,
            numero = numero,
            tipoConta = TipoConta.valueOf(tipo.name),
            titularConta = TitularConta(idTitular = titular.id, nomeTitular = titular.nome, cpfTitular = titular.cpf)
        )
    }
}