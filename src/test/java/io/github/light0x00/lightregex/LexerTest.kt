package io.github.light0x00.lightregex

import io.github.light0x00.lightregex.ast.SingleToken
import io.github.light0x00.lightregex.ast.RepeatTimesRangeToken
import io.github.light0x00.lightregex.ast.MetaToken
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
                    MetaToken(TokenType.LEFT_PARENTHESIS),
                    SingleToken('('.code),
                    SingleToken('a'.code),
                    SingleToken('\\'.code),
                    SingleToken('.'.code),
                    SingleToken('*'.code),
                    SingleToken("🤔".codePointAt(0)),
                    MetaToken(TokenType.ANY_LITERAL),
                    MetaToken(TokenType.ANY_TIMES),
                    MetaToken(TokenType.RIGHT_PARENTHESIS)
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
                    MetaToken(TokenType.LEFT_PARENTHESIS),
                    SingleToken('a'.code),
                    SingleToken('|'.code),
                    SingleToken('b'.code),
                    SingleToken("🤔".codePointAt(0)),
                    MetaToken(TokenType.RIGHT_PARENTHESIS),
                    MetaToken(TokenType.ANY_LITERAL),
                    MetaToken(TokenType.ANY_TIMES)
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
                        MetaToken(TokenType.LEFT_SQUARE_BRACKET),
                        MetaToken(TokenType.LEFT_PARENTHESIS),
                        SingleToken('a'.code),
                        MetaToken(TokenType.OR),
                        SingleToken('b'.code),
                        MetaToken(TokenType.RIGHT_PARENTHESIS),
                        MetaToken(TokenType.ANY_TIMES),
                        SingleToken('-'.code),
                        MetaToken(TokenType.ANY_LITERAL),
                        MetaToken(TokenType.RIGHT_SQUARE_BRACKET),
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
                        MetaToken(TokenType.LEFT_SQUARE_BRACKET),
                        SingleToken('('.code),
                        SingleToken('a'.code),
                        SingleToken('|'.code),
                        SingleToken('b'.code),
                        SingleToken(')'.code),
                        SingleToken('*'.code),
                        MetaToken(TokenType.HYPHEN),
                        SingleToken('.'.code),
                        MetaToken(TokenType.RIGHT_SQUARE_BRACKET),
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