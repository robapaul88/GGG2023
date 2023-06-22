package org.tensorflow.lite.examples.detection

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.tensorflow.lite.examples.detection.ui.theme.AndroidTheme
import org.tensorflow.lite.examples.detection.utils.ClearDbWithConfirmation

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AndroidTheme {
                ContentScreen()
            }
        }
    }
}

@Composable
fun ContentScreen() {
    val context = LocalContext.current
    // A surface container using the 'background' color from the theme
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Box(modifier = Modifier.fillMaxSize().padding(8.dp)) {
            Text(
                text = "GARMIN",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.align(Alignment.BottomCenter),
                color = MaterialTheme.colorScheme.primary
            )
            Column(modifier = Modifier.align(Alignment.Center)) {
                Button(modifier = Modifier
                    .align(CenterHorizontally)
                    .padding(16.dp),
                    onClick = {
                        context.startActivity(Intent(context, DetectorActivity::class.java))
                    }) {
                    Text(
                        color = MaterialTheme.colorScheme.onPrimary,
                        text = "Camera",
                        fontSize = 22.sp
                    )
                }
                Button(modifier = Modifier
                    .align(CenterHorizontally)
                    .padding(16.dp),
                    onClick = {
                        context.startActivity(ListActivity.getIntent(context))
                    }) {
                    Text(
                        color = MaterialTheme.colorScheme.onPrimary,
                        text = "Employees",
                        fontSize = 22.sp
                    )
                }
                ClearDbWithConfirmation()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AndroidTheme {
        ContentScreen()
    }
}