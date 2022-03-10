package com.ole.citastatto

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ole.citastatto.Screens.ShowStripe
import com.ole.citastatto.data.Day
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
                        composable("HomeScreen") { ButonFindStripe(navController) }
                        composable("Stripes") { ShowStripe(monthViewModel.daysAvailables.value as MutableList<Day>,monthViewModel) }
                        /*...*/
                    }
                    //LottieExample()
                }
            }
        }
    }



    @Composable
    fun ButonFindStripe(navController: NavHostController) {
        Button(onClick = {
            monthViewModel.retrieveMonths()
            monthViewModel.retrieveAvailableStripes(50)
            navController.navigate("Stripes")
        }) {
            Text(text = "Cargar datos")
        }
    }
}


