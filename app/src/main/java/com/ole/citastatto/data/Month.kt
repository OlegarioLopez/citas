package com.ole.citastatto.data

data class Month(
    var monthNumber: Int=0,
    var year: Int = 0,
    var monthName: String = "",
    var days: MutableList<Day> = mutableListOf()
)