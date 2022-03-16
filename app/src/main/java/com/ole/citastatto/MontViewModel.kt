package com.ole.citastatto

import android.util.Log
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
import java.time.LocalDate
import java.time.LocalTime

//TODO: Revisar si es mejor hacer el querySnapshot una sola vez para toda la clase y cómo hacerlo
class MontViewModel : ViewModel() {

    private val _month = mutableStateOf(Month())
    var month: State<Month> = _month

    private val _someStripe = mutableStateOf(true)
    var someStripe = _someStripe

    private val _daysAvailables = mutableStateOf<List<Day>>(mutableListOf())
    var daysAvailables: State<List<Day>> = _daysAvailables

    private val _stripesAvailables = mutableStateOf<List<Stripe>>(mutableListOf())
    var stripesAvailables: State<List<Stripe>> = _stripesAvailables

    private val monthCollectionRef = Firebase.firestore.collection("Months")
    private val stripesCollectionOrdered = Firebase.firestore.collection("Stripes").orderBy("dayInMonth").orderBy("momentIni")
    private val stripesCollectionRef = Firebase.firestore.collection("Stripes")

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
            val auxStripesAvailables: MutableList<Stripe> = mutableListOf()
            val querySnapshot = stripesCollectionOrdered.whereEqualTo("month", "APRIL").get().await()
            val finalStripesList: MutableList<Stripe> = mutableListOf()
            val dayStripeLimit = 5
            var currentNumberStripes = 0
            var currentDay = 0


            for (document in querySnapshot.documents) {

                val stripe = document.toObject<Stripe>()
                stripe?.let {
                    if (stripe.availability) auxStripesAvailables.add(it)
                }
            }
            auxStripesAvailables.sortBy { "momentIni[0]" }
            auxStripesAvailables.sortBy { "momentIni[1]" }

            for (stripe in auxStripesAvailables) {
                stripe.updateInternals()
                if (stripe.duration < durationBook) continue

                if (currentDay != stripe.dayInMonth) {
                    auxDaysWithStripe.add(
                        Day(
                            dayInMonth = stripe.dayInMonth,
                            month = stripe.month,
                            weekDay = LocalDate.of(
                                month.value.year,
                                month.value.monthNumber,
                                stripe.dayInMonth
                            ).dayOfWeek.toString()
                        )
                    )
                    currentDay = stripe.dayInMonth
                    currentNumberStripes = 1
                }
                if (currentNumberStripes < dayStripeLimit) {

                    finalStripesList.addAll(splitStripe(stripe, durationBook))


                    currentNumberStripes++ //TODO modoficar con un if( splitstripe().size == 2) currentNumber+=2 else currentNumber++ en caso de que así se quiera

                }
            }


            withContext(Dispatchers.Main) {

                if (finalStripesList.isEmpty()) _someStripe.value = false
                _daysAvailables.value = auxDaysWithStripe
                _stripesAvailables.value = finalStripesList
            }
        }
    }

    fun splitStripe(Stripe: Stripe, durationBook: Int): MutableList<Stripe> {
        Stripe.updateInternals() //TODO BORRAR
        val splitedStripe = mutableListOf<Stripe>()

        if (durationBook == Stripe.duration.toInt()) {
            Stripe.availability = false
            Stripe.splitedFrom = "${Stripe.dayInMonth}${Stripe.momentIni.get(0)}${Stripe.momentIni.get(1)}".toInt()
            splitedStripe.add(Stripe)
            return splitedStripe
        }

        val auxStripe = Stripe.copy( )
        val auxStripe2 = Stripe.copy()
        auxStripe.momentFin = sumTime(
            Stripe.momentIni,
            durationBook
        )
        auxStripe.availability = false
        auxStripe.bookedBy = "usuario de prueba"
        auxStripe.updateInternals()
        auxStripe.splitedFrom = "${Stripe.dayInMonth}${Stripe.momentIni.get(0)}${Stripe.momentIni.get(1)}".toInt()

        splitedStripe.add(auxStripe)
        auxStripe2.momentIni = minusTime(Stripe.momentFin, durationBook)
        auxStripe2.availability = false
        auxStripe2.bookedBy = "usuario de prueba"
        auxStripe2.splitedFrom = "${Stripe.dayInMonth}${Stripe.momentIni.get(0)}${Stripe.momentIni.get(1)}".toInt()

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
            var stripeFatherDocuments = stripesCollectionOrdered.whereEqualTo( "month","${selectedStripe.month}" ).whereEqualTo("key",selectedStripe.splitedFrom).get().await()
            for(stripeFatherDoc in stripeFatherDocuments){
                var dbstripeFatherDoc = Firebase.firestore.collection("Stripes").document(stripeFatherDoc.id)
                try {
                    var stripeFather = stripeFatherDoc.toObject<Stripe>()

                    if(selectedStripe.duration == stripeFather.duration){
                        selectedStripe.availability = false
                        selectedStripe.bookedBy = "Usuario de prueba1"
                        dbstripeFatherDoc.delete()
                        stripesCollectionRef.add(selectedStripe)
                    }else{
                        selectedStripe.availability = false
                        selectedStripe.bookedBy = "Usuario de prueba1"

                        dbstripeFatherDoc.delete()
                        Firebase.firestore.collection("Stripes").add(selectedStripe)
                        if(stripeFather.momentIni == selectedStripe.momentIni){

                            stripeFather.momentIni = sumTime(stripeFather.momentIni,selectedStripe.duration.toInt())
                            stripeFather.updateInternals()
                            stripesCollectionRef.add(stripeFather)
                        }
                        if(stripeFather.momentFin == selectedStripe.momentFin){

                                stripeFather.momentFin = minusTime(stripeFather.momentFin,selectedStripe.duration.toInt())
                                stripeFather.updateInternals()
                                stripesCollectionRef.add(stripeFather)

                    }
                    }
                }catch (e: Exception){
                Log.d("bookappointment","Error en el bookappointment")
                }

            }
        }
        //withContext(Dispatchers.Main) {


    }

   /* fun sayHello (){
        Log.d("Hello" , " Hello")
    }*/
}