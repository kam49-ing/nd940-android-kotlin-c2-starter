package com.udacity.asteroidradar.repository

import com.google.android.material.snackbar.Snackbar
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.api.AsteroidApi
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.database.AsteroidDatabaseDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.HttpException
import java.time.LocalDate

class AsteroidRepository(private val database: AsteroidDatabaseDao) {

    private var today = LocalDate.now().toString()
    val asteroids = database.getAsteroidsByDate(today)
    suspend fun refreshAsteroid(){

        withContext(Dispatchers.IO) {
            val response = AsteroidApi.retrofitService.getAsteroids()
            try {
                val asteroidJson = JSONObject(response)
                val asteroidList = parseAsteroidsJsonResult(asteroidJson)
                for(asteroid in asteroidList)
                {
                    database.insert(asteroid)
                }
                //database.insertAl(asteroidList)
            }catch (e:HttpException){

            }
        }
    }
}