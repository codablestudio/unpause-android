package studio.codable.unpause.repository

import com.google.firebase.firestore.FirebaseFirestore
import studio.codable.unpause.model.Shift
import studio.codable.unpause.model.firestore.FirestoreShift
import studio.codable.unpause.utilities.Constants
import studio.codable.unpause.utilities.networking.Result
import studio.codable.unpause.utilities.networking.callFirebase
import studio.codable.unpause.utilities.networking.callFirebaseRawResult
import timber.log.Timber
import javax.inject.Inject

class FirebaseShiftRepository @Inject constructor(
    firestore: FirebaseFirestore
) : IShiftRepository {

    private val usersCol = firestore.collection(Constants.FirestoreCollections.USERS)

    override suspend fun getAll(userId: String): Result<List<Shift>> {
        return callFirebase(usersCol.document(userId).get()) {
            FirebaseUserRepository.extractFirestoreUser(it).extractShifts()
        }
    }

    override suspend fun getCurrent(userId: String): Result<Shift?> {
        TODO("Not yet implemented")
    }

    @ExperimentalStdlibApi
    override suspend fun update(userId: String, shift: Shift): Result<Unit> {
        return callFirebase(usersCol.document(userId).get()) {
            val shifts =
                FirebaseUserRepository.extractFirestoreUser(it).extractShifts() as ArrayList
            Timber.d("Old shifts: $shifts")

            val s = shifts.find { s -> s.arrivalTime == shift.arrivalTime }
            shifts.remove(s)
            shifts.add(shift)

            Timber.d("Updated shifts: $shifts")

            callFirebaseRawResult(
                usersCol.document(userId)
                    .update(mapOf<String, List<FirestoreShift>>("shifts" to shifts.map { regular ->
                        FirestoreShift(regular)
                    }))
            ) { }
        }
    }

    @ExperimentalStdlibApi
    override suspend fun addNew(userId: String, newShift: Shift): Result<Unit> {
        return callFirebase(usersCol.document(userId).get()) {
            val shifts =
                FirebaseUserRepository.extractFirestoreUser(it).shifts ?: arrayListOf()
            Timber.d("Shifts: $shifts")

            (shifts as ArrayList).add(FirestoreShift(newShift))

            callFirebaseRawResult(
                usersCol.document(userId)
                    .update(mapOf<String, List<FirestoreShift>>("shifts" to shifts))
            ) { }

        }
    }

    override suspend fun delete(userId: String, shift: Shift): Result<Unit> {
        TODO("Not yet implemented")
    }
}