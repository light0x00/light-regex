package io.github.light0x00.lightregex

import io.github.light0x00.lightregex.lexcical.GeneralLexer
import io.github.light0x00.lightregex.lexcical.StringReader
import io.github.light0x00.lightregex.syntax.LiteralToken
import io.github.light0x00.lightregex.syntax.OrExpr
import io.github.light0x00.lightregex.syntax.Parser
import io.github.light0x00.lightregex.visitor.FirstSetVisitor
import io.github.light0x00.lightregex.visitor.FollowSetVisitor
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.containsInAnyOrder
import org.junit.jupiter.api.Test

/**
 * @author light
 * @since 2023/4/6
 */
class FollowSetVisitorTest {

    @Test
    fun test() {
        val ast = Parser(GeneralLexer(StringReader("(a|b)*c")))
            .parse()
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

}