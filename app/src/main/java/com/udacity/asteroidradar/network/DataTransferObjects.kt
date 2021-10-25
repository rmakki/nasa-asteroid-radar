package com.udacity.asteroidradar.network

import android.util.Log
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.database.DatabaseAsteroid
import org.json.JSONObject

/**
 * DataTransferObjects go in this file. These are responsible for parsing responses from the server
 * or formatting objects to send to the server. You should convert these to domain objects before
 * using them.
 */

/**
 Represents the  json String fetched from the server via network
 */
data class NetworkAsteroidContainer(val strNetworkAsteroidList: String)

/**
 * Convert Network results to a Domain object - List<Asteroid>
 */
fun NetworkAsteroidContainer.asDomainModel(): List<Asteroid> {
    // network data
    val netAsteroidData =
        parseAsteroidsJsonResult(JSONObject(strNetworkAsteroidList))
    Log.i("POJO size of Asteroids retrieved: ", netAsteroidData.size.toString())
    return netAsteroidData
}

/**
 * Convert Domain object to Database Entity
 */
fun List<Asteroid>.asDatabaseModel(): Array<DatabaseAsteroid> {
    return map {
        DatabaseAsteroid (
            id = it.id,
            codename = it.codename,
            closeApproachDate = it.closeApproachDate,
            absoluteMagnitude = it.absoluteMagnitude,
            estimatedDiameter = it.estimatedDiameter,
            relativeVelocity = it.relativeVelocity,
            distanceFromEarth = it.distanceFromEarth,
            isPotentiallyHazardous = it.isPotentiallyHazardous,
        )
    }.toTypedArray()
}
/** old
fun List<Asteroid>.asDatabaseModel(): List<DatabaseAsteroid> {
    return map {
        DatabaseAsteroid (
            id = it.id,
            codename = it.codename,
            closeApproachDate = it.closeApproachDate,
            absoluteMagnitude = it.absoluteMagnitude,
            estimatedDiameter = it.estimatedDiameter,
            relativeVelocity = it.relativeVelocity,
            distanceFromEarth = it.distanceFromEarth,
            isPotentiallyHazardous = it.isPotentiallyHazardous,
        )
    }
}
*/
