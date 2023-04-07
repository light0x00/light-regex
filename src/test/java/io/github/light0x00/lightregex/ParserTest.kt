package io.github.light0x00.lightregex

import io.github.light0x00.lightregex.lexcical.GeneralLexer
import io.github.light0x00.lightregex.lexcical.StringReader
import io.github.light0x00.lightregex.syntax.*
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
            .parse().also {
                (it.expr as Token).apply {
                    Assertions.assertEquals(LiteralToken("a"), this)
                }
            }
    }

    @Test
    fun testParseUnaryExpr() {
        Parser(GeneralLexer(StringReader("a*")))
            .parse().also {
                it.expr.apply {
                    Assertions.assertTrue(this is UnaryExpr)
                    Assertions.assertEquals("(a*)", this.toString())
                }
            }
    }

    @Test
    fun testOrExpr() {
        Parser(GeneralLexer(StringReader("a|b")))
            .parse().apply {
                Assertions.assertEquals("(a|b)", this.toString())
            }
    }

    @Test
    fun testAndExpr() {
        Parser(GeneralLexer(StringReader("a.b")))
            .parse().apply {
                Assertions.assertEquals(this.toString(), "((a.)b)")
            }
    }

    @Test
    fun testParse1() {
        Parser(GeneralLexer(StringReader("(ab|cd)|(e|f)g")))
            .parse().apply {
                Assertions.assertEquals("((((a(b|c))d)|(e|f))g)", this.toString())
            }
    }

    @Test
    fun testParse2() {
        Parser(GeneralLexer(StringReader("(a|b)*abb")))
            .parse().apply {
                println(this)
                Assertions.assertEquals("(((((a|b)*)a)b)b)", this.toString())
            }
    }

}