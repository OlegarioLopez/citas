package com.ole.citastatto.data

data class Stripe (
    var pos: Int,
    var duration: Int,
    val momentIni: Moment,
    val momentFin: Moment
        )