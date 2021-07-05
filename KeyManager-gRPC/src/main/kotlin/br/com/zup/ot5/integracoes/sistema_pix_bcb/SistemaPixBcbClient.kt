package br.com.zup.ot5.integracoes.sistema_pix_bcb

import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Delete
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.Post
import io.micronaut.http.client.annotation.Client

@Client(value = "http://localhost:8082")
interface SistemaPixBcbClient {

    @Post(uri = "/api/v1/pix/keys", consumes = [MediaType.APPLICATION_XML], produces = [MediaType.APPLICATION_XML])
    fun registraPix(@Body request: CreatePixKeyRequest) : HttpResponse<CreatePixKeyResponse>

    @Delete(uri = "/api/v1/pix/keys/{key}", consumes = [MediaType.APPLICATION_XML], produces = [MediaType.APPLICATION_XML])
    fun excluiPix(@PathVariable key: String,@Body request: DeletePixKeyRequest) : HttpResponse<DeletePixKeyResponse>
}