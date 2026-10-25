package com.dutradev.carimbosocian.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Search : Screen("search")
    object Settings : Screen("settings")
    object NovoPedido : Screen("novo_pedido")
    object VerPedido : Screen("ver_pedido/{pedidoId}") {
        fun createRoute(pedidoId: Long) = "ver_pedido/$pedidoId"
    }
    object AtualizarPedido : Screen("atualizar_pedido/{pedidoId}") {
        fun createRoute(pedidoId: Long) = "atualizar_pedido/$pedidoId"
    }
}

sealed class BottomNavItem(
    val route: String,
    val titleResId: Int,
    val icon: ImageVector
) {
    object Home : BottomNavItem(
        route = Screen.Home.route,
        titleResId = com.dutradev.carimbosocian.R.string.pedidos_titulo,
        icon = Icons.Default.List
    )

    object Search : BottomNavItem(
        route = Screen.Search.route,
        titleResId = com.dutradev.carimbosocian.R.string.search_page,
        icon = Icons.Default.Search
    )

    object Settings : BottomNavItem(
        route = Screen.Settings.route,
        titleResId = com.dutradev.carimbosocian.R.string.configuracoes_titulo,
        icon = Icons.Default.Settings
    )
}
