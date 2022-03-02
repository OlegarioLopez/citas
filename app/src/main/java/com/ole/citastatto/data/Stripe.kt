package com.ole.citastatto.data

data class Stripe (
    var pos: Int = -1,
    var duration: Int = -1,
    var momentIni: Moment = Moment(),
    var momentFin: Moment = Moment(),
    var initialized: Boolean = false
        )