package com.shweta.votecaster.databse

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class UserEntity (
    @PrimaryKey val mobile: String,
    @ColumnInfo(name = "image") val image: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "enroll") val enroll: String,
    @ColumnInfo(name = "email") val email: String,
    @ColumnInfo(name = "year") val year: String,
    @ColumnInfo(name = "class") val sClass: String,
    @ColumnInfo(name = "pass") val pass: String
)