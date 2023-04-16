package io.github.light0x00.lightregex

import java.util.stream.Stream
import kotlin.streams.toList


fun main() {
    val str = "happy 😄 anger 😡 sorrow 😞 joy😇";
    var idx = 0
    while (idx < str.length) {
        val codePoint = str.codePointAt(idx)
        println(Character.toString(codePoint))
        if (codePoint > 0xFFFF) {
            idx += 2
        } else {
            idx++
        }
    }
}

fun getter2() = sequence {
    for (i in Stream.of(1, 3, 5, 7)) {
        yield(i)
    }
}