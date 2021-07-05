package br.com.zup.ot5.chave_pix.cria_chave

import br.com.zup.ot5.KeyManagerRegistraServiceGrpc
import br.com.zup.ot5.RegistraChavePixRequest
import br.com.zup.ot5.RegistraChavePixResponse
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
) : KeyManagerRegistraServiceGrpc.KeyManagerRegistraServiceImplBase(){

    override fun registra(request: RegistraChavePixRequest,
                              responseObserver: StreamObserver<RegistraChavePixResponse>?) {

        val chavePixValidavel : CriaChavePixRequestValidavel = request.paraChavePixValidavel()
        val chavePixCriada = gerenciadorChavePix.criaChave(chavePixValidavel)

        responseObserver?.onNext(
            RegistraChavePixResponse
                .newBuilder()
                .setIdTitular(chavePixCriada.conta.titularConta.idTitular.toString())
                .setPixId(chavePixCriada.id.toString())
                .build()
        )
        responseObserver?.onCompleted()
    }


    private fun RegistraChavePixRequest.paraChavePixValidavel() : CriaChavePixRequestValidavel{
        return CriaChavePixRequestValidavel(tipoChave = if (this.tipoChave == RegistraChavePixRequest.TipoChave.TIPO_CHAVE_DESCONHECIDO) null else TipoChave.valueOf(this.tipoChave.name),
            idTitular = this.idTitular,
            valor = this.valorChave,
            tipoConta = if (this.tipoConta == RegistraChavePixRequest.TipoConta.TIPO_CONTA_DESCONHECIDO) null else TipoConta.valueOf(this.tipoConta.name)
        )
    }

}