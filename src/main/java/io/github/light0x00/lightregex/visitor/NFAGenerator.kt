package io.github.light0x00.lightregex.visitor

import io.github.light0x00.lightregex.ast.*
import io.github.light0x00.lightregex.automata.ACCEPT_STATE
import io.github.light0x00.lightregex.automata.NFA
import io.github.light0x00.lightregex.automata.NTransition
import io.github.light0x00.lightregex.automata.START_STATE


/**
 * 作用：根据 AST 生成 DFA
 * 前置条件：
 *  1.已经计算出 AST 的 First Set、Follow Set
 *
 * 遍历顺序：无要求
 * @author light
 * @since 2023/4/7
 */
class NFAGenerator(startTran: List<NTransition>) : AbstractVisitor() {

    val nfa = NFA(startTran)
    override fun visitRegExpr(ast: RegExpr) {
        nfa.addTrans(START_STATE, trans = ast.firstSet.toList())
    }

    override fun visitAccept(ast: Accept) {
        nfa.addTrans(ACCEPT_STATE, trans = emptyList())
    }

    override fun visitAndExpr(ast: AndExpr) {
    }

    override fun visitOrExpr(ast: OrExpr) {
    }

    override fun visitUnaryExpr(ast: UnaryExpr) {

    }

    override fun visitToken(ast: AbstractToken) {
        nfa.addTrans(ast.state!!, ast.followSet.toList())
    }


}