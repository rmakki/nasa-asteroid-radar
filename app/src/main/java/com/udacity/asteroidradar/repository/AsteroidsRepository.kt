package com.udacity.asteroidradar.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.PictureOfDay
import com.udacity.asteroidradar.database.AsteroidsDatabase
import com.udacity.asteroidradar.database.asDomainModel
import com.udacity.asteroidradar.network.AsteroidApi
import com.udacity.asteroidradar.network.NetworkAsteroidContainer
import com.udacity.asteroidradar.network.asDomainModel
import com.udacity.asteroidradar.api.getDownloadDates
import com.udacity.asteroidradar.database.AsteroidDao
import com.udacity.asteroidradar.network.AsteroidApi.retrofitMoshiService
import com.udacity.asteroidradar.network.asDatabaseModel
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import retrofit2.Response


// Pass Dao in constructor to avoid memory leaks. Dependency injection
class AsteroidsRepository(private val asteroidDao: AsteroidDao) {

//class AsteroidsRepository(private val database: AsteroidsDatabase) {

    // LiveData for Astronomy Picture of the Day
    private val _apod= MutableLiveData<PictureOfDay>()
    val apod: LiveData<PictureOfDay>
        get() = _apod

    // define upcoming week
    private var nextWeekDates: ArrayList<String>
    private var today: String
    private var nextWeek: String

    init {
        // Array of today + next 7 days
        nextWeekDates = getDownloadDates()
        today = nextWeekDates[0]    // date today
        nextWeek = nextWeekDates[1] // date one week from today

    }
    /**
     * Refresh the asteroids stored in the offline cache.
     *
     * This function uses the IO dispatcher to ensure the database insert database operation
     * happens on the IO dispatcher. By switching to the IO dispatcher using `withContext` this
     * function is now safe to call from any thread including the Main thread.
     *
     * To actually load the asteroids for use, observe [asteroids]
     */
    suspend fun refreshAsteroids() {
        withContext(Dispatchers.IO) {

                try {
                    // Suspend function call - Fetch from Network
                        // val response = AsteroidApi.retrofitServiceScalar.getAsteroids("2021-10-22","2021-10-29",Constants.NASA_API_KEY)
                    val response = AsteroidApi.retrofitServiceScalar
                        .getAsteroids(today,nextWeek, Constants.NASA_API_KEY)
                    if (response.isSuccessful) {

                        Log.i("Asteroids retrieved: ", response.body().toString())
                        var asteroidList =
                            NetworkAsteroidContainer(response.body().toString()).asDomainModel()
                        Log.i("POJO size of Asteroids retrieved:", asteroidList.size.toString())
                        // Write to the cash
                        asteroidDao.insertAll(*asteroidList.asDatabaseModel())

                    } else {
                        Log.e("Network response problem : ", response.message() )
                    }

                } catch (e: Exception) {
                    Log.e("Failure : ", e.stackTraceToString())
                }
        }
    }

    /**
     * All asteroids approaching Starting today
     */
    fun getAsteroidsStartToday(): LiveData<List<Asteroid>> {
        Log.i("getAsteroidsStartToday", "Get Asteroids Week from DB")

        return Transformations.map(asteroidDao.getAsteroidsAfterDate(today)) {
            it.asDomainModel()
        }
    }

    /**
     * All asteroids approaching today
     */
    fun getAsteroidsApproachingToday(): LiveData<List<Asteroid>> {
        Log.i("getAsteroidsApproachingToday", "Get Asteroids Today from DB")
        return Transformations.map(asteroidDao.getAsteroidsToday(today)) {
            it.asDomainModel()
        }
    }

    /**
     * Get all asteroids
     */
    fun getAsteroidsAll(): LiveData<List<Asteroid>> {
        Log.i("getAsteroidsAll", "Get All Asteroids from DB")
        return Transformations.map(asteroidDao.getAllAsteroids()) {
                    it.asDomainModel()
                }
    }

    // A list of all asteroids that can be shown on the screen - Did the function above instead, which
    // approach is better?
    //val asteroids: LiveData<List<Asteroid>> =
    //    Transformations.map(database.asteroidDao.getAllAsteroids()) {
    //        it.asDomainModel()
    //    }

    /**
     * Refresh Astronomy Picture of the Day apod in the offline cache.
     *
     */
    suspend fun refreshPictureOfTheDay() {

        // send GET request to server - coroutine to avoid blocking the UI thread
        withContext(Dispatchers.IO) {

            // Network request
            try{
                // GET
                Log.i("Refresh apod : ","Refresh apod - sending GET request")
                val response: Response<PictureOfDay> =
                    retrofitMoshiService.getPictureOfDay(Constants.NASA_API_KEY)

                if (response.isSuccessful) {
                    response.body()?.let {
                        Log.i("apod retrieved: ", response.body().toString())
                        _apod.postValue(it)
                    }
                }

                Log.i("Refresh apod: ","apod GET request success")

            } catch (e: Exception) {

                Log.i("Refresh apod Exception: ", e.stackTraceToString())
            }
        }
    }



}

/**
 * Deprecated Callback code. Retrofit 2.6 has built in support for Coroutines.
 * No need for Callback and enqueue or the Jake Wharton adapter anymore. See above
 * */
/*
private fun getAsteroids() {
    AsteroidApi.retrofitService.getAsteroids("2021-10-20","2021-10-27","3Ece5JvM6wnEUGZP8Xn1sWlNg1q1cZPdSwBvAFij").enqueue( object:
        Callback<List<Asteroid>> {
        override fun onFailure(call: Call<List<Asteroid>>, t: Throwable) {
            Log.e("Network Failure : ",  t.stackTraceToString())
        }

        override fun onResponse(call: Call<List<Asteroid>>, response: Response<List<Asteroid>>) {
            Log.i("Asteroids size retrieved: ",  response.body()?.size.toString())
            //Log.i("Asteroids : ",  response.body().toString())
        }
    })
}
*/