package org.tensorflow.lite.examples.detection.utils

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.tensorflow.lite.examples.detection.firebase.FirebaseProvider

@Composable
fun ClearDbWithConfirmation() {
    Column(
        modifier = Modifier
            .padding(16.dp)
    ) {
        val openDialog = remember { mutableStateOf(false) }

        Button(
            onClick = {
                openDialog.value = true
            }) {
            Text(color = Color.Black, text = "Clear Database", fontSize = 22.sp)
        }

        if (openDialog.value) {
            AlertDialog(
                onDismissRequest = {
                    openDialog.value = false
                },
                title = {
                    Text(text = "Are you sure you want to clear db?")
                },
                text = {
                    Text("All content will be erased.")
                },
                confirmButton = {
                    Button(
                        onClick = {
                            FirebaseProvider.clearDatabase()
                            openDialog.value = false
                        }) {
                        Text("Yes")
                    }
                },
                dismissButton = {
                    Button(
                        onClick = {
                            openDialog.value = false
                        }) {
                        Text("No")
                    }
                }
            )
        }
    }
}