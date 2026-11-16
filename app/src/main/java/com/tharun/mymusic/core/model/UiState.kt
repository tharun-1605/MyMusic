package com.tharun.mymusic.core.model

sealed interface UiState<out T> {
    data object Idle : UiState<Nothing>
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Empty(val message: String = "No items found") : UiState<Nothing>
    data class Error(val message: String) : UiState<Nothing>
}
