package io.github.light0x00.lightregex

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

/**
 * @author light
 * @since 2023/3/29
 */
class LexerTest {

    @Test
    fun testUnicode() {
        GeneralLexer(StringReader("""\u{1F914}""")).also {
            Assertions.assertIterableEquals(
                it.asSequence().toList().also { it -> println(it) },
                listOf("🤔")
            )
        }
    }

    @Test
    fun testPredict() {
        GeneralLexer(StringReader("""\u{0000}|a"""))
            .also {
                Assertions.assertEquals(it.peek(3), "a")
            }
    }

    @Test
    fun test() {
        GeneralLexer(StringReader("""(abc|ef\\g|\u{1F914})*abc""")).also {
            Assertions.assertEquals(it.peek(4), "ef\\g")
            Assertions.assertIterableEquals(
                it.asSequence().toList(),
                listOf("(", "abc", "|", "ef\\g", "|", "🤔", ")", "*", "abc")
            )
        }
    }

}