package com.chuckerteam.chucker.api.datamodel

data class HttpTransactionTuple(
        var status: HttpTransactionStatus,
        val requestDate: Long,
        val tookMs: Long,
        val protocol: String,
        val method: String,
        val host: String,
        val path: String,
        val scheme: String,
        val code: Int,
        val requestPayloadSize: Long,
        val responsePayloadSize: Long,
        val errorMessage: String?,
        val graphQlDetected: Boolean,
        val graphQlOperationName: String?,
)