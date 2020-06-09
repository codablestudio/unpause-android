package studio.codable.unpause.repository

import studio.codable.unpause.model.Company
import studio.codable.unpause.utilities.networking.Result

interface ICompanyRepository {

    suspend fun getUser(companyId: String): Result<Company>
}