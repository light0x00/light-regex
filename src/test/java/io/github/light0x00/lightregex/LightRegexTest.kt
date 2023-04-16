package io.github.light0x00.lightregex

import org.junit.jupiter.api.Test
import java.util.stream.Stream
import javax.xml.stream.events.Characters

/**
 * @author light
 * @since 2023/4/14
 */
class LightRegexTest {

    @Test
    fun test() {
        val regex = LightRegex("(a|b)*abb")
        val result = regex.match("ababbabbabb", true)
        println(result)
    }

    @Test
    fun test2() {
        val regex = LightRegex("(\\w\\d\\s){3}")
//        val result = regex.match("a12A3",0, eager = true)
        val result = regex.match("a1 b1 c1 ")

        println(result)
    }

    @Test
    fun testEmoji() {
        //U+1F600..U+1F64F
        val regex = LightRegex("[\\u{1F600}-\\u{1F644}]")
        val str = "happy😄 anger😡 sorrow😞 joy😇";
        val matches = regex.matchAll(str)
        for (m in matches) {
            println(str.substring(m.start, m.endInclusive + 1))
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    @Test
    fun testRangeMatch() {
        //U+1F600..U+1F64F
//        val str = "LOL😄,sounds good👀,to plant a sakura tree🌸"

        val regex = LightRegex("[a-zA-Z0-9\\s]")
        val str = "happy😄 anger😡 sorrow😞 joy😇"
        val matches = regex.matchAll(str)

        for (range in matches) {
            print(str.substring(range.first, range.endInclusive + 1))
        }
    }

}

fun main2() {

    val input = "abbabb"

    val regex = LightRegex("(a|b)+abb")
    val matches = regex.matchAll(input)

    println(if (matches.isNotEmpty()) "Match Found" else "Match Not Found")

    for (match in matches) {
        println(input.substring(match.first, match.last + 1) + ",at index ${match.first}~${match.last}")
    }
}

fun getter() = sequence {
    for (i in Stream.of(1, 3, 5, 7)) {
        yield(i)
    }
}