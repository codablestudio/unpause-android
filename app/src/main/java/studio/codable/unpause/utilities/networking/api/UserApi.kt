package studio.codable.unpause.utilities.networking.api

import studio.codable.unpause.model.User

interface UserApi {

    suspend fun getUser(userId: String): User
}