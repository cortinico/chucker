package com.chuckerteam.chucker.api.datamodel

data class HttpCall(
    val id: Long,
    val url: String,
    val host: String,
    val path: String,
    val scheme: String,
    val error: String,
    val method: String,
    val request: HttpRequest,
    val response: HttpResponse,
    val tookMs: Long,
    val protocol: String
)
