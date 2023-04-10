package io.github.light0x00.lightregex

import org.junit.jupiter.api.Test
import java.util.stream.Stream
import javax.xml.stream.events.Characters

/**
 * @author light
 * @since 2023/4/7
 */
class DFATest {
    @Test
    fun test() {
        
    }

}

fun main() {
    val s = "和123🤔456"
    println(s.codePointAt(0))
    s.codePointAt(4)

    println(s.codePoints())

    println(Integer.parseInt("1F914", 16))
    println(String(Character.toChars(Integer.parseInt("1F914", 16))).codePointAt(0))
//
//    println(Character.toString(s.codePointAt(5)))
//    println(s.codePointAt(4))
    println(Character.toString(s.codePointAt(3)) == "🤔")
//    println(Character.toString(s.codePointAt(4)) == "\uD83E\uDD14")
//    println(string)
}
