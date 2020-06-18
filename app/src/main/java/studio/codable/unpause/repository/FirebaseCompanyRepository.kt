package studio.codable.unpause.repository

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import studio.codable.unpause.model.Company
import studio.codable.unpause.model.firestore.FirestoreCompany
import studio.codable.unpause.utilities.Constants
import studio.codable.unpause.utilities.manager.GeofencingManager.*
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
}