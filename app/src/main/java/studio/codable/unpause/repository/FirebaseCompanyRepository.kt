package studio.codable.unpause.repository

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import studio.codable.unpause.model.Company
import studio.codable.unpause.model.firestore.FirestoreCompany
import studio.codable.unpause.utilities.Constants
import studio.codable.unpause.utilities.geofencing.GeofenceModel
import studio.codable.unpause.utilities.networking.Result
import studio.codable.unpause.utilities.networking.callFirebase
import javax.inject.Inject

class FirebaseCompanyRepository @Inject constructor(
    firestore: FirebaseFirestore
) : ICompanyRepository {

    companion object {
        fun extractFirestoreCompany(documentSnapshot: DocumentSnapshot): FirestoreCompany {
            return documentSnapshot.toObject(FirestoreCompany::class.java)!!
        }

        private const val PASSCODE_FIELD = "passcode"
    }

    private val companiesCol = firestore.collection(Constants.FirestoreCollections.COMPANIES)

    override suspend fun getCompany(companyId: String): Result<Company> {
        return callFirebase(companiesCol.document(companyId).get()) {
            extractFirestoreCompany(it).toCompany()
        }
    }

    override suspend fun getGeofences(companyId: String): Result<List<GeofenceModel>> {
        return callFirebase(companiesCol.document(companyId).get()) {
            extractFirestoreCompany(it).extractGeofences()
        }
    }

    override suspend fun getCompanyId(passcode : String) : Result<String> {
        return callFirebase(companiesCol.whereEqualTo(PASSCODE_FIELD, passcode).get()) {
                extractFirestoreCompany(it.documents[0]).documentId
        }
    }

    override suspend fun checkIfCompanyExists(passcode : String): Result<Boolean> {
        return callFirebase(companiesCol.whereEqualTo(PASSCODE_FIELD, passcode).get()) {
            it.documents.isNotEmpty()
        }
    }
}