package com.chuckerteam.chucker.internal.support.processors

import com.chuckerteam.chucker.api.datamodel.HttpHeader
import okhttp3.Headers

internal interface CallProcessor<I, O> {

    val maxContentLength: Long

    val headersToRedact: Set<String>
    fun processCall(input: I): O
    fun processHeaders(headers: Headers): List<HttpHeader> =
            headers.map { (name, value) ->
                if (headersToRedact.any { it.equals(name, ignoreCase = true) }) {
                    HttpHeader(name, "**")
                } else {
                    HttpHeader(name, value)
                }
            }
}