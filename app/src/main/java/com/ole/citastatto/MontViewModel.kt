package com.ole.citastatto

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.ole.citastatto.data.Day
import com.ole.citastatto.data.Month
import com.ole.citastatto.data.Stripe
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

//TODO: Revisar si es mejor hacer el querySnapshot una sola vez para toda la clase y cómo hacerlo
class MontViewModel : ViewModel() {

    private val _month = mutableStateOf(Month())
    var month: State<Month> = _month

    private val _daysAvailables = mutableStateOf<MutableList<Day>>(mutableListOf())
    var daysAvailables: State<MutableList<Day>> = _daysAvailables

    private val monthCollectionRef = Firebase.firestore.collection("Months")

    fun retrieveMonths() {
        viewModelScope.launch(Dispatchers.IO) {
            val querySnapshot = monthCollectionRef.whereEqualTo("monthName", "APRIL").get().await()
            var month: Month? = Month()
            for (document in querySnapshot.documents) {
                month = document.toObject<Month>()

            }
            withContext(Dispatchers.Main) {
                if (month != null) {
                    _month.value = month
                }
            }

        }
    }

    fun retrieveAvailableStripes(duration: Int) {

        viewModelScope.launch(Dispatchers.IO) {

            val auxDaysWithStripe: MutableList<Day> = mutableListOf()
            val querySnapshot = monthCollectionRef.whereEqualTo("monthName", "APRIL").get().await()

            for (document in querySnapshot.documents) {
                val month = document.toObject<Month>()
                month?.let {
                    for (day in month.days) {

                        var index = 0
                        if (auxDaysWithStripe.size < 3) {
                            for (strip in day.stripes) {
                                if (strip.duration < duration) {
                                    day.stripes.removeAt(index)

                                }
                                index++
                            }

                            if (day.stripes.isNotEmpty()) {

                                auxDaysWithStripe.add(day)

                            }
                        }
                    }
                }
            }
/*
            val auxDaysSplitedStripes: MutableList<Day> = splitStripes(auxDaysWithStripe, duration)
*/
            withContext(Dispatchers.Main) { _daysAvailables.value = auxDaysWithStripe }
        }
    }

    // si hay hueco de 3 a 7 y la cita dura una hora sólo se debe ofrecer de 3 a 4 y de 6 a 7 para juntar las citas
     /*fun splitStripes(daysStripesNoSplited: MutableList<Day>, duration: Int): MutableList<Day> {
         for (day in daysStripesNoSplited) {
             var auxStripe = Stripe()
             for (stripe in day.stripes) {
                 var split = 1
                 if (stripe.duration > duration && split < 2) { // si el hueco libre dura lo mismo que la cita, no se puede splitear
                     auxStripe.momentIni = stripe.momentIni
                     for (moment in day.moments) {
                         var auxStripe2 = Stripe(
                             momentIni = stripe.momentIni,
                             momentFin = moment
                         ) //triquiñuela para calcular el final del primer split
                         auxStripe2.durationCalc()
                         if (auxStripe2.duration == duration) {
                             auxStripe.momentFin = moment

                         }
                     }
                 }
             }
         }

         return mutableListOf()
     }*/
      /*fun bookAppointment(newStripeMap: Stripe, day: Day, monthNumber: Int) {
        CoroutineScope(Dispatchers.IO).launch {

            }
                //withContext(Dispatchers.Main) {
            }*/


}