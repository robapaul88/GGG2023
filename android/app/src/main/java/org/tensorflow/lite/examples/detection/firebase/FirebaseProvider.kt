package org.tensorflow.lite.examples.detection.firebase

import android.graphics.Bitmap
import android.util.Log
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.suspendCancellableCoroutine
import org.tensorflow.lite.examples.detection.list.presentation.EmployeeData
import org.tensorflow.lite.examples.detection.utils.BitmapConverter
import kotlin.coroutines.resume

object FirebaseProvider {
    private val firebaseDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()

    private const val PEOPLE_NUMBER = "PeopleNumber"
    private const val EMPLOYEES = "Employees"
    private const val DATA_ID = "ID"
    private const val DATA_NAME = "Name"
    private const val DATA_IMAGE = "Image"

    fun saveEmployee(name: String, faceImage: Bitmap) {
        val peopleNrReference = firebaseDatabase.getReference(PEOPLE_NUMBER).child(DATA_ID)
        peopleNrReference.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val currentId = snapshot.value as Long
                    val newPersonReference = FirebaseDatabase.getInstance().getReference(EMPLOYEES).child("$currentId")
                    val encodedBitmap = BitmapConverter.convertBitmapToString(faceImage)

                    newPersonReference.child(DATA_ID).setValue(currentId)
                    newPersonReference.child(DATA_NAME).setValue(name)
                    newPersonReference.child(DATA_IMAGE).setValue(encodedBitmap)

                    peopleNrReference.setValue(currentId + 1)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    suspend fun getEmployees() = suspendCancellableCoroutine<List<EmployeeData>> { continuation ->
        firebaseDatabase.getReference(EMPLOYEES).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val list = mutableListOf<EmployeeData>()
                    snapshot.children.forEach { data ->
                        list.add(dataMapper(data))
                    }
                    Log.d("FirebaseProvider", "getEmployees onDataChange: $list")
                    continuation.resume(list)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("FirebaseProvider", "getEmployees onCancelled")
                continuation.resume(mutableListOf())
            }
        })
    }
    private fun dataMapper(data: DataSnapshot): EmployeeData {
        data.apply {
            return EmployeeData(
                firstName = child(DATA_NAME).value as? String ?: "",
                lastName = child(DATA_NAME).value as? String ?: "",
                photo = BitmapConverter.convertStringToBitmap(
                    child(DATA_IMAGE).value as? String ?: ""
                ),
                timestamp = 0L
            )
        }
    }
}
