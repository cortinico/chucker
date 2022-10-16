package com.chuckerteam.chucker.internal.data.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.chuckerteam.chucker.internal.data.roomentity.RoomHttpTransaction

@Dao
internal interface HttpTransactionsDao {

    @Insert
    suspend fun insert(call: RoomHttpTransaction): Long?

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun update(call: RoomHttpTransaction): Int

    @Query("DELETE FROM transactions")
    suspend fun deleteAll(): Int

    @Query("SELECT * FROM transactions WHERE id = :id")
    fun getById(id: Long): LiveData<RoomHttpTransaction?>

    @Query("DELETE FROM transactions WHERE requestDate <= :threshold")
    suspend fun deleteBefore(threshold: Long): Int

    @Query("SELECT * FROM transactions")
    suspend fun getAll(): List<RoomHttpTransaction>

    @Query("SELECT * FROM transactions WHERE requestDate >= :timestamp")
    fun getTransactionsInTimeRange(timestamp: Long): List<RoomHttpTransaction>

}
