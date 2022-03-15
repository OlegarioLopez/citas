package com.ole.citastatto.Screens

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ole.citastatto.MontViewModel
import com.ole.citastatto.data.Day
import com.ole.citastatto.data.Stripe
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle



    @Composable
    fun ShowNoStripe() {
        Surface(
            modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)
        ) {
            Text(text = " Lo sentimos no hay citas disponibles para este mes")
        }
    }

    @Composable
    fun ShowStripe( monthViewModel: MontViewModel) {

        val daysList = monthViewModel.daysAvailables.value
        val stripesList = monthViewModel.stripesAvailables.value
        androidx.compose.material.Surface(
            modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)
        ) {

            if(!monthViewModel.someStripe.value) ShowNoStripe()
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
                            for (stripe in stripesList) {
                                stripe.updateInternals()
                                if(stripe.dayInMonth == day.dayInMonth){
                                    OutlinedButton(
                                        onClick = { monthViewModel.bookAppointment(stripe,day,monthViewModel.month.value.monthNumber) }) {
                                        Text(text = "$stripe")
                                    }
                                }

                            }

                        }
                    } }


                }
            }
        }


    }
