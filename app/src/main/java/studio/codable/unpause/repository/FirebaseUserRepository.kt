package studio.codable.unpause.repository

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import studio.codable.unpause.model.User
import studio.codable.unpause.model.firestore.FirestoreUser
import studio.codable.unpause.utilities.Constants
import studio.codable.unpause.utilities.manager.SessionManager
import studio.codable.unpause.utilities.networking.Result
import studio.codable.unpause.utilities.networking.callFirebase
import javax.inject.Inject

class FirebaseUserRepository @Inject constructor(
    firestore: FirebaseFirestore,
    private val sessionManager: SessionManager
) : IUserRepository {

    companion object {
        fun extractFirestoreUser(documentSnapshot: DocumentSnapshot): FirestoreUser {
            return documentSnapshot.toObject(FirestoreUser::class.java)!!
        }
    }

    private val usersCol = firestore.collection(Constants.FirestoreCollections.USERS)

    override suspend fun getUser(): Result<User> {
        return callFirebase(usersCol.document(sessionManager.userId).get()) {
            extractFirestoreUser(it).toUser()
        }
    }

    override suspend fun createUser(user: User): Result<Unit> {
        return callFirebase(usersCol.document(user.id).set(FirestoreUser(user))) {
            Unit
        }
    }

    override suspend fun updateUser(user: User): Result<Unit> {
        return callFirebase(usersCol.document(user.id).update(FirestoreUser(user).asHashMap())) {
            Unit
        }
    }

    override suspend fun isUserVerified(user: User): Result<Boolean> {
        TODO("Not yet implemented")
    }
}