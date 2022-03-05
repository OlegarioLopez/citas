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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

//TODO: Revisar si es mejor hacer el querySnapshot una sola vez para toda la clase y cómo hacerlo
class MontViewModel : ViewModel() {
    private val _name = mutableStateOf("SIN CARGA")
    var name: State<String> = _name

    private val _daysAvailables = mutableStateOf<MutableList<Day>>(mutableListOf())
    var daysAvailables: State<MutableList<Day>> = _daysAvailables

    private val monthCollectionRef = Firebase.firestore.collection("Months")

    fun retrieveMonths() {
        viewModelScope.launch(Dispatchers.IO) {
            val querySnapshot = monthCollectionRef.whereEqualTo("monthName", "APRIL").get().await()
            val sb = StringBuilder()
            for (document in querySnapshot.documents) {
                val month = document.toObject<Month>()

                sb.append(month?.monthName)
            }
            withContext(Dispatchers.Main) { _name.value = sb.toString() }

        }
    }

    fun retrieveStripes(duration: Int) {

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
   /* fun splitStripes(daysStripesNoSplited: MutableList<Day>, duration: Int): MutableList<Day> {
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
    }
    private fun bookAppointment( newStripeMap: Map<String, Any>) = CoroutineScope(Dispatchers.IO).launch {
        val monthQuery = monthCollectionRef
            .whereEqualTo("firstName", person.firstName)
            .whereEqualTo("lastName", person.lastName)
            .whereEqualTo("age", person.age)
            .get()
            .await()
        if(personQuery.documents.isNotEmpty()) {
            for(document in personQuery) {
                try {
                    //personCollectionRef.document(document.id).update("age", newAge).await()
                    personCollectionRef.document(document.id).set(
                        newPersonMap,
                        SetOptions.merge()
                    ).await()
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        } else {
            withContext(Dispatchers.Main) {
                Toast.makeText(this@MainActivity, "No persons matched the query.", Toast.LENGTH_LONG).show()
            }
        }
    }*/
}