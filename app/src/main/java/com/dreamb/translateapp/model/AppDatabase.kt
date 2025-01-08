package com.dreamb.translateapp.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Sentence::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun sentenceDao(): SentenceDao
}


object MyDb {
    @Volatile  //메인 메모리에 올림
    private var instance: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
        return instance ?: synchronized(this) {     //동기화 처리
            Room.databaseBuilder(
                context,
                AppDatabase::class.java, "room.db"
            )
                .build()
                .also { instance = it }    //also가 무엇인지 알아보기
        }
    }

}