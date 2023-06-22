package org.tensorflow.lite.examples.detection.firebase

import android.graphics.Bitmap
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import org.tensorflow.lite.examples.detection.list.presentation.EmployeeData
import org.tensorflow.lite.examples.detection.utils.BitmapConverter

object FirebaseProvider {
    private val firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()

    private const val PEOPLE_NUMBER = "PeopleNumber"
    private const val EMPLOYEES = "Employees"
    private const val DATA_ID = "ID"
    private const val DATA_NAME = "Name"
    private const val DATA_IMAGE = "Image"
    private const val LAST_SEEN_AT = "LastSeenAt"

    fun saveEmployee(name: String, faceImage: Bitmap) {
        val peopleNrReference = firebaseDatabase.getReference(PEOPLE_NUMBER).child(DATA_ID)
        peopleNrReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val currentId = snapshot.value as Long
                    val newPersonReference = FirebaseDatabase.getInstance().getReference(EMPLOYEES).child("$currentId")
                    val encodedBitmap = BitmapConverter.convertBitmapToString(faceImage)

                    newPersonReference.child(DATA_ID).setValue(currentId)
                    newPersonReference.child(DATA_NAME).setValue(name)
                    newPersonReference.child(DATA_IMAGE).setValue(encodedBitmap)
                    newPersonReference.child(LAST_SEEN_AT).setValue(System.currentTimeMillis())

                    peopleNrReference.setValue(currentId + 1)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    val employeesFlow = callbackFlow<List<EmployeeData>> {
        firebaseDatabase.getReference(EMPLOYEES).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val list = mutableListOf<EmployeeData>()
                    snapshot.children.forEach { data ->
                        list.add(0, dataMapper(data))
                    }
                    Log.d("FirebaseProvider", "getEmployees onDataChange: $list")
                    trySend(list)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("FirebaseProvider", "getEmployees onCancelled")
                trySend(mutableListOf())
            }
        })

        awaitClose { }
    }

    fun clearDatabase() {
        firebaseDatabase.getReference(EMPLOYEES).setValue(null)
        firebaseDatabase.getReference(PEOPLE_NUMBER).setValue(null)
        firebaseDatabase.getReference(PEOPLE_NUMBER).child(DATA_ID).setValue(0L)
    }

    fun removeEmployee(id: String) {
        firebaseDatabase.getReference(EMPLOYEES).child(id).setValue(null)
        firebaseDatabase.getReference(PEOPLE_NUMBER).child(DATA_ID).get().addOnSuccessListener {
            if (it.exists()) {
                val currentNr = it.value as? Long
                if (currentNr != null) {
                    firebaseDatabase.getReference(PEOPLE_NUMBER).child(DATA_ID).setValue(currentNr - 1)
                }
            }
        }
    }

    private fun dataMapper(data: DataSnapshot): EmployeeData {
        data.apply {
            return EmployeeData(
                id = child(DATA_ID).value as? Long,
                firstName = child(DATA_NAME).value as? String ?: "",
                lastName = child(DATA_NAME).value as? String ?: "",
                photo = BitmapConverter.convertStringToBitmap(
                    child(DATA_IMAGE).value as? String ?: ""
                ),
                timestamp = child(LAST_SEEN_AT).value as? Long ?: 0L
            )
        }
    }
}
