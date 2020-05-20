package studio.codable.unpause.repository

import studio.codable.unpause.model.User
import studio.codable.unpause.utilities.networking.NetworkResult
import studio.codable.unpause.utilities.networking.api.UserApi
import studio.codable.unpause.utilities.networking.callApi

class UserRepository(private val userApi: UserApi) : IUserRepository {

    override suspend fun getUser(userId: String): NetworkResult<User> {
        return callApi {
            userApi.getUser(userId)
        }
    }
}