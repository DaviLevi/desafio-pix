package br.com.zup.ot5.compartilhado.validadores

import io.micronaut.core.annotation.AnnotationValue
import io.micronaut.validation.validator.constraints.ConstraintValidator
import io.micronaut.validation.validator.constraints.ConstraintValidatorContext
import java.util.*

class UUIDValidoValidator : ConstraintValidator<UUIDValido, String> {
    override fun isValid(
        value: String?,
        annotationMetadata: AnnotationValue<UUIDValido>,
        context: ConstraintValidatorContext
    ): Boolean {
        return try{
            UUID.fromString(value)
            true;
        }catch(e: IllegalArgumentException){
            false;
        }
    }
}
