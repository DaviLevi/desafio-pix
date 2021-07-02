package br.com.zup.ot5.compartilhado.model

import javax.persistence.Embeddable
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

@Embeddable
class Conta(
    @field:NotEmpty private val instituicao: String,
    @field:NotEmpty private val ispb: String,
    @field:NotEmpty private val agencia: String,
    @field:NotEmpty private val numero: String,
    @field:NotNull @field:Enumerated(EnumType.STRING) val tipoConta: TipoConta
)