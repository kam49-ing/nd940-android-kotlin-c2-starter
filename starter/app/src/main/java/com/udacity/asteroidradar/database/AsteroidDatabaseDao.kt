package com.udacity.asteroidradar.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.PictureOfDay

@Dao
interface AsteroidDatabaseDao{
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(asteroid: Asteroid)

    @Update
    suspend fun update(asteroid: Asteroid)

    @Query("SELECT * FROM asteroid WHERE id=:id")
    suspend fun getAsteroid(id:Long): Asteroid?

    @Query("SELECT * FROM asteroid ORDER BY id")
    fun getAllAsteroids():LiveData<List<Asteroid>?>

    @Query("SELECT * FROM asteroid WHERE close_approach_date >= :date ORDER BY close_approach_date")
    fun getAsteroidsByDate(date:String):LiveData<List<Asteroid>?>
}