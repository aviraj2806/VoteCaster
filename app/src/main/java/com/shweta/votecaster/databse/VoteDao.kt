package com.shweta.votecaster.databse

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface VoteDao {

    @Insert
    fun insertVote(voteEntity: VoteEntity)

    @Query("SELECT * FROM vote")
    fun getAllVotes(): List<VoteEntity>

    @Query("SELECT * FROM vote WHERE mobile =:mobile")
    fun isVoted(mobile: String): VoteEntity

    @Query("SELECT * FROM vote WHERE vote_to_id = :id")
    fun getVotesById(id: Int): List<VoteEntity>
}