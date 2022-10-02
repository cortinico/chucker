package com.chuckerteam.chucker.api.datamodel

data class HttpRequest(
    val id: Long,
    val redactedHeaders: List<HttpHeader>,
    val unredactedHeadersByteSize: Long,
    val body: HttpBody,
    val date: Long,
    val payloadSize: Long,
    val contentType: String,
    val headers: String,
    val headersSize: Long,
    val isRequestBodyEncoded: Boolean = false,
    val graphQlDetected: Boolean = false,
    val graphQlOperationName: String
)
