package com.ole.citastatto.data

data class Month(
    val days: List<Day>,
    val name: String,
    val pos: Int    // Position, could be the first Month of the year, the second...
)
