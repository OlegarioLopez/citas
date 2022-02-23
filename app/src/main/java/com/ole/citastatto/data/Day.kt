package com.ole.citastatto.data

// A moment represent a period of 15 minutes, there is a different amount of moments depending on the working hours
data class Day(
    val weekDay: Int // 1 is monday, 2 tuesday...
) {
    val workMorning = listOf(10, 11, 12, 13) //TODO extraer el horario laboral a otra clase que se inyecte para no hardcodearlo
    val workEvening = listOf(17, 18, 19)   //TODO extraer el horario laboral a otra clase que se inyecte para no hardcodearlo
    private var momentPos: Int = 1
    var stripes: MutableList<Stripe> = mutableListOf() // TODO revisar la inicialización del parámetro duration de la Stripe
    val moments: MutableList<Moment> = fillMoments()

    init {
       if(this.weekDay!=6 && this.weekDay!=7){
            stripes =  mutableListOf(Stripe(1,duration= moments.size*15,moments.first(),moments.last()))
        }
    }

    private fun fillMoments(): MutableList<Moment> {
        val moments: MutableList<Moment> = mutableListOf()
        if(this.weekDay == 6 || this.weekDay==7)  return mutableListOf()
        for (i in workMorning) {
            for (e in 0..30 step 15) {
                moments.add(Moment(momentPos, Time(i, e), Time(i, e + 15)))
                momentPos++
            }
            moments.add(Moment(momentPos, Time(i, 45), Time(i + 1, 0)))
            momentPos++
        }

        for (i in workEvening) {
            for (e in 0..30 step 15) {
                moments.add(Moment(momentPos, Time(i, e), Time(i, e + 15)))
                momentPos++
            }
            moments.add(Moment(momentPos, Time(i, 45), Time(i + 1, 0)))
            momentPos++
        }
        return moments
    }



}