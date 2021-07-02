package br.com.zup.ot5.compartilhado.validadores

import br.com.zup.ot5.chave_pix.cria_chave.CriaChavePixRequestValidavel
import io.micronaut.core.annotation.AnnotationValue
import io.micronaut.validation.validator.constraints.ConstraintValidator
import io.micronaut.validation.validator.constraints.ConstraintValidatorContext

class ChavePixValidaValidator : ConstraintValidator<ChavePixValida, CriaChavePixRequestValidavel> {
    override fun isValid(
        value: CriaChavePixRequestValidavel,
        annotationMetadata: AnnotationValue<ChavePixValida>,
        context: ConstraintValidatorContext
    ): Boolean {

        if(value.tipoChave == null) return false

        return value.tipoChave.valida(value.valor)
    }
}
