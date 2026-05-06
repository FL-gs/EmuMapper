package dev.emuctrlr.app.features.pairing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dev.emuctrlr.app.core.pairing.PairingEngine

class PairingViewModelFactory(
    private val pairingEngine: PairingEngine
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return PairingViewModel(pairingEngine) as T
    }
}
