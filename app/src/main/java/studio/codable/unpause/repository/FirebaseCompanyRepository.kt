package studio.codable.unpause.repository

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import studio.codable.unpause.model.Company
import studio.codable.unpause.model.firestore.FirestoreCompany
import studio.codable.unpause.utilities.Constants
import studio.codable.unpause.utilities.networking.Result
import studio.codable.unpause.utilities.networking.await
import studio.codable.unpause.utilities.networking.callFirebase
import timber.log.Timber
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
    private val firestore = firestore

    override suspend fun getCompany(companyPath: String): Result<Company> {
        return callFirebase(firestore.document(companyPath).get()) {
            extractFirestoreCompany(it).toCompany()
        }
    }
}