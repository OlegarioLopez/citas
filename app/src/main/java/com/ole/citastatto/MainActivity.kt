package com.ole.citastatto

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ole.citastatto.Screens.MapGoogle
import com.ole.citastatto.Screens.ShowSpot
import com.ole.citastatto.ui.theme.CitasTattoTheme


class MainActivity : ComponentActivity() {

    private val monthViewModel by viewModels<MontViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContent {
            CitasTattoTheme {
                Column {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = "HomeScreen") {
                        composable("HomeScreen") { HomeScreen(navController) }
                        composable("Spots") { ShowSpot(monthViewModel,navController) }
                        composable("map"){MapGoogle()}
                    }
                    //LottieExample()
                }
            }
        }
    }



    @Composable
    fun HomeScreen(navController: NavHostController) {
        Column(
            Modifier.fillMaxSize(),
            verticalArrangement  = Arrangement.Center,
            horizontalAlignment  = Alignment.CenterHorizontally)
        {
            Button(onClick = {
                monthViewModel.retrieveMonths()
                monthViewModel.retrieveAvailableSpots(50)
                navController.navigate("Spots")
            }) {
                Text(text = "Reservar cita")
            }
            Button(onClick = {

                navController.navigate("map")
            }) {
                Text(text = "¿Dónde estamos?")
            }
        }

    }
}


