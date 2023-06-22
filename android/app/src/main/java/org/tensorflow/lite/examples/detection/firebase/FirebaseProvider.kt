package org.tensorflow.lite.examples.detection.firebase

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import org.tensorflow.lite.examples.detection.utils.BitmapConverter

object FirebaseProvider {

    fun savePerson(name: String, faceImage: Bitmap) {
        val peopleNrReference = FirebaseDatabase.getInstance().getReference("PeopleNumber").child("ID")
        peopleNrReference.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val currentId = snapshot.value as Long
                    val newPersonReference = FirebaseDatabase.getInstance().getReference("Employees").child("$currentId")
                    val encodedBitmap = BitmapConverter.convertBitmapToString(faceImage)

                    newPersonReference.child("ID").setValue(currentId)
                    newPersonReference.child("Name").setValue(name)
                    newPersonReference.child("Image").setValue(encodedBitmap)

                    peopleNrReference.setValue(currentId + 1)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

    }
}