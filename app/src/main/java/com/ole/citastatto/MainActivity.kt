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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ole.citastatto.data.Day
import com.ole.citastatto.ui.theme.CitasTattoTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class MainActivity : ComponentActivity() {

    private val monthViewModel by viewModels<MontViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            CitasTattoTheme {
                Column {
                    ButonRetrieveData()
                    ShowStripe(monthViewModel.daysAvailables.value as MutableList<Day>)


                    //LottieExample()
                }
            }
        }
    }

    @Composable
    fun ShowNoStripe() {
        Surface(
            modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)
        ) {
            Text(text = " Lo sentimos no hay citas disponibles para este mes")
        }
    }

    @Composable
    fun ShowStripe(List: MutableList<Day>) {

        androidx.compose.material.Surface(
            modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)
        ) {

            if(!monthViewModel.someStripe.value) ShowNoStripe()
            Column() {
                for (day in List) {
                    Row(
                        modifier = Modifier.padding(vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        var date = LocalDate.of(
                                        monthViewModel.month.value.year,
                                        monthViewModel.month.value.monthNumber,
                                        day.dayInMonth
                                    ).format(
                                        DateTimeFormatter
                                            .ofLocalizedDate(FormatStyle.LONG)
                                    )
                        Text(
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            text = "${day.weekDay}, $date "
                        )
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp)
                        ) {
                            for (stripe in day.stripes) {
                                stripe.updateInternals()
                                OutlinedButton(onClick = { monthViewModel.bookAppointment(stripe,day,monthViewModel.month.value.monthNumber) }) {
                                    Text(text = "$stripe")

                                }
                            }

                        }
                    }

                }
            }
        }


    }

    @Composable
    fun ButonRetrieveData() {
        Button(onClick = {
            monthViewModel.retrieveMonths()
            monthViewModel.retrieveAvailableStripes(500)
        }) {
            Text(text = "Cargar datos")
        }
    }
}


