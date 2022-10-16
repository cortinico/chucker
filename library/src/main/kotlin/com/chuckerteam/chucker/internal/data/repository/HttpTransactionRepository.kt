package com.chuckerteam.chucker.internal.data.repository

import androidx.lifecycle.LiveData
import com.chuckerteam.chucker.api.datamodel.HttpTransaction
import com.chuckerteam.chucker.api.datamodel.HttpRequest
import com.chuckerteam.chucker.api.datamodel.HttpResponse
import com.chuckerteam.chucker.internal.data.roomentity.RoomHttpTransaction
import com.chuckerteam.chucker.internal.data.roomentity.RoomHttpTransactionTuple

/**
 * Repository Interface representing all the operations that are needed to let Chucker work
 * with [RoomHttpTransaction] and [RoomHttpCallTuple]. Please use [RoomHttpCallDatabaseRepository] that
 * uses Room and SqLite to run those operations.
 */
internal interface HttpTransactionRepository {

    suspend fun insertTransaction(transaction: HttpTransaction)
    suspend fun updateTransaction(transaction: HttpTransaction): Int
    suspend fun insertRequest(request: HttpRequest)
    suspend fun updateRequest(request: HttpRequest): Int
    suspend fun insertResponse(response: HttpResponse)
    suspend fun updateResponse(response: HttpResponse): Int
    suspend fun deleteOldTransactions(threshold: Long)
    suspend fun deleteAllTransactions()

    fun getSortedTransactionTuples(): LiveData<List<RoomHttpTransactionTuple>>
    fun getFilteredTransactionTuples(code: String, path: String): LiveData<List<RoomHttpTransactionTuple>>

    fun getTransaction(transactionId: Long): LiveData<HttpTransaction?>

    suspend fun getAllTransactions(): List<HttpTransaction>

    fun getTransactionsInTimeRange(minTimestamp: Long?): List<HttpTransaction>
}
