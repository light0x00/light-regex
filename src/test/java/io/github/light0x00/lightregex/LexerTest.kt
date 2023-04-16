package io.github.light0x00.lightregex

import io.github.light0x00.lightregex.ast.SingleToken
import io.github.light0x00.lightregex.ast.RepeatTimesRangeToken
import io.github.light0x00.lightregex.ast.Token
import io.github.light0x00.lightregex.ast.TokenType
import io.github.light0x00.lightregex.lexcical.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

/**
 * @author light
 * @since 2023/3/29
 */
class LexerTest {

    fun getLexer(source: String): IDynamicLexer {
        return GeneralLexer(StringReader(source))
    }

    @Test
    fun testUnicode() {
        getLexer("""\u{1F914}""").also {
            Assertions.assertIterableEquals(
                listOf(SingleToken("🤔".codePointAt(0))),
                it.asSequence().toList()
            )
        }
    }

    @Test
    fun testTwoCodeUnits() {
        getLexer("""a🤔c""").also {
            Assertions.assertIterableEquals(
                listOf(
                    SingleToken('a'.code),
                    SingleToken("🤔".codePointAt(0)),
                    SingleToken('c'.code),
                ),
                it.asSequence().toList()
            )
        }
    }

    @Test
    fun testEscape() {
        getLexer("""(\(\a\\\.\*\u{1F914}.*)""").also {
            Assertions.assertIterableEquals(
                listOf(
                    Token(TokenType.LEFT_PARENTHESIS),
                    SingleToken('('.code),
                    SingleToken('a'.code),
                    SingleToken('\\'.code),
                    SingleToken('.'.code),
                    SingleToken('*'.code),
                    SingleToken("🤔".codePointAt(0)),
                    Token(TokenType.ANY_LITERAL),
                    Token(TokenType.ANY_TIMES),
                    Token(TokenType.RIGHT_PARENTHESIS)
                ),
                it.asSequence().toList()
            )
        }
    }

    @Test
    fun testPredict() {
        getLexer("""\u{0000}|a""")
            .also {
                Assertions.assertEquals(SingleToken('a'.code), it.lookahead(3))
            }
    }

    @Test
    fun test() {
        getLexer(
            """(a\|b\u{1F914}).*"""
        ).also {
            Assertions.assertIterableEquals(
                listOf(
                    Token(TokenType.LEFT_PARENTHESIS),
                    SingleToken('a'.code),
                    SingleToken('|'.code),
                    SingleToken('b'.code),
                    SingleToken("🤔".codePointAt(0)),
                    Token(TokenType.RIGHT_PARENTHESIS),
                    Token(TokenType.ANY_LITERAL),
                    Token(TokenType.ANY_TIMES)
                ),
                it.asSequence().toList()
            )
        }
    }

    @Test
    fun testSwitch1() {
        getLexer("[(a|b)*-.]")
            .also {
                Assertions.assertIterableEquals(
                    listOf(
                        Token(TokenType.LEFT_SQUARE_BRACKET),
                        Token(TokenType.LEFT_PARENTHESIS),
                        SingleToken('a'.code),
                        Token(TokenType.OR),
                        SingleToken('b'.code),
                        Token(TokenType.RIGHT_PARENTHESIS),
                        Token(TokenType.ANY_TIMES),
                        SingleToken('-'.code),
                        Token(TokenType.ANY_LITERAL),
                        Token(TokenType.RIGHT_SQUARE_BRACKET),
                    ),
                    it.asSequence().toList()
                )
            }
    }

    @Test
    fun testSwitch2() {
        getLexer("[(a|b)*-.]")
            .also {
                it.switchTokenizers(TOKENIZER_SET_FOR_SQUARE_BRACKET_EXPR)
                Assertions.assertIterableEquals(
                    listOf(
                        Token(TokenType.LEFT_SQUARE_BRACKET),
                        SingleToken('('.code),
                        SingleToken('a'.code),
                        SingleToken('|'.code),
                        SingleToken('b'.code),
                        SingleToken(')'.code),
                        SingleToken('*'.code),
                        Token(TokenType.HYPHEN),
                        SingleToken('.'.code),
                        Token(TokenType.RIGHT_SQUARE_BRACKET),
                    ),
                    it.asSequence().toList()
                )
            }
    }

    @Test
    fun testCurlyBracket() {
        getLexer("a{2}b{1,3}c{2,}").also {
            Assertions.assertIterableEquals(
                listOf(
                    SingleToken('a'.code),
                    RepeatTimesRangeToken(2),
                    SingleToken('b'.code),
                    RepeatTimesRangeToken(1, 3),
                    SingleToken('c'.code),
                    RepeatTimesRangeToken(2, infinite = true)
                ),
                it.asSequence().toList()
            )
        }
    }

}