package com.example.utlikotlin

import kotlin.math.pow
import kotlin.math.round

fun Double.roundDecimal(digit: Int): Double {
    val factor = 10.0.pow(digit)

    return round(this * factor) / factor
}

fun Double.showDecimal(digit: Int) = String.format("%.${digit}f", this)