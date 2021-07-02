package br.com.zup.ot5.chave_pix.cria_chave

import br.com.zup.ot5.CriaChavePixRequest
import br.com.zup.ot5.CriaChavePixResponse
import br.com.zup.ot5.KeyManagerGRPCServiceGrpc
import br.com.zup.ot5.chave_pix.GerenciadorChavePix
import br.com.zup.ot5.chave_pix.TipoChave
import br.com.zup.ot5.compartilhado.interceptors.ErrorHandler
import br.com.zup.ot5.compartilhado.model.TipoConta
import io.grpc.stub.StreamObserver
import javax.inject.Singleton


@Singleton
@ErrorHandler
class CriaPixGrpcEndpoint(
    private val gerenciadorChavePix: GerenciadorChavePix
) : KeyManagerGRPCServiceGrpc.KeyManagerGRPCServiceImplBase(){


    override fun criaChavePix(request: CriaChavePixRequest,
                              responseObserver: StreamObserver<CriaChavePixResponse>?) {

        val chavePixValidavel : CriaChavePixRequestValidavel = request.paraChavePixValidavel()
        val chavePixCriada = gerenciadorChavePix.criaChave(chavePixValidavel)

        responseObserver?.onNext(
             CriaChavePixResponse
                 .newBuilder()
                    .setIdTitular(chavePixCriada.idTitular.toString())
                    .setPixId(chavePixCriada.id.toString())
                 .build()
        )
        responseObserver?.onCompleted()
    }


    private fun CriaChavePixRequest.paraChavePixValidavel() : CriaChavePixRequestValidavel{
        return CriaChavePixRequestValidavel(tipoChave = if (this.tipoChave == CriaChavePixRequest.TipoChave.TIPO_CHAVE_DESCONHECIDO) null else TipoChave.valueOf(this.tipoChave.name),
            idTitular = this.idTitular,
            valor = this.valorChave,
            tipoConta = if (this.tipoConta == CriaChavePixRequest.TipoConta.TIPO_CONTA_DESCONHECIDO) null else TipoConta.valueOf(this.tipoConta.name)
        )
    }

}