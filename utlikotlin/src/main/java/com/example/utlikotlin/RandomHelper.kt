package com.example.utlikotlin

object RandomHelper {
    private val oneDigitNumbers = (0..9).map { it.toString() }
    private val lowercaseLetters = ('a'..'z').map { it.toString() }
    private val uppercaseLetters = ('A'..'Z').map { it.toString() }

    val numbersAndLetters = oneDigitNumbers + lowercaseLetters + uppercaseLetters
    val numbersAndLowerLetters = oneDigitNumbers + lowercaseLetters
    val numbersAndUpperLetters = oneDigitNumbers + uppercaseLetters
    val numbers = oneDigitNumbers
    val lowerLetters = lowercaseLetters
    val upperLetters = uppercaseLetters
    val letters = lowercaseLetters + uppercaseLetters

    fun generateRandomString(length: Int) = buildString {
        repeat(length) {
            append(numbersAndLetters.random())
        }
    }
}