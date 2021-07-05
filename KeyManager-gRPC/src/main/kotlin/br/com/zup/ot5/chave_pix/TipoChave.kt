package br.com.zup.ot5.chave_pix

import br.com.zup.ot5.integracoes.sistema_pix_bcb.TipoChaveBcb
import org.hibernate.validator.internal.constraintvalidators.hv.EmailValidator
import org.hibernate.validator.internal.constraintvalidators.hv.br.CPFValidator

enum class TipoChave {
    CPF{
        override fun valida(chave: String?): Boolean {
            if (chave.isNullOrBlank()) {
                return false
            }

            if (!chave.matches("[0-9]+".toRegex())) {
                return false
            }

            return CPFValidator().run {
                initialize(null)
                isValid(chave, null)
            }
        }

        override fun paraTipoChaveBcb(): TipoChaveBcb {
            return TipoChaveBcb.CPF
        }
    },
    TELEFONE_CELULAR {
        override fun valida(chave: String?): Boolean {
            if(chave.isNullOrBlank()) return false
            return chave.matches("^\\+[1-9][0-9]\\d{1,14}\$".toRegex())
        }

        override fun paraTipoChaveBcb(): TipoChaveBcb {
            return TipoChaveBcb.PHONE
        }
    },
    EMAIL {
        override fun valida(chave: String?): Boolean {
            if(chave.isNullOrBlank()){
                return false
            }
            return EmailValidator().run {
                initialize(null)
                isValid(chave, null)
            }
        }

        override fun paraTipoChaveBcb(): TipoChaveBcb {
            return TipoChaveBcb.EMAIL
        }
    },
    ALEATORIA {
        override fun valida(chave: String?): Boolean {
            return chave.isNullOrBlank()
        }

        override fun paraTipoChaveBcb(): TipoChaveBcb {
            return TipoChaveBcb.RANDOM
        }
    };

    abstract fun valida(chave: String?) : Boolean
    abstract fun paraTipoChaveBcb() : TipoChaveBcb
}