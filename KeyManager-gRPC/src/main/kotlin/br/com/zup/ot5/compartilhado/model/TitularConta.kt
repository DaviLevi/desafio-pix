package br.com.zup.ot5.compartilhado.model

import java.util.*
import javax.persistence.Embeddable
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

@Embeddable
class TitularConta(
    @field:NotNull var idTitular: UUID,
    @field:NotEmpty var nomeTitular: String,
    @field:NotEmpty var cpfTitular: String
)