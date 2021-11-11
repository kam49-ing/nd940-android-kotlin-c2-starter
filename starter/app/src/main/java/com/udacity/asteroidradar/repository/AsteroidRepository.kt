package com.udacity.asteroidradar.repository

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
    private var endDate = LocalDate.now().plusDays(7).toString()
    val allAsteroids = database.getAllAsteroids()
    val asteroidsOfWeek = database.getAsteroidsByDate(today, endDate)
    val asteroidOfToday = database.getAsteroidOfToday(today)
    suspend fun refreshAsteroid(){
        try {
            withContext(Dispatchers.IO) {
                val response = AsteroidApi.retrofitService.getAsteroids()
                try {
                    val asteroidJson = JSONObject(response)
                    val asteroidList = parseAsteroidsJsonResult(asteroidJson)
                    for (asteroid in asteroidList) {
                        database.insert(asteroid)
                    }
                } catch (e: HttpException) {

                }
            }
        }
        catch (e:Exception){

        }

    }

    suspend fun deletePreviousAsteroids(){
        database.deletePreviousAsteroid(today)
    }
}