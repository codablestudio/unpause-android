package studio.codable.unpause.repository

import studio.codable.unpause.model.User
import studio.codable.unpause.utilities.networking.Result

interface IUserRepository {

    suspend fun getUser(userId: String): Result<User>

    suspend fun createUser(user: User): Result<Unit>

    suspend fun updateUser(user: User): Result<Unit>

    suspend fun isUserVerified(user: User): Result<Boolean>
}