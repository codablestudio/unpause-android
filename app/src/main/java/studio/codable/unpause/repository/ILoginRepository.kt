package studio.codable.unpause.repository

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.AuthResult
import studio.codable.unpause.utilities.networking.Result

interface ILoginRepository {

    suspend fun login(email: String, password: String): Result<String>

    suspend fun register(
        email: String,
        password: String,
        firstName: String?,
        lastName: String?
    ): Result<String>

    suspend fun verifyEmail(email: String, password: String): Result<Boolean>

    fun signOut()

    suspend fun updateEmail(newEmail: String): Result<Unit>

    suspend fun updatePassword(newPassword: String): Result<Unit>

    suspend fun sendPasswordResetEmail(email: String): Result<Unit>

    suspend fun signInWithGoogle(account: GoogleSignInAccount, clientId: String): Result<AuthResult>

    fun isUserVerified(): Boolean

    suspend fun sendVerificationEmail() : Result<Unit>
}