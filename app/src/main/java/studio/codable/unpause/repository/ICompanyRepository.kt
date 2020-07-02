package studio.codable.unpause.repository

import studio.codable.unpause.model.Company
import studio.codable.unpause.utilities.geofencing.GeofenceModel
import studio.codable.unpause.utilities.networking.Result

interface ICompanyRepository {

    suspend fun getCompany(companyId: String): Result<Company>

    suspend fun getGeofences(companyId: String): Result<List<GeofenceModel>>

}