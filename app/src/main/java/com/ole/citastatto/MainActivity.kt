package com.ole.citastatto

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.ole.citastatto.data.Month
import com.ole.citastatto.ui.theme.CitasTattoTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {

    private val appointmentCollectionRef = Firebase.firestore.collection("Appointments")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CitasTattoTheme {
                ButtonAppointment()
            }
        }
    }

     private fun saveAppointment(appointment: Month) = CoroutineScope(Dispatchers.IO).launch {
        try {
            appointmentCollectionRef.add(appointment).await()
            withContext(Dispatchers.Main) {
                Toast.makeText(
                    this@MainActivity,
                    "éxito en el guardado en firestore",
                    Toast.LENGTH_LONG
                ).show()
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    @Composable
    fun ButtonAppointment() {
        Button(
            onClick = {
                val month=Month(31,"Marzo",3,2)
                saveAppointment(month)
            },
        ) {
            Text(text = "Botón")
        }
    }

}


