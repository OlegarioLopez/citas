package com.ole.citastatto.data

data class Day(
    val moments: List<Moment>,
    val pos: Int,   // Position, could be the first day of the month, the second...
    val weekDay: String
)
