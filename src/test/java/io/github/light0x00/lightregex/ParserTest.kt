package io.github.light0x00.lightregex

import io.github.light0x00.lightregex.ast.SingleToken
import io.github.light0x00.lightregex.ast.RegExpr
import io.github.light0x00.lightregex.ast.Token
import io.github.light0x00.lightregex.ast.UnaryExpr
import io.github.light0x00.lightregex.common.astToPlantUML
import io.github.light0x00.lightregex.common.traversePostOrder
import io.github.light0x00.lightregex.lexcical.GeneralLexer
import io.github.light0x00.lightregex.lexcical.StringReader
import io.github.light0x00.lightregex.syntax.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

fun parseAsAST(pattern: String): RegExpr {
    return Parser(GeneralLexer(StringReader(pattern)))
        .parse()
}

/**
 * @author light
 * @since 2023/3/30
 */
class ParserTest {

    @Test
    fun testParsePrimaryExpr() {
        parseAsAST("a")
            .also {
                (it.expr as Token).apply {
                    Assertions.assertEquals(SingleToken('a'.code), this)
                }
            }
    }

    @Test
    fun testParseUnaryExpr() {
        parseAsAST("a*")
            .also {
                it.expr.apply {
                    Assertions.assertTrue(this is UnaryExpr)
                    Assertions.assertEquals("(a*)", this.toString())
                }
            }
    }

    @Test
    fun testOrExpr() {
        parseAsAST("a|b")
            .apply {
                Assertions.assertEquals("(a|b)", this.toString())
            }
    }

    @Test
    fun testAndExpr() {
        parseAsAST("a.b")
            .apply {
                Assertions.assertEquals(this.toString(), "((a.)b)")
            }
    }

    @Test
    fun testParse1() {
        parseAsAST("(ab|cd)|(e|f)g")
            .apply {
                Assertions.assertEquals("((((a(b|c))d)|(e|f))g)", this.toString())
            }
    }

    @Test
    fun testParse2() {
        parseAsAST("(a|b)*abb")
            .apply {
                println(this)
                Assertions.assertEquals("(((((a|b)*)a)b)b)", this.toString())
            }
    }

    @Test
    fun testParseSquareBracket() {
        parseAsAST("[a-.*]")
            .apply {
                println(this)
                println(astToPlantUML(this))
                Assertions.assertEquals("(a-.|*)", this.toString())
            }
    }

    @Test
    fun testParseCurlyBracket1() {
        parseAsAST("a{1,2}bc")
            .apply {
                println(this)
                println(astToPlantUML(this))
                Assertions.assertEquals("(((a(a?))b)c)", this.toString())
            }
    }

    @Test
    fun testParseCurlyBracket2() {
        parseAsAST("(a|b){1,2}c")
            .apply {
                println(this)
                println(astToPlantUML(this))
                Assertions.assertEquals("(((a|b)((a|b)?))c)", this.toString())
            }
    }

    @Test
    fun testParseCurlyBracket3() {
        parseAsAST("(a{1,2}){1,2}")
            .apply {

                traversePostOrder(this) {
                    println(it)
                }
                println(this)
                println(astToPlantUML(this))
//                Assertions.assertEquals("(((a|b)((a|b)?))c)", this.toString())
            }
    }


    @Test
    fun testParseOptional() {
        parseAsAST("ab?c")
            .apply {
                println(this)
                println(astToPlantUML(this))
                Assertions.assertEquals("((a(b?))c)", this.toString())
            }
    }

    @Test
    fun testParseAtLeastOnce() {

        parseAsAST("ab+c")
            .apply {
                println(this)
                println(astToPlantUML(this))
                Assertions.assertEquals("((a(b+))c)", this.toString())
            }
    }

}