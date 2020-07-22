package studio.codable.unpause.repository

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import studio.codable.unpause.model.Location
import studio.codable.unpause.model.firestore.FirestoreLocation
import studio.codable.unpause.model.firestore.FirestoreLocation.Companion.NAME_FIELD
import studio.codable.unpause.utilities.Constants
import studio.codable.unpause.utilities.Constants.FirestoreCollections.LOCATIONS
import studio.codable.unpause.utilities.manager.SessionManager
import studio.codable.unpause.utilities.networking.Result
import studio.codable.unpause.utilities.networking.callFirebase
import studio.codable.unpause.utilities.networking.callFirebaseRawResult
import javax.inject.Inject

class FirebaseLocationRepository @Inject constructor(
    firestore: FirebaseFirestore,
    private val sessionManager: SessionManager
) : ILocationRepository {

    companion object {
        fun extractFirestoreLocation(documentSnapshot: DocumentSnapshot): FirestoreLocation {
            return documentSnapshot.toObject(FirestoreLocation::class.java)!!
        }
    }

    private val usersCol = firestore.collection(Constants.FirestoreCollections.USERS)
    private val locationsCol = usersCol.document(sessionManager.userId).collection(LOCATIONS)

    override suspend fun getAll(): Result<List<Location>> {
        return callFirebase(locationsCol.get()) {
            it.documents.map { snapshot ->  extractFirestoreLocation(snapshot).toLocation() }
        }

    }

    override suspend fun addLocation(location: Location): Result<Unit> {
        return callFirebase(locationsCol.add(FirestoreLocation(location).asHashMap())) {
            Unit
        }
    }

    override suspend fun deleteLocation(location: Location): Result<Unit> {
            return callFirebase(locationsCol.whereEqualTo(NAME_FIELD, location.name).get()) {
                val id = extractFirestoreLocation(it.documents[0]).documentId
                callFirebaseRawResult(locationsCol.document(id).delete()) {
                }
            }
    }
}