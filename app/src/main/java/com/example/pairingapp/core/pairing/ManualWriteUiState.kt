package com.example.pairingapp.core.pairing

sealed interface ManualWriteUiState {
    data object Idle : ManualWriteUiState
    data class Holding(val progress: Float) : ManualWriteUiState
    data object Writing : ManualWriteUiState
    data object Success : ManualWriteUiState
}