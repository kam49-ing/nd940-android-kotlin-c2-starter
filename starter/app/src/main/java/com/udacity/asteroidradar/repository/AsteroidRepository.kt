package com.udacity.asteroidradar.repository

import android.util.Log
import android.view.animation.Transformation
import androidx.lifecycle.Transformations.map
import androidx.lifecycle.viewModelScope
import com.google.android.material.snackbar.Snackbar
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.api.AsteroidApi
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.database.AsteroidDatabaseDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
                    //database.insertAl(asteroidList)
                } catch (e: HttpException) {

                }
            }
        }
        catch (e:Exception){

        }
        /*try {
            withContext(Dispatchers.IO) {
                try {
                    val dayImage = AsteroidApi.retrofitService.getDayImage()

                    val day = JSONObject(dayImage).getString("url")
                    database.insertPictureOfDay(day)
                } catch (e: Exception) {
                    Log.i("MainFragment", "exception when fetching day image: $e")
                    _dayImageUrl.value = null
                }
            }
        }catch(e:Exception){
            Log.i("MainFragment", "Error when lunching scope: $e")
            _dayImageUrl.value = null
        }*/

    //}
    }
}