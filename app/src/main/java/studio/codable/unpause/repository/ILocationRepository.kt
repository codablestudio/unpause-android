package studio.codable.unpause.repository

import studio.codable.unpause.model.Location
import studio.codable.unpause.utilities.networking.Result

interface ILocationRepository {

    suspend fun getAll(): Result<List<Location>>

    suspend fun addLocation(location: Location): Result<Unit>

}