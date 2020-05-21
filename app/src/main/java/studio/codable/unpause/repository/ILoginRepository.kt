package studio.codable.unpause.repository

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import studio.codable.unpause.model.User
import studio.codable.unpause.utilities.networking.Result

interface ILoginRepository {

    suspend fun login(email: String, password: String): Result<User>

    suspend fun register(email: String, password: String): Result<User>

    suspend fun verifyEmail(email: String, password: String): Result<Unit>

    suspend fun signOut(): Result<Unit>

    suspend fun updateEmail(newEmail: String): Result<Unit>

    suspend fun updatePassword(newPassword: String): Result<Unit>

    suspend fun sendPasswordResetEmail(email: String): Result<Unit>

    suspend fun signInWithGoogle(account: GoogleSignInAccount, clientId: String): Result<Unit>
}