package com.shweta.votecaster.databse

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vote")
data class VoteEntity (
    @PrimaryKey val mobile: String,
    @ColumnInfo(name = "vote_to_id") val vote_to_id: Int,
    @ColumnInfo(name = "vote_to_name") val vote_to_name: String
)