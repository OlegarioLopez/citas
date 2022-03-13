package com.ole.citastatto.data

import java.time.LocalTime

data class Day(
    val weekDay: String = "",
    var dayInMonth: Int = 1,
    val startMorning: List<Int> = listOf(0,0),
    val finishMorning: List<Int> = listOf(0,0),
    val startEvening: List<Int> = listOf(0,0),
    val finishtEvening: List<Int> = listOf(0,0),
    var stripes: MutableList<Stripe> = mutableListOf(),
    val month: String=""

){
    private var startMorningTime: LocalTime? = LocalTime.of(0,0)
    private var finishMorningTime: LocalTime? = LocalTime.of(0,0)
    private var startEveningTime: LocalTime? = LocalTime.of(0,0)
    private var finishtEveningTime: LocalTime? = LocalTime.of(0,0)

   init{
       startMorningTime=LocalTime.of(startMorning[0],startMorning[1])
       finishMorningTime=LocalTime.of(finishMorning[0],finishMorning[1])
       startEveningTime=LocalTime.of(startEvening[0],startEvening[1])
       finishtEveningTime=LocalTime.of(finishtEvening[0],finishtEvening[1])


   }

}