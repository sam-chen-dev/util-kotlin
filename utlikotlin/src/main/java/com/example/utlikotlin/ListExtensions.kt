package com.example.utlikotlin

fun <T> List<T>.range(fromIndex: Int, toIndex: Int) = this.subList(fromIndex, toIndex + 1)

fun <T> List<T>.getRandom(quantity: Int) = shuffled().take(quantity)

fun <T> List<T>.replace(targetItem: T, newItem: T) = map {
    if (it == targetItem) {
        newItem
    } else {
        it
    }
}