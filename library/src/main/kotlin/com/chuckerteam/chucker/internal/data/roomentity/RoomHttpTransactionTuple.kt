package com.chuckerteam.chucker.internal.data.roomentity

import com.chuckerteam.chucker.api.datamodel.HttpTransactionStatus
internal data class RoomHttpTransactionTuple(
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
