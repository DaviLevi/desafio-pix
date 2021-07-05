package br.com.zup.ot5.integracoes.sistema_pix_bcb

import java.time.LocalDateTime

data class CreatePixKeyResponse(
    val keyType : TipoChaveBcb,
    val key: String,
    val bankAccount: ContaBcbResponse,
    val owner: TitularBcbResponse,
    val createdAt: LocalDateTime
)


data class TitularBcbResponse(
    val type: TipoPessoaBcb,
    val name: String,
    val taxIdNumber: String
)



data class ContaBcbResponse(
    val participant : String,
    val branch: String,
    val accountNumber: String,
    val accountType: TipoContaBcb
)




//<CreatePixKeyResponse>
//    <keyType>CPF</keyType>
//    <key>string</key>
//    <bankAccount>
//        <participant>string</participant>
//        <branch>string</branch>
//        <accountNumber>string</accountNumber>
//        <accountType>CACC</accountType>
//    </bankAccount>
//    <owner>
//        <type>NATURAL_PERSON</type>
//        <name>string</name>
//        <taxIdNumber>string</taxIdNumber>
//    </owner>
//    <createdAt>2021-07-04T15:09:12.465Z</createdAt>
//</CreatePixKeyResponse>