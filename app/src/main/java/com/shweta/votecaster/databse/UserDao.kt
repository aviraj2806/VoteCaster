package com.shweta.votecaster.databse

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UserDao {

    @Insert
    fun insertStudent(userEntity: UserEntity)

    @Query("SELECT * FROM user WHERE mobile = :mobile")
    fun getStudentByMobile(mobile: String): UserEntity

    @Query("SELECT * FROM user")
    fun getAllStudents(): List<UserEntity>

    @Query("UPDATE user SET class =:sClass  WHERE mobile =:mobile")
    fun editUserClass(sClass: String,mobile: String)

    @Query("UPDATE user SET year =:year  WHERE mobile =:mobile")
    fun editUserYear(year: String,mobile: String)
}