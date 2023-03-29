@file:JvmName("Toolkit")

package io.github.light0x00.lightregex

import java.util.*

fun assertTrue(condition: Boolean, msg: String = "Assertion failed") {
    if (!condition) {
        throw LightRegexException(msg)
    }
}

fun isLetter(chr: Char): Boolean {
    return isLetterSmallCase(chr) || isLetterCapitalCase(chr)
}

fun isLetterCapitalCase(chr: Char): Boolean {
    return chr in '\u0041'..'\u005A'
}

fun isLetterSmallCase(chr: Char): Boolean {
    return chr in '\u0061'..'\u007A'
}

fun isDigit(chr: Char): Boolean {
    return chr in '\u0030'..'\u0039'
}

fun unicodeHexToString(unicode: String): String {
    return String(Character.toChars(Integer.parseInt(unicode, 16)))
}