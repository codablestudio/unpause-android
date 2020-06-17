package studio.codable.unpause.repository

import studio.codable.unpause.model.Shift
import studio.codable.unpause.utilities.networking.Result

interface IShiftRepository {

    suspend fun getAll(): Result<List<Shift>>
    suspend fun getCurrent(): Result<Shift?>
    suspend fun update(shift: Shift): Result<Unit>
    suspend fun addNew(newShift: Shift): Result<Unit>
    suspend fun delete(shift: Shift): Result<Unit>
}