package com.dutradev.carimbosocian.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.dutradev.carimbosocian.di.AppContainer
import com.dutradev.carimbosocian.di.ViewModelFactory
import com.dutradev.carimbosocian.ui.screens.atualizar.AtualizarPedidoScreen
import com.dutradev.carimbosocian.ui.screens.atualizar.AtualizarPedidoViewModel
import com.dutradev.carimbosocian.ui.screens.home.HomeScreen
import com.dutradev.carimbosocian.ui.screens.home.HomeViewModel
import com.dutradev.carimbosocian.ui.screens.novo.NovoPedidoScreen
import com.dutradev.carimbosocian.ui.screens.novo.NovoPedidoViewModel
import com.dutradev.carimbosocian.ui.screens.search.SearchScreen
import com.dutradev.carimbosocian.ui.screens.search.SearchViewModel
import com.dutradev.carimbosocian.ui.screens.settings.SettingsScreen
import com.dutradev.carimbosocian.ui.screens.settings.SettingsViewModel
import com.dutradev.carimbosocian.ui.screens.ver.VerPedidoScreen
import com.dutradev.carimbosocian.ui.screens.ver.VerPedidoViewModel
import com.dutradev.carimbosocian.ui.util.isTabletOrLandscape

@Composable
fun CarimboxNavGraph(
    navController: NavHostController,
    appContainer: AppContainer,
    modifier: Modifier = Modifier
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val navItems = listOf(
        BottomNavItem.Home,
        BottomNavItem.Search,
        BottomNavItem.Settings
    )

    val showNav = navItems.any { it.route == currentRoute }
    val isTablet = isTabletOrLandscape()

    val onNavigateToItem: (String) -> Unit = { targetRoute ->
        if (currentRoute != targetRoute) {
            navController.navigate(targetRoute) {
                popUpTo(Screen.Home.route) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        }
    }

    if (isTablet) {
        // Layout Responsivo para Tablet: NavigationRail na lateral esquerda
        Row(modifier = modifier.fillMaxSize()) {
            if (showNav) {
                NavigationRail {
                    navItems.forEach { item ->
                        val selected = currentRoute == item.route
                        NavigationRailItem(
                            selected = selected,
                            onClick = { onNavigateToItem(item.route) },
                            icon = {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = stringResource(item.titleResId)
                                )
                            },
                            label = { Text(stringResource(item.titleResId)) }
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            ) {
                NavContent(navController = navController, appContainer = appContainer)
            }
        }
    } else {
        // Layout Celular: NavigationBar tradicional na parte inferior
        Scaffold(
            modifier = modifier,
            bottomBar = {
                if (showNav) {
                    NavigationBar {
                        navItems.forEach { item ->
                            val selected = currentRoute == item.route
                            NavigationBarItem(
                                selected = selected,
                                onClick = { onNavigateToItem(item.route) },
                                icon = {
                                    Icon(
                                        imageVector = item.icon,
                                        contentDescription = stringResource(item.titleResId)
                                    )
                                },
                                label = { Text(stringResource(item.titleResId)) }
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                NavContent(navController = navController, appContainer = appContainer)
            }
        }
    }
}

@Composable
private fun NavContent(
    navController: NavHostController,
    appContainer: AppContainer
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        // Home / Lista de Pedidos
        composable(Screen.Home.route) {
            val vm: HomeViewModel = viewModel(
                factory = ViewModelFactory(appContainer)
            )
            HomeScreen(
                viewModel = vm,
                onNavigateToDetail = { pedidoId ->
                    navController.navigate(Screen.VerPedido.createRoute(pedidoId))
                },
                onNavigateToNovoPedido = {
                    navController.navigate(Screen.NovoPedido.route)
                }
            )
        }

        // Busca
        composable(Screen.Search.route) {
            val vm: SearchViewModel = viewModel(
                factory = ViewModelFactory(appContainer)
            )
            SearchScreen(
                viewModel = vm,
                onNavigateToDetail = { pedidoId ->
                    navController.navigate(Screen.VerPedido.createRoute(pedidoId))
                }
            )
        }

        // Configurações
        composable(Screen.Settings.route) {
            val vm: SettingsViewModel = viewModel(
                factory = ViewModelFactory(appContainer)
            )
            SettingsScreen(viewModel = vm)
        }

        // Novo Pedido
        composable(Screen.NovoPedido.route) {
            val vm: NovoPedidoViewModel = viewModel(
                factory = ViewModelFactory(appContainer)
            )
            NovoPedidoScreen(
                viewModel = vm,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Ver Pedido
        composable(
            route = Screen.VerPedido.route,
            arguments = listOf(navArgument("pedidoId") { type = NavType.LongType })
        ) { backStackEntry ->
            val pedidoId = backStackEntry.arguments?.getLong("pedidoId") ?: 0L
            val vm: VerPedidoViewModel = viewModel(
                factory = ViewModelFactory(appContainer, pedidoId)
            )
            VerPedidoScreen(
                viewModel = vm,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEdit = { id ->
                    navController.navigate(Screen.AtualizarPedido.createRoute(id))
                }
            )
        }

        // Atualizar Pedido
        composable(
            route = Screen.AtualizarPedido.route,
            arguments = listOf(navArgument("pedidoId") { type = NavType.LongType })
        ) { backStackEntry ->
            val pedidoId = backStackEntry.arguments?.getLong("pedidoId") ?: 0L
            val vm: AtualizarPedidoViewModel = viewModel(
                factory = ViewModelFactory(appContainer, pedidoId)
            )
            AtualizarPedidoScreen(
                viewModel = vm,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
