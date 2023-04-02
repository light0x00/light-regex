package io.github.light0x00.lightregex

import io.github.light0x00.lightregex.TokenType.LITERAL
import io.github.light0x00.lightregex.TokenType.SPECIAL
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
                listOf(Token(LITERAL, "🤔")),
                it.asSequence().toList()
            )
        }
    }

    @Test
    fun testEscape() {
        GeneralLexer(StringReader("""(\(\a\\\.\*\u{1F914}.*)""")).also {
            Assertions.assertIterableEquals(
                it.asSequence().toList(),
                listOf(
                    Token(SPECIAL, "("),
                    Token(LITERAL, "("),
                    Token(LITERAL, "a"),
                    Token(LITERAL, "\\"),
                    Token(LITERAL, "."),
                    Token(LITERAL, "*"),
                    Token(LITERAL, "🤔"),
                    Token(SPECIAL, "."),
                    Token(SPECIAL, "*"),
                    Token(SPECIAL, ")")
                )
            )
        }
    }

    @Test
    fun testPredict() {
        GeneralLexer(StringReader("""\u{0000}|a"""))
            .also {
                Assertions.assertEquals(Token(LITERAL, "a"), it.lookahead(3))
            }
    }

    @Test
    fun test() {
        GeneralLexer(StringReader("""(a\|b\u{1F914}).*""")).also {
            Assertions.assertIterableEquals(
                listOf(
                    Token(SPECIAL, "("),
                    Token(LITERAL, "a"),
                    Token(LITERAL, "|"),
                    Token(LITERAL, "b"),
                    Token(LITERAL, "🤔"),
                    Token(SPECIAL, ")"),
                    Token(SPECIAL, "."),
                    Token(SPECIAL, "*")
                ),
                it.asSequence().toList()
            )
        }
    }

}