package io.github.light0x00.lightregex.visitor

import io.github.light0x00.lightregex.NFA
import io.github.light0x00.lightregex.Transition
import io.github.light0x00.lightregex.syntax.*

/**
 * @author light
 * @since 2023/4/7
 */
class NFAGenerator(startTran: List<Transition>) : IVisitor {

    val nfa = NFA(startTran)

    override fun visit(ast: AST) {
        if (ast is Token)
            nfa.addState(ast.state!!, ast.followSet.toList())

    }
}