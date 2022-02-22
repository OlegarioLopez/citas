package com.ole.citastatto.data

// A moment represent a period of 15 minutes, there is a different amount of moments depending on the working hours
data class Moment(
    val pos: Int, // Position, could be the first Moment of the day, the second...
    val timeIni: Time,
    val timeFin: Time
)
