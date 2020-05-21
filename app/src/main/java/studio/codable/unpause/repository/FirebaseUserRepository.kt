package studio.codable.unpause.repository

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import studio.codable.unpause.model.User
import studio.codable.unpause.utilities.Constants
import studio.codable.unpause.utilities.networking.Result
import studio.codable.unpause.utilities.networking.callFirestore
import studio.codable.unpause.utilities.networking.callFirestoreNested

class FirebaseUserRepository(
    firestore: FirebaseFirestore
) : IUserRepository {

    private val usersCol = firestore.collection(Constants.FirestoreCollections.USERS)

    override suspend fun getUser(userId: String): Result<User> {
        return callFirestore(usersCol.document(userId).get()) { extractUser(it) }
    }

    override suspend fun createUser(user: User): Result<User> {
        return callFirestore(usersCol.add(user)) { reference ->
            callFirestoreNested(reference.get()) { snapshot ->
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

    private fun extractUser(documentSnapshot: DocumentSnapshot): User {
        return documentSnapshot.toObject(User::class.java)!!
    }
}