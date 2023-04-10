package com.example.vref_solutions_tablet_application

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.vref_solutions_tablet_application.screens.*

@Composable
fun SetupNavGraph(
    navController: NavHostController
) {
    NavHost(navController = navController, startDestination = ScreenNavName.Login.route) {
        composable( route = ScreenNavName.Login.route) {
            LoginScreen(navController = navController)
        }

        composable( route = ScreenNavName.MainMenu.route) {
            MainMenu(navController = navController)
        }

        composable( route = ScreenNavName.LiveTraining.route) {
            LiveTrainingScreen(navController = navController)
        }

        composable( route = ScreenNavName.NewTraining.route) {
            NewTrainingScreen(navController = navController)
        }

        composable( route = ScreenNavName.AdminMenu.route) {
            SuperAdminAndAdminScreen(navController = navController)
        }
    }
}