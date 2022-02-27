package com.ole.citastatto.data

data class Stripe (
    var pos: Int = -1,
    var duration: Int = -1,
    val momentIni: Moment = Moment(),
    val momentFin: Moment = Moment()
        )