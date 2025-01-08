package com.dreamb.translateapp.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Sentence(
    @PrimaryKey(autoGenerate = true) val uid: Int = 0,
    @ColumnInfo(name = "oriSentence") val oriSentence: String?,
    @ColumnInfo(name = "transSentence") val transSentence: String?,
)