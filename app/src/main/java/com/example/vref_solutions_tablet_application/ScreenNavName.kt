package com.example.vref_solutions_tablet_application

sealed class ScreenNavName(val route: String) {
    object Login: ScreenNavName(route = "Login")
    object MainMenu: ScreenNavName(route = "MainMenu")
    object LiveTraining: ScreenNavName(route = "LiveTraining")
    object NewTraining: ScreenNavName(route = "NewTraining")
    object AdminMenu: ScreenNavName(route = "AdminMenu")
}
