package io.github.light0x00.lightregex

import io.github.light0x00.lightregex.visitor.FirstSetVisitor
import io.github.light0x00.lightregex.visitor.FollowSetVisitor
import io.github.light0x00.lightregex.visitor.NFAGenerator
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers
import org.junit.jupiter.api.Test

/**
 * @author light
 * @since 2023/4/7
 */
class NFATest {


    @Test
    fun test2() {
        val nfa = getNFA("a{2,4}bc")
        val plantUMLSource = nfaToPlantUML(nfa)
        println(plantUMLSource)
    }

    @Test
    fun test() {
        val nfa = getNFA("(a|b)*ab")
        val plantUMLSource = nfaToPlantUML(nfa)
        assertThat(
            "", plantUMLSource, Matchers.equalTo(
                """
                hide empty description
                state 1
                state 2
                state 3
                state 4
                [*]-down->1 : a
                [*]-down->2 : b
                [*]-down->3 : a
                1-down->3 : a
                1-down->1 : a
                1-down->2 : b
                2-down->3 : a
                2-down->1 : a
                2-down->2 : b
                3-down->4 : b
                4-down->[*] : EOF
                
            """.trimIndent()
            )
        )
    }

}

fun getNFA(pattern: String): NFA {
    val ast = parseAsAST(pattern)
    //First Set
    val firstSetVisitor = FirstSetVisitor()
    traversePostOrder(ast) { node ->
        firstSetVisitor.visit(node)
    }
    //Follow Set
    val followSetVisitor = FollowSetVisitor()
    val nfaGenerator = NFAGenerator(ast.firstSet.toList())
    traversePreOrder(ast) { node ->
        followSetVisitor.visit(node)
        nfaGenerator.visit(node)
    }
    return nfaGenerator.nfa
}