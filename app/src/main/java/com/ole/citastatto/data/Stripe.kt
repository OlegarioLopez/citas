package com.ole.citastatto.data

import java.time.Duration
import java.time.LocalTime

data class Stripe (
    val month: String = "",
    val dayInMonth: Int = 0,
    var availability: Boolean = true,
    var bookedBy: String = "",
    var momentIni: List<Int> = listOf(0,0),
    var momentFin: List<Int> = listOf(0,0),
    var duration: Long = 0

){

    override fun toString(): String {
        return "De $momentIniTime a $momentFinTime"
    }

     var momentIniTime: LocalTime? = LocalTime.of(0,0)
     var momentFinTime: LocalTime? = LocalTime.of(0,0)
    init {
        momentIniTime = LocalTime.of(momentIni[0],momentIni[1])
        momentFinTime = LocalTime.of(momentFin[0],momentFin[1])
        duration = Duration.between(momentIniTime,momentFinTime).toMinutes()
    }

    fun updateInternals(){
        momentIniTime = LocalTime.of(momentIni[0],momentIni[1])
        momentFinTime = LocalTime.of(momentFin[0],momentFin[1])
        duration = Duration.between(momentIniTime,momentFinTime).toMinutes()
    }
}