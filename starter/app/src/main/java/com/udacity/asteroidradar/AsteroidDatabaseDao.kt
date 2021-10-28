package com.udacity.asteroidradar

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface AsteroidDatabaseDao{
    @Insert
    suspend fun insert(asteroid: Asteroid)

    @Update
    suspend fun update(asteroid: Asteroid)

    @Query("SELECT * FROM asteroid ORDER BY id")
    suspend fun getAllAsteroids():List<Asteroid>?

}