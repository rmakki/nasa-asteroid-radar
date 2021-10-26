package com.udacity.asteroidradar.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.viewModelScope
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.database.AsteroidsDatabase
import com.udacity.asteroidradar.database.asDomainModel
import com.udacity.asteroidradar.network.AsteroidApi
import com.udacity.asteroidradar.network.NetworkAsteroidContainer
import com.udacity.asteroidradar.network.asDomainModel
import com.udacity.asteroidradar.api.getDownloadDates
import com.udacity.asteroidradar.network.asDatabaseModel
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers


// Pass DB in constructor to avoid memory leaks. Dependency injection
class AsteroidsRepository(private val database: AsteroidsDatabase) {

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
                        database.asteroidDao.insertAll(*asteroidList.asDatabaseModel())

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
        return Transformations.map(database.asteroidDao.getAsteroidsAfterDate(today)) {
            it.asDomainModel()
        }
        //return database.asteroidDao.getAsteroidsAfterDate(today).map {
        //    it.asDomainModel()
        //}
    }

    /**
     * All asteroids approaching today
     */
    fun getAsteroidsApproachingToday(): LiveData<List<Asteroid>> {
        return Transformations.map(database.asteroidDao.getAsteroidsToday(today)) {
            it.asDomainModel()
        }
    }

    /**
     * Get all asteroids
     */
    fun getAsteroidsAll(): LiveData<List<Asteroid>> {

        return Transformations.map(database.asteroidDao.getAllAsteroids()) {
                    it.asDomainModel()
                }
    }

    // A list of all asteroids that can be shown on the screen - Did the function above instead
    //val asteroids: LiveData<List<Asteroid>> =
    //    Transformations.map(database.asteroidDao.getAllAsteroids()) {
    //        it.asDomainModel()
    //    }

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