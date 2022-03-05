package com.ole.citastatto

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import com.ole.citastatto.ui.animations.LottieExample
import com.ole.citastatto.ui.theme.CitasTattoTheme

class MainActivity : ComponentActivity() {

    private val monthViewModel by viewModels<MontViewModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            CitasTattoTheme {
                Column {
                    ButonRetrieveData()
                    Text(text = monthViewModel.name.value?:" fallo al cargar")
                    for(day in monthViewModel.daysAvailables.value){
                        Row(horizontalArrangement = Arrangement.Center) {
                            Text(text = "${day.weekDay}, ${day.dayInMonth}  " )
                            Column() {
                                for( stripe in day.stripes){
                                    Text(text = "${day.stripes}")
                                }
                                
                            }
                        }

                    }
                    LottieExample()
                }
            }
        }
    }


    @Composable
    fun ButonRetrieveData() {
            Button(onClick = {
                monthViewModel.retrieveMonths()
                monthViewModel.retrieveStripes(185)
            }){
                Text(text = "Cargar datos")
            }
    }
}


