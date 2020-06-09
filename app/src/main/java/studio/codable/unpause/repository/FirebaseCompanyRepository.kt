package studio.codable.unpause.repository

import com.google.firebase.firestore.FirebaseFirestore
import studio.codable.unpause.model.Company
import studio.codable.unpause.utilities.networking.Result
import javax.inject.Inject

class FirebaseCompanyRepository @Inject constructor(
firestore: FirebaseFirestore
) : ICompanyRepository {

    override suspend fun getUser(companyId: String): Result<Company> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}