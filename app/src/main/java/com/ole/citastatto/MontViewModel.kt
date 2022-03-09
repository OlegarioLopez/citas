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
import java.time.LocalTime

//TODO: Revisar si es mejor hacer el querySnapshot una sola vez para toda la clase y cómo hacerlo
class MontViewModel : ViewModel() {

    private val _month = mutableStateOf(Month())
    var month: State<Month> = _month

    private val _someStripe = mutableStateOf(true)
    var someStripe = _someStripe

    private val _daysAvailables = mutableStateOf<List<Day>>(mutableListOf())
    var daysAvailables: State<List<Day>> = _daysAvailables

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

    fun retrieveAvailableStripes(durationBook: Int) {

        viewModelScope.launch(Dispatchers.IO) {

            val auxDaysWithStripe: MutableList<Day> = mutableListOf()
            val querySnapshot = monthCollectionRef.whereEqualTo("monthName", "APRIL").get().await()


            for (document in querySnapshot.documents) {
                val month = document.toObject<Month>()
                month?.let {
                    for (day in month.days) {

                        val auxDay = cloneDay(day)
                        if (day.stripes.isEmpty()) {

                            continue
                        }
                        if (auxDaysWithStripe.size == 3) {
                            break
                        }

                        for (strip in day.stripes) {

                            if (auxDay.stripes.size >= 5) {
                                break
                            }

                            strip.updateInternals()
                            // TODO hay que verificar si el Strip no es nulo
                            if (strip.duration >= durationBook) {
                                auxDay.stripes.addAll(splitStripe(strip, durationBook))
                                //TODO Ordenar las stripes por moment Ini sortBy(momentIni[0]) ?
                                auxDay.stripes.removeAt(0)


                            } else auxDay.stripes.removeAt(0)
                        }
                        if (auxDay.stripes.isNotEmpty()) {
                            auxDaysWithStripe.add(auxDay)
                        }

                    }
                }
            }
/*
           TODO terminar la funcion splitStripes para mostrar las citas adecuadamente a los usuario (duración y juntar)
*/
            withContext(Dispatchers.Main) {
                var stripeAvailable = false
                for (day in auxDaysWithStripe) {
                    if (day.stripes.isNotEmpty()) stripeAvailable = true
                }
                if (!stripeAvailable) _someStripe.value = stripeAvailable
                _daysAvailables.value = auxDaysWithStripe
            }
        }
    }

    private fun cloneDay(day: Day): Day {
        val a = day.weekDay
        val b = day.dayInMonth
        val c = day.startMorning
        val d = day.finishMorning
        val e = day.startEvening
        val f = day.finishtEvening
        val g = day.stripes.toMutableList()
        val result = Day(a, b, c, d, e, f, g)
        return result

    }

    fun splitStripe(Stripe: Stripe, durationBook: Int): MutableList<Stripe> {
        Stripe.updateInternals() //TODO BORRAR
        val splitedStripe = mutableListOf<Stripe>()

        if (durationBook == Stripe.duration.toInt()) {
            Stripe.availability = false
            splitedStripe.add(Stripe)
            return splitedStripe
        }

        val auxStripe = Stripe.copy()
        val auxStripe2 = Stripe.copy()
        auxStripe.momentFin = sumTime(
            Stripe.momentIni,
            durationBook
        )
        auxStripe.availability = false
        auxStripe.bookedBy = "usuario de prueba"
        auxStripe.updateInternals()
        splitedStripe.add(auxStripe)
        auxStripe2.momentIni = minusTime(Stripe.momentFin, durationBook)
        auxStripe2.availability = false
        auxStripe2.bookedBy = "usuario de prueba"
        auxStripe2.updateInternals()
        splitedStripe.add(auxStripe2)

        return splitedStripe
    }

    private fun minusTime(momentFin: List<Int>, durationBook: Int): List<Int> {
        var timeIni = LocalTime.of(momentFin[0], momentFin[1])
        timeIni = timeIni.minusMinutes(durationBook.toLong())

        return listOf(timeIni.hour, timeIni.minute)
    }

    private fun sumTime(momentIni: List<Int>, durationBook: Int): List<Int> {
        var timeIni = LocalTime.of(momentIni[0], momentIni[1])
        timeIni = timeIni.plusMinutes(durationBook.toLong())

        return listOf(timeIni.hour, timeIni.minute)

    }

    fun bookAppointment(selectedStripe: Stripe, day: Day, monthNumber: Int) {
        CoroutineScope(Dispatchers.IO).launch {

        }
        //withContext(Dispatchers.Main) {


    }
}