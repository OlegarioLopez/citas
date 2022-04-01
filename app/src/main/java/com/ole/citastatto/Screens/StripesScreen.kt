package com.ole.citastatto.Screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

        val today = LocalDate.now().dayOfMonth
        var daysList = monthViewModel.daysAvailables.value.filter { it.dayInMonth >=  today }
        val spotsList = monthViewModel.spotsAvailables.value



            if(!monthViewModel.someSpot.value) ShowNoSpot()
            LazyColumn(
            ) {
                for (day in daysList) {
                    item {
                        Card(
                            shape = RoundedCornerShape(5.dp),
                            modifier = Modifier.padding(5.dp),
                            elevation = 5.dp,
                            backgroundColor = Color.Transparent
                        ){
                            Row(
                                modifier = Modifier.padding(10.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                var date = LocalDate.of(
                                    monthViewModel.month.value.year,
                                    monthViewModel.month.value.monthNumber,
                                    day.dayInMonth
                                ).format(
                                    DateTimeFormatter
                                        .ofLocalizedDate(FormatStyle.MEDIUM)
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
                                                shape = RoundedCornerShape(5.dp),
                                                onClick = { monthViewModel.bookAppointment(spot,day,monthViewModel.month.value.monthNumber)
                                                    navController.popBackStack()}) {
                                                Text(text = "$spot")
                                            }
                                        }

                                    }

                                }
                            }
                        } }


                }
            }



    }
