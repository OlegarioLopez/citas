package com.ole.citastatto.Screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.ole.citastatto.MontViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle



    @Composable
    fun ShowNoSpot() {
        Surface(
            modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)
        ) {
            Text(text = " Lo sentimos no hay citas disponibles para este mes")
        }
    }

    @Composable
    fun ShowSpot(monthViewModel: MontViewModel, navController: NavHostController) {

        val daysList = monthViewModel.daysAvailables.value
        val spotsList = monthViewModel.spotsAvailables.value
        androidx.compose.material.Surface(
            modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)
        ) {

            if(!monthViewModel.someSpot.value) ShowNoSpot()
            LazyColumn(
            ) {
                for (day in daysList) {
                    item { Row(
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
                            for (spot in spotsList) {
                                spot.updateInternals()
                                if(spot.dayInMonth == day.dayInMonth){
                                    OutlinedButton(
                                        onClick = { monthViewModel.bookAppointment(spot,day,monthViewModel.month.value.monthNumber)
                                            navController.popBackStack()}) {
                                        Text(text = "$spot")
                                    }
                                }

                            }

                        }
                    } }


                }
            }
        }


    }
