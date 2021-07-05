package br.com.zup.ot5.chave_pix.cria_chave

import br.com.zup.ot5.chave_pix.ChavePix
import br.com.zup.ot5.chave_pix.TipoChave
import br.com.zup.ot5.compartilhado.model.Conta
import br.com.zup.ot5.compartilhado.model.TipoConta
import br.com.zup.ot5.compartilhado.validadores.ChavePixValida
import br.com.zup.ot5.compartilhado.validadores.UUIDValido
import io.micronaut.core.annotation.Introspected
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@Introspected
@ChavePixValida
data class CriaChavePixRequestValidavel(
    @field:NotNull val tipoChave: TipoChave?,
    @field:NotEmpty @field:UUIDValido val idTitular: String?,
    @field:Size(max = 77) val valor: String?,
    @field:NotNull val tipoConta: TipoConta?
){
    fun paraPix(conta: Conta) : ChavePix {
        return ChavePix(
            tipoChave = tipoChave!!,
            chave = valor?: "",
            conta = conta
        )
    }
}