package br.com.zup.ot5.compartilhado.model

import javax.persistence.Embeddable
import javax.persistence.Embedded
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.validation.Valid
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

@Embeddable
class Conta(
    @field:NotEmpty val instituicao: String,
    @field:NotEmpty val ispb: String,
    @field:NotEmpty val agencia: String,
    @field:NotEmpty val numero: String,
    @field:NotNull @field:Enumerated(EnumType.STRING) val tipoConta: TipoConta,
    @field:NotNull @field:Valid @field:Embedded val titularConta: TitularConta
)