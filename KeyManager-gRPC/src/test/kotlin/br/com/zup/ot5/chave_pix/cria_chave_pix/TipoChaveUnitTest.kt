package br.com.zup.ot5.chave_pix.cria_chave_pix

import br.com.zup.ot5.chave_pix.TipoChave
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class TipoChaveUnitTest {



    @Nested
    inner class ALEATORIA {

        @Test
        fun `Deve ser considerado valido um valor nulo para tipo chave aleatoria`() {
            with(TipoChave.ALEATORIA) {
                Assertions.assertTrue(valida(null))
            }
        }

        @Test
        fun `Nao deve ser considerado valido um valor nao nulo para tipo chave aleatoria`() {
            with(TipoChave.ALEATORIA) {
                Assertions.assertFalse(valida("algum valor"))
                Assertions.assertFalse(valida(""))
                Assertions.assertFalse(valida("   "))
            }
        }
    }

    @Nested
    inner class CPF {

        @Test
        fun `Deve ser considerado valido um numero valido de CPF`() {
            with(TipoChave.CPF) {
                Assertions.assertTrue(valida("01606156233"))
            }
        }

        @Test
        fun `Nao deve ser considerado valido um valor alfanumerico ou nao numerico`() {
            with(TipoChave.CPF) {
                Assertions.assertFalse(valida("123ASBFGH"))
                Assertions.assertFalse(valida(""))
                Assertions.assertFalse(valida("   "))
            }
        }

        @Test
        fun `Nao deve ser considerado valido um valor nulo ou vazio`() {
            with(TipoChave.CPF) {
                Assertions.assertFalse(valida(null))
                Assertions.assertFalse(valida(""))
                Assertions.assertFalse(valida("   "))
            }
        }

        @Test
        fun `Nao deve ser considerado valido um numero invalido de CPF`() {
            with(TipoChave.CPF) {
                Assertions.assertFalse(valida("33666622244"))
            }
        }
    }

    @Nested
    inner class TELEFONE_CELULAR {

        @Test
        fun `Deve ser considerado valido um numero celular valido`() {
            with(TipoChave.TELEFONE_CELULAR) {
                Assertions.assertTrue(valida("+5534998807471"))
            }
        }

        @Test
        fun `Nao deve ser considerado valido um numero de telefone invalido`() {
            with(TipoChave.TELEFONE_CELULAR) {
                Assertions.assertFalse(valida("998807471"))
                Assertions.assertFalse(valida("+5534998807471111111111111"))
                Assertions.assertFalse(valida("+5534998AAA"))
            }
        }

        @Test
        fun `Nao deve ser considerado valido um valor nulo ou vazio`() {
            with(TipoChave.TELEFONE_CELULAR) {
                Assertions.assertFalse(valida(null))
                Assertions.assertFalse(valida(""))
                Assertions.assertFalse(valida("   "))
            }
        }
    }

    @Nested
    inner class EMAIL {

        @Test
        fun `Deve ser considerado valido um endereco valido para tipo chave email`() {
            with(TipoChave.EMAIL) {
                Assertions.assertTrue(valida("davide.sgalambro@gmail.com"))
            }
        }

        @Test
        fun `Nao deve ser considerado valido um endereco invalido para tipo chave email`() {
            with(TipoChave.EMAIL) {
                Assertions.assertFalse(valida("davide@.com"))
                Assertions.assertFalse(valida("davidegmail.com"))
                Assertions.assertFalse(valida("   "))
                Assertions.assertFalse(valida(""))
                Assertions.assertFalse(valida(null))
            }
        }
    }



}