package io.github.light0x00.lightregex

import io.github.light0x00.lightregex.TokenType.LITERAL
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

/**
 * @author light
 * @since 2023/3/30
 */
class ParserTest {

    @Test
    fun testParsePrimaryExpr() {
        Parser(GeneralLexer(StringReader("a")))
            .parse().apply {
                Assertions.assertTrue(this is PrimaryExpr)
                (this as PrimaryExpr).apply {
                    Assertions.assertEquals(token, Token(LITERAL, "a"))
                }
            }
    }

    @Test
    fun testParseUnaryExpr() {
        Parser(GeneralLexer(StringReader("a*")))
            .parse().apply {
                Assertions.assertTrue(this is UnaryExpr)
                Assertions.assertEquals(this.toString(),"(a*)")
            }
    }

    @Test
    fun testOrExpr() {
        Parser(GeneralLexer(StringReader("a|b")))
            .parse().apply {
                Assertions.assertEquals(this.toString(),"(a|b)")
            }
    }

    @Test
    fun testAndExpr() {
        Parser(GeneralLexer(StringReader("a.b")))
            .parse().apply {
                Assertions.assertEquals(this.toString(),"((a.)b)")
            }
    }

    @Test
    fun testParse1() {
        Parser(GeneralLexer(StringReader("(ab|cd)|(e|f)g")))
            .parse().apply {
                Assertions.assertEquals(this.toString(),"((((a(b|c))d)|(e|f))g)")
            }
    }

    @Test
    fun testParse2() {
        Parser(GeneralLexer(StringReader("(a|b)*abb")))
            .parse().apply {
                println(this)
                Assertions.assertEquals(this.toString(), "(((((a|b)*)a)b)b)")
            }
    }

}