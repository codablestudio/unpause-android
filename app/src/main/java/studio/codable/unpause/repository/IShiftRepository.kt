package studio.codable.unpause.repository

import studio.codable.unpause.model.Shift
import studio.codable.unpause.utilities.networking.Result

interface IShiftRepository {

    suspend fun getAll(userId: String): Result<List<Shift>>
    suspend fun getCurrent(userId: String): Result<Shift?>
    suspend fun update(userId: String, shift: Shift): Result<Unit>
    suspend fun addNew(userId: String, newShift: Shift): Result<Unit>
    suspend fun delete(userId: String, shift: Shift): Result<Unit>
}