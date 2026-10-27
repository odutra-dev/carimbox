package com.dutradev.carimbosocian.di

import android.content.Context
import com.dutradev.carimbosocian.data.local.PreferencesDataStore
import com.dutradev.carimbosocian.data.remote.SupabaseClientProvider
import com.dutradev.carimbosocian.data.repository.PedidosRepository
import com.dutradev.carimbosocian.data.repository.PedidosRepositoryImpl

interface AppContainer {
    val pedidosRepository: PedidosRepository
    val preferencesDataStore: PreferencesDataStore
}

class DefaultAppContainer(private val context: Context) : AppContainer {
    override val pedidosRepository: PedidosRepository by lazy {
        PedidosRepositoryImpl(SupabaseClientProvider.client)
    }

    override val preferencesDataStore: PreferencesDataStore by lazy {
        PreferencesDataStore(context)
    }
}
