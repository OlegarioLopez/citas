package com.ole.citastatto

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.*
import com.ole.citastatto.ui.theme.CitasTattoTheme

class MainActivity : ComponentActivity() {

    private val montViewModel by viewModels<MontViewModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            CitasTattoTheme {
                Column() {
                    ButonRetrieveData()
                    Text(text = montViewModel.name.value?:" fallo al cargar")
                }
            }
        }
    }


    @Composable
    fun ButonRetrieveData() {
            Button(onClick = { montViewModel.retrieveMonths()}){
                Text(text = "Cargar datos")
            }
    }
}


