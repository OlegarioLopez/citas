package com.ole.citastatto.data

import com.ole.citastatto.data.Day

data class Month(
    var monthName: String = "",
    var days: MutableList<Day> = mutableListOf()
)