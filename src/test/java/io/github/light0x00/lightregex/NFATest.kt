package io.github.light0x00.lightregex

import io.github.light0x00.lightregex.lexcical.GeneralLexer
import io.github.light0x00.lightregex.lexcical.StringReader
import io.github.light0x00.lightregex.syntax.Parser
import io.github.light0x00.lightregex.visitor.FirstSetVisitor
import io.github.light0x00.lightregex.visitor.FollowSetVisitor
import io.github.light0x00.lightregex.visitor.NFAGenerator
import org.junit.jupiter.api.Test

/**
 * @author light
 * @since 2023/4/7
 */
class NFATest {

    @Test
    fun test() {
        val ast = Parser(GeneralLexer(StringReader("(a|b)*ab")))
            .parse()
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

        val plantUMLSource = nfaToPlantUML(nfaGenerator.nfa)
        println(plantUMLSource)

    }
}