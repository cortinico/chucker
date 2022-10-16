package com.chuckerteam.chucker.internal.data.repository

import androidx.lifecycle.LiveData
import com.chuckerteam.chucker.api.datamodel.HttpTransaction
import com.chuckerteam.chucker.api.datamodel.HttpRequest
import com.chuckerteam.chucker.api.datamodel.HttpResponse
import com.chuckerteam.chucker.internal.data.room.ChuckerDatabase
import com.chuckerteam.chucker.internal.data.roomentity.RoomHttpTransaction

internal class HttpTransactionDatabaseRepository(private val database: ChuckerDatabase) :
    HttpTransactionRepository {

    private val transactionsDao get() = database.transactionsDao()

    override fun getFilteredTransactionTuples(code: String, path: String): LiveData<List<HttpTransactionTuple>> {
        val pathQuery = if (path.isNotEmpty()) "%$path%" else "%"
        return transactionsDao.getFilteredTuples("$code%", pathQuery)
    }

    override fun getTransaction(transactionId: Long): LiveData<HttpTransaction?> {
        return transactionsDao.getById(transactionId)
            .distinctUntilChanged { old, new -> old?.hasTheSameContent(new) != false }
    }

    override fun getSortedTransactionTuples(): LiveData<List<HttpTransactionTuple>> {
        return transactionsDao.getSortedTuples()
    }

    override suspend fun deleteAllTransactions() {
        transactionsDao.deleteAll()
    }

    override suspend fun insertTransaction(transaction: HttpTransaction) {
        val id = transactionsDao.insert(transaction)
        transaction.id = id ?: 0
    }

    override suspend fun updateTransaction(transaction: HttpTransaction): Int {
        return transactionsDao.update(transaction)
    }

    override suspend fun deleteOldTransactions(threshold: Long) {
        transactionsDao.deleteBefore(threshold)
    }

    override suspend fun getAllTransactions(): List<HttpTransaction> = transactionsDao.getAll()

    override fun getTransactionsInTimeRange(minTimestamp: Long?): List<HttpTransaction> {
        val timestamp = minTimestamp ?: 0L
        return transactionsDao.getTransactionsInTimeRange(timestamp)
    }

    override suspend fun insertTransaction(call: HttpTransaction) {
        TODO("Not yet implemented")
    }

    override suspend fun updateTransaction(call: HttpTransaction): Int {
        TODO("Not yet implemented")
    }

    override suspend fun insertRequest(request: HttpRequest) {
        TODO("Not yet implemented")
    }

    override suspend fun updateRequest(request: HttpRequest): Int {
        TODO("Not yet implemented")
    }

    override suspend fun insertResponse(response: HttpResponse) {
        TODO("Not yet implemented")
    }

    override suspend fun updateResponse(response: HttpResponse): Int {
        TODO("Not yet implemented")
    }

    override suspend fun deleteOldCalls(threshold: Long) {
        transactionsDao.deleteBefore(threshold)
    }

    override suspend fun deleteAllCalls() {
        transactionsDao.deleteAll()
    }

    override fun getCall(callId: Long): LiveData<RoomHttpTransaction?> {
        return transactionsDao.getById(callId)
        // TODO
//            .distinctUntilChanged { old, new -> old?.hasTheSameContent(new) != false }
    }

    override suspend fun getAllCalls(): List<RoomHttpTransaction> = transactionsDao.getAll()

    override fun getCallsInTimeRange(minTimestamp: Long?): List<RoomHttpTransaction> {
        val timestamp = minTimestamp ?: 0L
        return transactionsDao.getTransactionsInTimeRange(timestamp)
    }
}
