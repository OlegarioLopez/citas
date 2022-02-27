package com.ole.citastatto.data

// A moment represent a period of 15 minutes, there is a different amount of moments depending on the working hours
data class Moment(
    val pos: Int = -1, // Position, could be the first Moment of the day, the second...
    var timeIni: Time = Time(-1, -1),
    var timeFin: Time = Time(-1, -1),

    val available: Boolean = true
) {
    init {
        //when primary constructor is used with timeIni, timeFin is filled
        if (timeIni.hour != -1) {
            fillTimeFin()
        }
        //when primary constructor is used with timeFin, timeIni is filled
        if (timeFin.hour != -1) {
            fillTimeIni()
        }
    }

    private fun fillTimeFin() {
        if (timeIni.min == 45) {
            timeFin = Time(timeIni.hour + 1, 0)
        } else timeFin = Time(timeIni.hour, timeIni.min + 15)


    }

    private fun fillTimeIni() {
        if (timeFin.min == 0) {
            timeIni = Time(timeFin.hour - 1, 45)
        } else timeIni = Time(timeFin.hour, timeFin.min - 15)
    }
}
