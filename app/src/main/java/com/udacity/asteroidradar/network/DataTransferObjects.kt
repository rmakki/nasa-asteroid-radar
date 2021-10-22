package com.udacity.asteroidradar.network

import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.database.DatabaseAsteroid

class DataTransferObjects {
}

// convert from network data transfer object to database object
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