package br.com.zup.ot5.chave_pix

class PermissaoInsuficienteException(
    chave : String
) : RuntimeException("Chave pix '$chave' já foi utilizada")