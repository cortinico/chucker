package com.chuckerteam.chucker.api.datamodel

data class HttpResponse(
    val id: Long,
    val method: String,
    val redactedHeaders: List<HttpHeader>,
    val unredactedHeadersByteSize: Long,
    val body: HttpBody,
    val date: Long,
    val tlsVersion: String,
    val cipherSuite: String,
    val code: Int,
    val message: String,
    val payloadSize: Long,
    val contentType: String,
    val headers: String,
    val headersSize: Long,
    val isResponseBodyEncoded: Boolean = false,
    val responseImageData: ByteArray
)
