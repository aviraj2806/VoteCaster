package com.shweta.votecaster.databse

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [VoteEntity::class], version = 1)
abstract class VoteDatabase : RoomDatabase(){
    abstract fun voteDao(): VoteDao
}