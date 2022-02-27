package com.ole.citastatto.data

data class Month(
    val daysNumber:Int =-1,
    val name: String = "",
    val pos: Int = -1,   // Position, could be the first Month of the year, the second...
    val firstDay: Int = -1 // 1 means the month begin in a monday, 2 in a tuesday...
)
{

    val days: MutableList<Day> = mutableListOf()

    init {
        var weekDay=firstDay
    for (i in 1..daysNumber){
        days.add(Day(weekDay))
        weekDay++
        if(weekDay==8){
            weekDay=1
        }
    }
    }
}
