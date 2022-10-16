package com.chuckerteam.chucker.internal.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.chuckerteam.chucker.internal.data.roomentity.RoomHttpRequest
import com.chuckerteam.chucker.internal.data.roomentity.RoomHttpResponse
import com.chuckerteam.chucker.internal.data.roomentity.RoomHttpTransaction

@Database(entities = [RoomHttpTransaction::class, RoomHttpRequest::class, RoomHttpResponse::class],
        version = 10,
        exportSchema = false)
internal abstract class ChuckerDatabase : RoomDatabase() {

    abstract fun transactionsDao(): HttpTransactionsDao

    companion object {
        private const val DB_NAME = "chucker.db"

        fun create(applicationContext: Context): ChuckerDatabase {
            return Room.databaseBuilder(applicationContext, ChuckerDatabase::class.java, DB_NAME)
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}
