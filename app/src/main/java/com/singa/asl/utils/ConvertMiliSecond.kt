package com.singa.asl.utils

fun timeStringToMillis(time: String): Long {
    val parts = time.split(":", ".")
    if (parts.size != 4) {
        throw IllegalArgumentException("Time format must be hh:mm:ss.SSS")
    }

    val hours = parts[0].toLong()
    val minutes = parts[1].toLong()
    val seconds = parts[2].toLong()
    val millis = parts[3].toLong()

    return (hours * 3600 + minutes * 60 + seconds) * 1000 + millis
}

fun timeToMillis(time: String): Long {
    val parts = time.split(":")
    val hours = parts[0].toLong()
    val minutes = parts[1].toLong()
    val seconds = parts[2].toLong()

    val millis = (hours * 3600000) + (minutes * 60000) + (seconds * 1000)
    return millis
}
