package dev.emumapper.app.features.pairing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dev.emumapper.app.core.pairing.PairingEngine

class PairingViewModelFactory(
    private val pairingEngine: PairingEngine
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return PairingViewModel(pairingEngine) as T
    }
}
