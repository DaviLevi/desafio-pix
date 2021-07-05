package br.com.zup.ot5.chave_pix.exclui_chave

import br.com.zup.ot5.chave_pix.GerenciadorChavePix
import br.com.zup.ot5.compartilhado.interceptors.ErrorHandler
import br.com.zup.ot5.ExcluiChavePixRequest
import br.com.zup.ot5.ExcluiChavePixResponse
import br.com.zup.ot5.KeyManagerExcluiServiceGrpc
import io.grpc.stub.StreamObserver
import javax.inject.Singleton

@Singleton
@ErrorHandler
class ExcluiPixGrpcEndpoint(
    private val gerenciadorChavePix: GerenciadorChavePix
) : KeyManagerExcluiServiceGrpc.KeyManagerExcluiServiceImplBase(){


    override fun excluiChavePix(request: ExcluiChavePixRequest,
                              responseObserver: StreamObserver<ExcluiChavePixResponse>?) {

        val requestExclusaoValidavel = request.paraRequestValidavel()

        gerenciadorChavePix.excluiPix(requestExclusaoValidavel)

        responseObserver?.onNext(ExcluiChavePixResponse.newBuilder().build())
        responseObserver?.onCompleted()
    }


    private fun ExcluiChavePixRequest.paraRequestValidavel() : ExcluiChavePixRequestValidavel {
        return ExcluiChavePixRequestValidavel(
            pixId = pixId,
            idTitular = idTitular
        )
    }

}