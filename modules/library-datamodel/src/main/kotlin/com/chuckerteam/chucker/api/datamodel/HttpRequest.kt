package com.chuckerteam.chucker.api.datamodel

data class HttpRequest(
    val url: String,
    val host: String,
    val path: String,
    val scheme: String,
    // Mutable as it can be modified after the request has been sent by other interceptors
    var headersSize: Long,
    // Mutable as it can be modified after the request has been sent by other interceptors
    var redactedHeaders: List<HttpHeader>,
    val method: String,
    val body: HttpBody,
    // Mutable as it's updated once the response lands.
    var date: Long,
    val payloadSize: Long,
    val contentType: String? = null,
    val graphQlDetected: Boolean,
    val graphQlOperationName: String? = null,
)
