package br.com.zup.ot5.chave_pix

import br.com.zup.ot5.compartilhado.model.Conta
import java.util.*
import javax.persistence.*
import javax.validation.Valid
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull


@Entity
class ChavePix(
    @field:NotNull @field:Enumerated(EnumType.STRING) val tipoChave: TipoChave,
    @field:NotEmpty var chave: String,
    @field:NotNull @field:Valid @field:Embedded val conta: Conta
){

    @Id
    @GeneratedValue
    lateinit var id: UUID

    fun naoPertenceAoTitular(outroIdTitular: UUID) : Boolean{
        return conta.titularConta.idTitular != outroIdTitular
    }

}