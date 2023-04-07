package io.github.light0x00.lightregex

import io.github.light0x00.lightregex.lexcical.GeneralLexer
import io.github.light0x00.lightregex.lexcical.StringReader
import io.github.light0x00.lightregex.syntax.LiteralToken
import io.github.light0x00.lightregex.syntax.Token
import io.github.light0x00.lightregex.syntax.TokenType
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
                listOf(LiteralToken("🤔")),
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
                    Token(TokenType.LEFT_PARENTHESIS),
                    LiteralToken("("),
                    LiteralToken("a"),
                    LiteralToken("\\"),
                    LiteralToken("."),
                    LiteralToken("*"),
                    LiteralToken("🤔"),
                    Token(TokenType.LITERAL_ANY),
                    Token(TokenType.STAR),
                    Token(TokenType.RIGHT_PARENTHESIS)
                )
            )
        }
    }

    @Test
    fun testPredict() {
        GeneralLexer(StringReader("""\u{0000}|a"""))
            .also {
                Assertions.assertEquals(LiteralToken("a"), it.lookahead(3))
            }
    }

    @Test
    fun test() {
        GeneralLexer(StringReader("""(a\|b\u{1F914}).*""")).also {
            Assertions.assertIterableEquals(
                listOf(
                    Token(TokenType.LEFT_PARENTHESIS),
                    LiteralToken("a"),
                    LiteralToken("|"),
                    LiteralToken("b"),
                    LiteralToken("🤔"),
                    Token(TokenType.RIGHT_PARENTHESIS),
                    Token(TokenType.LITERAL_ANY),
                    Token(TokenType.STAR)
                ),
                it.asSequence().toList()
            )
        }
    }

}