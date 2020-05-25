package studio.codable.unpause.repository

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import studio.codable.unpause.model.User
import studio.codable.unpause.utilities.Constants
import studio.codable.unpause.utilities.networking.Result
import studio.codable.unpause.utilities.networking.callFirebase
import studio.codable.unpause.utilities.networking.callFirebaseRawResult
import javax.inject.Inject

class FirebaseUserRepository @Inject constructor(
    firestore: FirebaseFirestore
) : IUserRepository {

    companion object {
        fun extractUser(documentSnapshot: DocumentSnapshot): User {
            return documentSnapshot.toObject(User::class.java)!!
        }
    }

    private val usersCol = firestore.collection(Constants.FirestoreCollections.USERS)

    override suspend fun getUser(userId: String): Result<User> {
        return callFirebase(usersCol.document(userId).get()) { extractUser(it) }
    }

    override suspend fun createUser(user: User): Result<User> {
        return callFirebase(usersCol.add(user)) { reference ->
            callFirebaseRawResult(reference.get()) { snapshot ->
                extractUser(snapshot)
            }
        }
    }

    override suspend fun updateUser(user: User): Result<Unit> {
        TODO("Not yet implemented")
    }

    override suspend fun isUserVerified(user: User): Result<Boolean> {
        TODO("Not yet implemented")
    }
}