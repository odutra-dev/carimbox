package com.dutradev.carimbosocian.data.remote

import com.dutradev.carimbosocian.BuildConfig
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.ktor.client.engine.okhttp.OkHttp
import okhttp3.ConnectionSpec

object SupabaseClientProvider {
    val client: SupabaseClient by lazy {
        val url = BuildConfig.SUPABASE_URL.ifBlank { "https://placeholder.supabase.co" }
        val key = BuildConfig.SUPABASE_KEY.ifBlank { "placeholder-key" }

        createSupabaseClient(
            supabaseUrl = url,
            supabaseKey = key
        ) {
            httpEngine = OkHttp.create {
                config {
                    connectionSpecs(
                        listOf(
                            ConnectionSpec.MODERN_TLS,
                            ConnectionSpec.COMPATIBLE_TLS,
                            ConnectionSpec.CLEARTEXT
                        )
                    )
                }
            }
            install(Postgrest)
        }
    }
}
