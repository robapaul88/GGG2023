package org.tensorflow.lite.examples.detection.list.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.tensorflow.lite.examples.detection.firebase.FirebaseProvider

class ListViewModel(val firebaseProvider: FirebaseProvider) : ViewModel() {

    data class EmployeeListUiState(
        val list: List<EmployeeData> = mutableListOf(),
        val isLoading: Boolean = true
    )

    private val _uiState: MutableStateFlow<EmployeeListUiState> = MutableStateFlow(
        EmployeeListUiState()
    )
    val uiState: StateFlow<EmployeeListUiState> = _uiState

    init {
        println("merepere init viewmodel")
        getData()
    }

    private fun getData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(list = firebaseProvider.getEmployees(), isLoading = false)
        }
    }

    companion object {
        val Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                return ListViewModel(firebaseProvider = FirebaseProvider) as T
            }
        }
    }
}