package br.com.zup.ot5.compartilhado.handlers

import br.com.zup.ot5.chave_pix.ChavePixInexistenteException
import com.google.rpc.Code
import com.google.rpc.Status
import javax.inject.Singleton

@Singleton
class ChavePixInexistenteExceptionHandler : CustomExceptionHandler<ChavePixInexistenteException>{

    override fun handle(e: ChavePixInexistenteException): CustomExceptionHandler.StatusWithDetails {

        val status = Status.newBuilder()
            .setCode(Code.NOT_FOUND_VALUE)
            .setMessage(e.message)
            .build()

        return CustomExceptionHandler.StatusWithDetails(status)
    }

    override fun supports(e: Exception): Boolean {
        return e is ChavePixInexistenteException
    }
}