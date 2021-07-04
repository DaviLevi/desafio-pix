package br.com.zup.ot5.compartilhado.handlers

import br.com.zup.ot5.chave_pix.TitularChavePixDivergenteException
import com.google.rpc.Code
import com.google.rpc.Status
import javax.inject.Singleton

@Singleton
class TitularChavePixDivergenteExceptionHandler : CustomExceptionHandler<TitularChavePixDivergenteException>{

    override fun handle(e: TitularChavePixDivergenteException): CustomExceptionHandler.StatusWithDetails {

        val status = Status.newBuilder()
            .setCode(Code.PERMISSION_DENIED_VALUE)
            .setMessage(e.message)
            .build()

        return CustomExceptionHandler.StatusWithDetails(status)
    }

    override fun supports(e: Exception): Boolean {
        return e is TitularChavePixDivergenteException
    }
}