package br.com.zup.ot5.chave_pix

class ChavePixDuplicadaException(
    chave : String
) : RuntimeException("Chave pix '$chave' já foi utilizada")