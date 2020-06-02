package studio.codable.unpause.repository

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import studio.codable.unpause.model.User
import studio.codable.unpause.utilities.Constants
import studio.codable.unpause.utilities.networking.Result
import studio.codable.unpause.utilities.networking.callFirebase
import javax.inject.Inject

class FirebaseLoginRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    firestore: FirebaseFirestore
) : ILoginRepository {

    private val usersCol = firestore.collection(Constants.FirestoreCollections.USERS)

    override suspend fun login(email: String, password: String): Result<String> {
        return callFirebase(firebaseAuth.signInWithEmailAndPassword(email, password)) {
            email
        }
    }

    override suspend fun register(
        email: String,
        password: String,
        firstName: String?,
        lastName: String?
    ): Result<String> {
        return callFirebase(firebaseAuth.createUserWithEmailAndPassword(email, password)) {
            createUserInDatabase(email, firstName.orEmpty(), lastName.orEmpty())
            email
        }
    }

    override suspend fun verifyEmail(email: String, password: String): Result<Boolean> {
        return callFirebase(firebaseAuth.signInWithEmailAndPassword(email, password)) {
            it.user!!.isEmailVerified
        }

    }

    override suspend fun signOut(): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun updateEmail(newEmail: String): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun updatePassword(newPassword: String): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
        return callFirebase(firebaseAuth.sendPasswordResetEmail(email)) {
            Unit
        }
    }

    override suspend fun signInWithGoogle(
        account: GoogleSignInAccount,
        clientId: String
    ): Result<AuthResult> {
        val credential = GoogleAuthProvider.getCredential(account.idToken, clientId)
        return callFirebase(firebaseAuth.signInWithCredential(credential)) {
           it
        }
    }

    override fun isUserVerified(): Boolean {
        return if (firebaseAuth.currentUser != null) {
            firebaseAuth.currentUser!!.isEmailVerified
        } else {
            false
        }
    }

    private suspend fun createUserInDatabase(
        email: String,
        firstName: String,
        lastName: String
    ): Result<Unit> {
        val user = User(email, firstName, lastName, email)
        return callFirebase(usersCol.document(email).set(user)) {}
    }
}