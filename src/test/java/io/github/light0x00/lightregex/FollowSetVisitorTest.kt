package io.github.light0x00.lightregex

import io.github.light0x00.lightregex.ast.AST
import io.github.light0x00.lightregex.ast.LiteralToken
import io.github.light0x00.lightregex.ast.OrExpr
import io.github.light0x00.lightregex.visitor.FirstSetVisitor
import io.github.light0x00.lightregex.visitor.FollowSetVisitor
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.containsInAnyOrder
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

/**
 * @author light
 * @since 2023/4/6
 */
class FollowSetVisitorTest {


    @Test
    fun testForCurlyBracketExpr0() {
        val ast = parseAsAST("a{2,4}bc")
        determineFollowSet(ast)
        println(astToPlantUML(ast))
    }

    @Test
    fun testForCurlyBracketExpr1() {
        val ast = parseAsAST("(a|b){2,4}c")
        determineFollowSet(ast)
        println(astToPlantUML(ast))
    }

    @Test
    fun testForCurlyBracketExpr2() {
        val ast = parseAsAST("(ab){2,4}c")
        determineFollowSet(ast)
        println(astToPlantUML(ast))
    }

    @Test
    fun test5() {
        val ast = parseAsAST("(a{2,4}b{0,2}){2,4}c")
        determineFollowSet(ast)
        println(astToPlantUML(ast))
    }

    @Test
    fun test() {
        val ast = parseAsAST("(a|b)*c")
        determineFollowSet(ast)

        println(astToPlantUML(ast))

        (ast.expr.children[0].children[0] as OrExpr)
            .apply {
                assertThat(
                    (this.left as LiteralToken).followSet.map(Transition::toString),
                    containsInAnyOrder(
                        "a→1", "b→2", "c→3"
                    )
                )
            }
    }

    private fun determineFollowSet(ast: AST) {
        //First Set
        val firstSetVisitor = FirstSetVisitor()
        traversePostOrder(ast) { node ->
            firstSetVisitor.visit(node)
        }
        //Follow Set
        val followSetVisitor = FollowSetVisitor()
        traversePreOrder(ast) { node ->
            followSetVisitor.visit(node)
        }
    }

}