package br.com.zup.ot5.compartilhado.validadores

import javax.validation.Constraint
import javax.validation.Payload
import kotlin.reflect.KClass

@Target(AnnotationTarget.CLASS)
@MustBeDocumented
@Constraint(validatedBy = [ChavePixValidaValidator::class])
annotation class ChavePixValida(
    val message: String = "Chave pix invalida ou incompativel com o tipo chave informado",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)
