package br.com.zup.ot5.integracoes.sistema_pix_bcb

data class DeletePixKeyRequest(
    val key: String,
    val participant: String
)
