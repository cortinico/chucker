package com.chuckerteam.chucker.api.datamodel

data class HttpTransaction(
        var status: HttpTransactionStatus = HttpTransactionStatus.Created,
        var errorMessage: String? = null,
        var request: HttpRequest? = null,
        var response: HttpResponse? = null,
)

