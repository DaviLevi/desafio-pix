package br.com.zup.ot5.compartilhado.model

import br.com.zup.ot5.integracoes.sistema_pix_bcb.TipoContaBcb

enum class TipoConta {
    CONTA_CORRENTE {
        override fun paraTipoContaBcb(): TipoContaBcb {
            return TipoContaBcb.CACC
        }
    }, CONTA_POUPANCA {
        override fun paraTipoContaBcb(): TipoContaBcb {
            return TipoContaBcb.SVGS
        }
    };


    abstract fun paraTipoContaBcb() : TipoContaBcb
}