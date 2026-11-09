package com.dutradev.carimbosocian

import android.app.Application
import android.os.Build
import android.util.Log
import com.dutradev.carimbosocian.di.AppContainer
import com.dutradev.carimbosocian.di.DefaultAppContainer
import org.conscrypt.Conscrypt
import java.security.Security

class CarimboxApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        setupSecurityProviders()
        setupCrashHandler()
        container = DefaultAppContainer(this)
    }

    private fun setupSecurityProviders() {
        // Ignora no ambiente de testes JVM do Robolectric para evitar falha no carregamento JNI
        if (Build.FINGERPRINT.contains("robolectric", ignoreCase = true) ||
            Build.HARDWARE == "robolectric"
        ) {
            return
        }
        try {
            // Insere o provedor Conscrypt para garantir suporte a TLS 1.2/1.3 e CAs modernas
            // mesmo em versões antigas como Android 5.0/5.1 (API 21/22)
            Security.insertProviderAt(Conscrypt.newProvider(), 1)
        } catch (e: Throwable) {
            Log.w("CarimboxApp", "Não foi possível registrar Conscrypt", e)
        }
    }

    private fun setupCrashHandler() {
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            Log.e("CarimboxCrash", "Exceção não tratada na thread ${thread.name}: ${throwable.message}", throwable)
            defaultHandler?.uncaughtException(thread, throwable)
        }
    }
}
