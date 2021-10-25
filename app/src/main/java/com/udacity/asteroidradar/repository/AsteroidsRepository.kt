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
            //val asteroidlist = AsteroidApi.retrofitServiceScalar.getAsteroids().await()
            //database.asteroidDao.insertAll(*asteroidlist.asDatabaseModel())
                try {
                    // Suspend function call - Fetch from Network
                    //var response = AsteroidApi.retrofitServiceScalar.getAsteroids("2021-10-22","2021-10-29","3Ece5JvM6wnEUGZP8Xn1sWlNg1q1cZPdSwBvAFij")
                    val response = AsteroidApi.retrofitServiceScalar
                        .getAsteroids(today,nextWeek, Constants.NASA_API_KEY)
                    if (response.isSuccessful) {

                        Log.i("Asteroids retrieved: ", response.body().toString())
                        var asteroidList =
                            NetworkAsteroidContainer(response.body().toString()).asDomainModel()
                        Log.i("POJO size of Asteroids retrieved:", asteroidList.size.toString())

                        database.asteroidDao.insertAll(*asteroidList.asDatabaseModel())

                    // network data
                        //val netAsteroidData =
                        //    parseAsteroidsJsonResult(JSONObject(response.body()!!))
                        //Log.i("POJO size of Asteroids retrieved: ", netAsteroidData.size.toString())
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

    /**
     * A list of all asteroids that can be shown on the screen - Did the function above instead
     */
    //val asteroids: LiveData<List<Asteroid>> =
    //    Transformations.map(database.asteroidDao.getAllAsteroids()) {
    //        it.asDomainModel()
    //    }

}
