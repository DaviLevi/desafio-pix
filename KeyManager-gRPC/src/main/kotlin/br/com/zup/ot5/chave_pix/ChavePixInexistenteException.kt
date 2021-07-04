package br.com.zup.ot5.chave_pix

class ChavePixInexistenteException(
    chave : String
) : RuntimeException("Chave pix '$chave' inexistente")