package studio.codable.unpause.repository

import com.google.firebase.firestore.DocumentReference
import studio.codable.unpause.model.User
import studio.codable.unpause.utilities.networking.Result

interface IUserRepository {

    suspend fun getUser(): Result<User>

    suspend fun createUser(user: User): Result<Unit>

    suspend fun updateUser(user: User): Result<Unit>

    suspend fun isUserVerified(user: User): Result<Boolean>

    suspend fun updateFirstName(userId : String, firstName : String) : Result<Unit>

    suspend fun updateLastName(userId : String, lastName : String) : Result<Unit>

    suspend fun updateCompany(userId : String, companyReference: DocumentReference) : Result<Unit>
}