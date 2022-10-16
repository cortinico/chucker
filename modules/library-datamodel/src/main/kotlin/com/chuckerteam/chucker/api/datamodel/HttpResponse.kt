package com.chuckerteam.chucker.api.datamodel

data class HttpResponse(
    // Mutable as the body will be populated once it has been processed by the whole interceptor chain.
    var body: HttpBody? = null,
    val date: Long,
    var headersSize: Long,
    val protocol: String,
    val code: Int,
    var redactedHeaders: List<HttpHeader>,
    val tlsVersion: String? = null,
    val cipherSuite: String? = null,
    val message: String,
    val contentType: String? = null,
    // TODO Can be computed
    val tookMs: Long,
)
