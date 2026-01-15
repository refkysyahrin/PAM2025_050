package com.example.yourtis.ui.theme.view.controllNavigasi

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.yourtis.ui.theme.view.auth.HalamanLogin
import com.example.yourtis.ui.theme.view.auth.HalamanRegister
import com.example.yourtis.ui.theme.view.pembeli.HalamanCart
import com.example.yourtis.ui.theme.view.pembeli.HalamanCheckout
import com.example.yourtis.ui.theme.view.pembeli.HalamanDetailSayur
import com.example.yourtis.ui.theme.view.petani.HalamanEntrySayur
import com.example.yourtis.ui.theme.view.petani.HalamanHomePetani
import com.example.yourtis.ui.theme.view.petani.HalamanKelolaProduk
import com.example.yourtis.ui.theme.view.petani.HalamanLaporan
import com.example.yourtis.ui.view.pembeli.HalamanKatalog


object DestinasiLogin { const val route = "login" }
object DestinasiRegister { const val route = "register" }
object DestinasiHomePetani { const val route = "home_petani" }
object DestinasiKelolaProduk { const val route = "kelola_produk" }
object DestinasiEntrySayur { const val route = "entry_sayur?id={id}" }
object DestinasiHomePembeli { const val route = "home_pembeli" }
object DestinasiCart { const val route = "cart" }
object DestinasiDetail { const val route = "detail_sayur/{id}" }
object DestinasiCheckout { const val route = "checkout" }

@Composable
fun PengelolaHalaman(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = DestinasiLogin.route) {
        composable(DestinasiLogin.route) {
            HalamanLogin(
                onLoginSuccess = { user ->
                    if (user.role == "Petani") navController.navigate(DestinasiHomePetani.route)
                    else navController.navigate(DestinasiHomePembeli.route)
                },
                onNavigateToRegister = { navController.navigate(DestinasiRegister.route) }
            )
        }

        composable(DestinasiRegister.route) {
            HalamanRegister(onRegisterSuccess = { navController.popBackStack() }, onNavigateBack = { navController.popBackStack() })
        }

        composable(DestinasiHomePetani.route) {
            HalamanHomePetani(
                onLogout = { navController.navigate(DestinasiLogin.route) { popUpTo(0) } },
                onNavigateToKelolaProduk = { navController.navigate(DestinasiKelolaProduk.route) },
                onNavigateToLaporan = { navController.navigate("laporan_transaksi") }
            )
        }

        composable(DestinasiHomePembeli.route) {
            HalamanKatalog(
                onLogout = { navController.navigate(DestinasiLogin.route) { popUpTo(0) } },
                onNavigateToCart = { navController.navigate(DestinasiCart.route) },
                onNavigateToDetail = { id -> navController.navigate("detail_sayur/$id") }
            )
        }

        composable(
            route = DestinasiDetail.route,
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id") ?: 0
            HalamanDetailSayur(idSayur = id, onNavigateBack = { navController.popBackStack() })
        }

        composable(DestinasiCart.route) {
            HalamanCart(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToCheckout = { navController.navigate(DestinasiCheckout.route) }
            )
        }

        composable(DestinasiCheckout.route) {
            HalamanCheckout(
                onNavigateBack = { navController.popBackStack() },
                onCheckoutSuccess = {
                    navController.navigate(DestinasiHomePembeli.route) {
                        popUpTo(DestinasiHomePembeli.route) { inclusive = true }
                    }
                }
            )
        }

        // ... rute lainnya (Laporan, Kelola Produk, dll)
    }
}