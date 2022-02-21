package com.ole.citastatto.data


data class Appointment(val TimeIni: Time, val TimeFin: Time) {

    var duration: Int = durationCalc()

    private fun durationCalc(): Int {
        var duration:Int
        if (TimeFin.min >= TimeIni.min) {
            duration = (TimeFin.hour - TimeIni.hour) * 60
            duration += TimeFin.min - TimeIni.min
        } else {
            duration = ((TimeFin.hour - TimeIni.hour)-1) * 60
            duration += TimeFin.min + (60 - TimeIni.min)
        }
        return duration
    }
}
