package studio.codable.unpause.repository

import studio.codable.unpause.model.User
import studio.codable.unpause.utilities.networking.NetworkResult

interface IUserRepository {

    suspend fun getUser(userId: String): NetworkResult<User>
}