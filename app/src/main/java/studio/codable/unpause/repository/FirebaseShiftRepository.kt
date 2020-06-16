package studio.codable.unpause.repository

import com.google.firebase.firestore.FirebaseFirestore
import studio.codable.unpause.model.Shift
import studio.codable.unpause.model.firestore.FirestoreShift
import studio.codable.unpause.utilities.Constants
import studio.codable.unpause.utilities.manager.SessionManager
import studio.codable.unpause.utilities.networking.Result
import studio.codable.unpause.utilities.networking.callFirebase
import studio.codable.unpause.utilities.networking.callFirebaseRawResult
import timber.log.Timber
import javax.inject.Inject

class FirebaseShiftRepository @Inject constructor(
    firestore: FirebaseFirestore,
    private val sessionManager: SessionManager
) : IShiftRepository {

    private val usersCol = firestore.collection(Constants.FirestoreCollections.USERS)

    override suspend fun getAll(): Result<List<Shift>> {
        return callFirebase(usersCol.document(sessionManager.userId).get()) {
            FirebaseUserRepository.extractFirestoreUser(it).extractShifts()
        }
    }

    override suspend fun getCurrent(): Result<Shift?> {
        TODO("Not yet implemented")
    }

    @ExperimentalStdlibApi
    override suspend fun update(shift: Shift): Result<Unit> {
        return callFirebase(usersCol.document(sessionManager.userId).get()) {
            val shifts =
                FirebaseUserRepository.extractFirestoreUser(it).extractShifts() as ArrayList
            Timber.d("Old shifts: $shifts")

            val s = shifts.find { s -> s.arrivalTime == shift.arrivalTime }
            shifts.remove(s)
            shifts.add(shift)

            Timber.d("Updated shifts: $shifts")

            callFirebaseRawResult(
                usersCol.document(sessionManager.userId)
                    .update(mapOf<String, List<FirestoreShift>>("shifts" to shifts.map { regular ->
                        FirestoreShift(regular)
                    }))
            ) { }
        }
    }

    @ExperimentalStdlibApi
    override suspend fun addNew(newShift: Shift): Result<Unit> {
        return callFirebase(usersCol.document(sessionManager.userId).get()) {
            val shifts =
                FirebaseUserRepository.extractFirestoreUser(it).shifts ?: arrayListOf()
            Timber.d("Shifts: $shifts")

            (shifts as ArrayList).add(FirestoreShift(newShift))

            callFirebaseRawResult(
                usersCol.document(sessionManager.userId)
                    .update(mapOf<String, List<FirestoreShift>>("shifts" to shifts))
            ) { }

        }
    }

    override suspend fun delete(shift: Shift): Result<Unit> {
        return callFirebase(usersCol.document(sessionManager.userId).get()) {
            val shifts =
                FirebaseUserRepository.extractFirestoreUser(it).extractShifts() as ArrayList

            shifts.remove(shift)

            callFirebaseRawResult(
                usersCol.document(sessionManager.userId)
                    .update(mapOf<String, List<FirestoreShift>>("shifts" to shifts.map { shift ->
                        FirestoreShift(shift)
                    }))
            ) { }
        }
    }
}