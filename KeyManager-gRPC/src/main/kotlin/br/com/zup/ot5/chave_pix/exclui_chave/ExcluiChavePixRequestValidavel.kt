package br.com.zup.ot5.chave_pix.exclui_chave

import br.com.zup.ot5.compartilhado.validadores.UUIDValido
import io.micronaut.core.annotation.Introspected
import javax.validation.constraints.NotEmpty

@Introspected
data class ExcluiChavePixRequestValidavel(
    @field:UUIDValido @field:NotEmpty val pixId: String,
    @field:UUIDValido @field:NotEmpty val idTitular: String,
)