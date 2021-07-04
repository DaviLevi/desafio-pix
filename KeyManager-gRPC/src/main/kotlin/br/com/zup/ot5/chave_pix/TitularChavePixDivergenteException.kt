package br.com.zup.ot5.chave_pix

class TitularChavePixDivergenteException(
) : RuntimeException("Uma chave pix pode ser removida somente pelo seu dono")