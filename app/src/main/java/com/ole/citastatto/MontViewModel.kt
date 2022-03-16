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
import com.ole.citastatto.data.Spot
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

    private val _someSpot = mutableStateOf(true)
    var someSpot = _someSpot

    private val _daysAvailables = mutableStateOf<List<Day>>(mutableListOf())
    var daysAvailables: State<List<Day>> = _daysAvailables

    private val _spotsAvailables = mutableStateOf<List<Spot>>(mutableListOf())
    var spotsAvailables: State<List<Spot>> = _spotsAvailables

    private val monthCollectionRef = Firebase.firestore.collection("Months")
    private val spotsCollectionOrdered = Firebase.firestore.collection("Spots").orderBy("dayInMonth").orderBy("momentIni")
    private val spotsCollectionRef = Firebase.firestore.collection("Spots")

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

    fun retrieveAvailableSpots(durationBook: Int) {

            viewModelScope.launch(Dispatchers.IO) {

            val auxDaysWithSpot: MutableList<Day> = mutableListOf()
            val auxSpotsAvailables: MutableList<Spot> = mutableListOf()
            val querySnapshot = spotsCollectionOrdered.whereEqualTo("month", "APRIL").get().await()
            val finalSpotsList: MutableList<Spot> = mutableListOf()
            val daySpotLimit = 5
            var currentNumberSpots = 0
            var currentDay = 0


            for (document in querySnapshot.documents) {

                val spot = document.toObject<Spot>()
                spot?.let {
                    if (spot.availability) auxSpotsAvailables.add(it)
                }
            }
            auxSpotsAvailables.sortBy { "momentIni[0]" }
            auxSpotsAvailables.sortBy { "momentIni[1]" }

            for (spot in auxSpotsAvailables) {
                spot.updateInternals()
                if (spot.duration < durationBook) continue

                if (currentDay != spot.dayInMonth) {
                    auxDaysWithSpot.add(
                        Day(
                            dayInMonth = spot.dayInMonth,
                            month = spot.month,
                            weekDay = LocalDate.of(
                                month.value.year,
                                month.value.monthNumber,
                                spot.dayInMonth
                            ).dayOfWeek.toString()
                        )
                    )
                    currentDay = spot.dayInMonth
                    currentNumberSpots = 1
                }
                if (currentNumberSpots < daySpotLimit) {

                    finalSpotsList.addAll(splitSpot(spot, durationBook))


                    currentNumberSpots++ //TODO modoficar con un if( splitSpot().size == 2) currentNumber+=2 else currentNumber++ en caso de que así se quiera

                }
            }


            withContext(Dispatchers.Main) {

                if (finalSpotsList.isEmpty()) _someSpot.value = false
                _daysAvailables.value = auxDaysWithSpot
                _spotsAvailables.value = finalSpotsList
            }
        }
    }

    fun splitSpot(Spot: Spot, durationBook: Int): MutableList<Spot> {
        Spot.updateInternals() //TODO BORRAR
        val splitedSpot = mutableListOf<Spot>()

        if (durationBook == Spot.duration.toInt()) {
            Spot.availability = false
            Spot.splitedFrom = "${Spot.dayInMonth}${Spot.momentIni.get(0)}${Spot.momentIni.get(1)}".toInt()
            splitedSpot.add(Spot)
            return splitedSpot
        }

        val auxSpot = Spot.copy( )
        val auxSpot2 = Spot.copy()
        auxSpot.momentFin = sumTime(
            Spot.momentIni,
            durationBook
        )
        auxSpot.availability = false
        auxSpot.bookedBy = "usuario de prueba"
        auxSpot.updateInternals()
        auxSpot.splitedFrom = "${Spot.dayInMonth}${Spot.momentIni.get(0)}${Spot.momentIni.get(1)}".toInt()

        splitedSpot.add(auxSpot)
        auxSpot2.momentIni = minusTime(Spot.momentFin, durationBook)
        auxSpot2.availability = false
        auxSpot2.bookedBy = "usuario de prueba"
        auxSpot2.splitedFrom = "${Spot.dayInMonth}${Spot.momentIni.get(0)}${Spot.momentIni.get(1)}".toInt()

        auxSpot2.updateInternals()
        splitedSpot.add(auxSpot2)

        return splitedSpot
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

    fun bookAppointment(selectedSpot: Spot, day: Day, monthNumber: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            var spotFatherDocuments = spotsCollectionOrdered.whereEqualTo( "month","${selectedSpot.month}" ).whereEqualTo("key",selectedSpot.splitedFrom).get().await()
            for(spotFatherDoc in spotFatherDocuments){
                var dbSpotFatherDoc = Firebase.firestore.collection("Spots").document(spotFatherDoc.id)
                try {
                    var spotFather = spotFatherDoc.toObject<Spot>()

                    if(selectedSpot.duration == spotFather.duration){
                        selectedSpot.availability = false
                        selectedSpot.bookedBy = "Usuario de prueba1"
                        dbSpotFatherDoc.delete()
                        spotsCollectionRef.add(selectedSpot)
                    }else{
                        selectedSpot.availability = false
                        selectedSpot.bookedBy = "Usuario de prueba1"

                        dbSpotFatherDoc.delete()
                        Firebase.firestore.collection("Spots").add(selectedSpot)
                        if(spotFather.momentIni == selectedSpot.momentIni){

                            spotFather.momentIni = sumTime(spotFather.momentIni,selectedSpot.duration.toInt())
                            spotFather.updateInternals()
                            spotsCollectionRef.add(spotFather)
                        }
                        if(spotFather.momentFin == selectedSpot.momentFin){

                                spotFather.momentFin = minusTime(spotFather.momentFin,selectedSpot.duration.toInt())
                                spotFather.updateInternals()
                                spotsCollectionRef.add(spotFather)

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