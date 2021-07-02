package br.com.zup.ot5.compartilhado.handlers

import br.com.zup.ot5.chave_pix.ChavePixDuplicadaException
import com.google.rpc.Code
import com.google.rpc.Status
import javax.inject.Singleton

@Singleton
class ChavePixDuplicadaExceptionHandler : CustomExceptionHandler<ChavePixDuplicadaException>{

    override fun handle(e: ChavePixDuplicadaException): CustomExceptionHandler.StatusWithDetails {

        val status = Status.newBuilder()
            .setCode(Code.ALREADY_EXISTS_VALUE)
            .setMessage(e.message)
            .build()

        return CustomExceptionHandler.StatusWithDetails(status)
    }

    override fun supports(e: Exception): Boolean {
        return e is ChavePixDuplicadaException
    }
}