package org.tensorflow.lite.examples.detection.list.presentation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ListScreen(viewModel: ListViewModel, onScreenClose: () -> Unit) {
    val state = viewModel.uiState.collectAsState().value
    Scaffold(modifier = Modifier.background(MaterialTheme.colorScheme.primary), topBar = {
        TopAppBar(modifier = Modifier,
            title = { AppToolbarText(text = "Employees") },
            colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = MaterialTheme.colorScheme.primary),
            navigationIcon = {
                ToolbarAction(
                    image = Icons.Filled.ArrowBack, onClick = onScreenClose
                )
            })
    }) { paddingValues ->
        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background.copy(alpha = 0.3f))
            ) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        } else {
            if (state.list.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background.copy(alpha = 0.3f))
                ) {
                    Text(
                        modifier = Modifier.align(Alignment.Center),
                        text = "No employees",
                        style = MaterialTheme.typography.headlineLarge,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(
                        top = paddingValues.calculateTopPadding() + 16.dp, bottom = 24.dp
                    ),
                    modifier = Modifier.padding(horizontal = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    stickyHeader {
                        Box(
                            modifier = Modifier
                                .background(
                                    color = MaterialTheme.colorScheme.primary.copy(0.7f),
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .padding(8.dp)
                        ) {
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = "Total: ${state.list.size}",
                                style = MaterialTheme.typography.labelLarge,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                    item {
                        Column(
                            modifier = Modifier
                                .background(
                                    color = MaterialTheme.colorScheme.surface,
                                    shape = RoundedCornerShape(4.dp)
                                )
                                .padding(horizontal = 16.dp)
                        ) {
                            state.list.forEachIndexed { index, employee ->
                                EmployeeItem(data = employee, onDelete = {
                                    if (it != null) {
                                        viewModel.deleteEmployee(it)
                                    }
                                })
                                if (index != state.list.size - 1) {
                                    Divider(
                                        thickness = 1.dp,
                                        color = MaterialTheme.colorScheme.onSurface.copy(0.2f)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

}

@Composable
fun EmployeeItem(data: EmployeeData, onDelete: (Long?) -> Unit) {
    var showDetails by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
            .clickable { showDetails = !showDetails },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(text = data.firstName, style = MaterialTheme.typography.headlineSmall)
            if (data.timestamp > 0) {
                Text(
                    text = "Last seen: ${formatDate(data.timestamp)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(0.5f)
                )
            }
        }
        data.photo?.let {
            Image(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(4.dp)),
                bitmap = it.asImageBitmap(),
                contentDescription = null
            )
        }
        if (showDetails) {
            data.photo?.let {
                DetailsDialog(
                    image = it.asImageBitmap(),
                    onDismiss = { showDetails = false },
                    onDelete = {
                        showDetails = false
                        onDelete(data.id)
                    })
            }
        }
    }
}

@Composable
fun DetailsDialog(
    image: ImageBitmap, onDismiss: () -> Unit, onDelete: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
    ) {
        Card(
            shape = RoundedCornerShape(4.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .size(300.dp, 300.dp)
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    modifier = Modifier.size(200.dp), bitmap = image, contentDescription = null
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        modifier = Modifier
                            .padding(end = 5.dp)
                            .weight(1f), onClick = onDismiss
                    ) {
                        Text(text = "Cancel", color = MaterialTheme.colorScheme.onPrimary)
                    }
                    Button(
                        modifier = Modifier
                            .padding(start = 5.dp)
                            .weight(1f),
                        onClick = onDelete,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                    ) {
                        Text(text = "Delete", color = MaterialTheme.colorScheme.onError)
                    }
                }


            }
        }
    }
}

fun formatDate(timestamp: Long): CharSequence =
    android.text.format.DateFormat.format("dd MM, yyyy - hh:mm:ss a", Date(timestamp))

@Composable
fun AppToolbarText(text: String) {
    Text(
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Start,
        text = text,
        color = MaterialTheme.colorScheme.onPrimary
    )
}

@Composable
fun ToolbarAction(
    image: ImageVector, onClick: () -> Unit
) {
    IconButton(onClick = onClick) {
        Icon(
            imageVector = image,
            tint = MaterialTheme.colorScheme.onPrimary,
            contentDescription = null
        )
    }
}