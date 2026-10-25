package com.dutradev.carimbosocian.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dutradev.carimbosocian.data.local.PreferencesDataStore
import com.dutradev.carimbosocian.data.repository.PedidosRepository
import com.dutradev.carimbosocian.ui.screens.atualizar.AtualizarPedidoViewModel
import com.dutradev.carimbosocian.ui.screens.home.HomeViewModel
import com.dutradev.carimbosocian.ui.screens.novo.NovoPedidoViewModel
import com.dutradev.carimbosocian.ui.screens.search.SearchViewModel
import com.dutradev.carimbosocian.ui.screens.settings.SettingsViewModel
import com.dutradev.carimbosocian.ui.screens.ver.VerPedidoViewModel

class ViewModelFactory(
    private val appContainer: AppContainer,
    private val pedidoId: Long? = null
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(appContainer.pedidosRepository) as T
            }
            modelClass.isAssignableFrom(NovoPedidoViewModel::class.java) -> {
                NovoPedidoViewModel(appContainer.pedidosRepository) as T
            }
            modelClass.isAssignableFrom(VerPedidoViewModel::class.java) -> {
                requireNotNull(pedidoId) { "pedidoId é necessário para VerPedidoViewModel" }
                VerPedidoViewModel(pedidoId, appContainer.pedidosRepository) as T
            }
            modelClass.isAssignableFrom(AtualizarPedidoViewModel::class.java) -> {
                requireNotNull(pedidoId) { "pedidoId é necessário para AtualizarPedidoViewModel" }
                AtualizarPedidoViewModel(pedidoId, appContainer.pedidosRepository) as T
            }
            modelClass.isAssignableFrom(SearchViewModel::class.java) -> {
                SearchViewModel(appContainer.pedidosRepository) as T
            }
            modelClass.isAssignableFrom(SettingsViewModel::class.java) -> {
                SettingsViewModel(appContainer.preferencesDataStore) as T
            }
            else -> throw IllegalArgumentException("ViewModel desconhecida: ${modelClass.name}")
        }
    }
}
