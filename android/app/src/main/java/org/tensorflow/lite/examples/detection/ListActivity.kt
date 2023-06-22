package org.tensorflow.lite.examples.detection

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import org.tensorflow.lite.examples.detection.list.presentation.ListScreen
import org.tensorflow.lite.examples.detection.list.presentation.ListViewModel
import org.tensorflow.lite.examples.detection.ui.theme.AndroidTheme

class ListActivity : ComponentActivity() {
    private val viewModel: ListViewModel by viewModels { ListViewModel.Factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AndroidTheme {
                ListScreen(
                    viewModel = viewModel,
                    onScreenClose = { finish() }
                )
            }
        }
    }

    companion object {
        fun getIntent(context: Context) = Intent(context, ListActivity::class.java)
    }
}