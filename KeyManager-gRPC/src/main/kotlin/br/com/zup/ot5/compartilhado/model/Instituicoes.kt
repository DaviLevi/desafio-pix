package br.com.zup.ot5.compartilhado.model

import java.lang.IllegalStateException

class Instituicoes {

    companion object{
        private val ispbs = mapOf<String, Long>(
            "ITAÚ UNIBANCO S.A." to 60701190
        )

        fun ispbDe(instituicao: String): Long {
            return ispbs[instituicao] ?: throw IllegalStateException("Instituicao desconhecida")
        }
    }
}