package com.udacity.asteroidradar.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.PictureOfDay
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
//import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Build the Moshi object that Retrofit will be using, making sure to add the Kotlin adapter for
 * full Kotlin compatibility.
 */
private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

/**
 * Use the Retrofit builder to build a retrofit object using a Scalar converter.
 * Use this to fetch the complex Nasa json as a String
 */
private val retrofitScalar = Retrofit.Builder()
    .addConverterFactory(ScalarsConverterFactory.create()) // to help convert the complex json to String
    .baseUrl(Constants.BASE_URL)
    .build()

/**
 * Use the Retrofit builder to build a retrofit object using a Moshi converter with our Moshi
 * object. Use this to retrieve image of the day
 */

private val retrofitMoshi = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(Constants.BASE_URL)
    .build()

/**
 * A public interface that exposes the [getAsteroids] method
 */
interface AsteroidApiService {

    /**
     * Returns list of asteroids in json format. Returning String because we have to manually parse
     * (data too complex for Moshi to parse into an POJO)
    */
    @GET("${Constants.NEOWS_API_URL}/feed")
    suspend fun getAsteroids(
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String,
        @Query("api_key") apiKey: String,
    ): Response<String>

    /**
     * Returns the picture of the Day object
     */
    @GET(Constants.APOD_API_URL)
    suspend fun getPictureOfDay(
        @Query("api_key") apiKey: String,
    ): Response<PictureOfDay>

}

/**
 * A public Api object that exposes the lazy-initialized Retrofit service
 *
 * Kotlin Singleton class. No constructor. The object will be instantiated and its init block if any
 will be executed lazily upon first access, in a thread-safe way
 */

object AsteroidApi {
val retrofitServiceScalar : AsteroidApiService by lazy {
    retrofitScalar.create(AsteroidApiService::class.java)
    }
    val retrofitMoshiService : AsteroidApiService by lazy {
        retrofitMoshi.create(AsteroidApiService::class.java)
    }
}





