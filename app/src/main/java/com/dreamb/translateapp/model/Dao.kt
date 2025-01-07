package com.dreamb.translateapp.model

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SentenceDao {
    @Query("SELECT * FROM sentence")
    fun getAll(): Flow<List<Sentence>>

    @Insert
    fun insertAll(vararg users: Sentence)

    @Delete
    fun delete(user: Sentence)

}