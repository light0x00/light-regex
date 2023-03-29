package io.github.light0x00.lightregex

/**
 * @author light
 * @since 2023/3/29
 */
interface IPredictableLexer : Iterator<String> {

    fun peek(): String

    fun peek(i: Int): String

}