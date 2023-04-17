package io.github.light0x00.lightregex.visitor

import io.github.light0x00.lightregex.ast.*
import io.github.light0x00.lightregex.common.LightRegexException

/**
 * @author light
 * @since 2023/4/6
 */
class FollowSetVisitor : AbstractVisitor() {

//    val nfa = NFA()

    override fun visitRegExpr(ast: RegExpr) {
        ast.expr.followSet = ast.accept.firstSet
//        nfa.addTrans(START_STATE, trans = ast.firstSet.toList())
    }

    override fun visitAccept(ast: Accept) {
//        nfa.addTrans(ACCEPT_STATE, trans = emptyList())
    }

    override fun visitAndExpr(ast: AndExpr) {
        ast.right.followSet = ast.followSet
        ast.left.followSet =
            if (ast.right.nullable) {
                ast.right.firstSet.union(ast.right.followSet)
            } else {
                ast.right.firstSet
            }
    }

    override fun visitOrExpr(ast: OrExpr) {
        ast.right.followSet = ast.followSet
        ast.left.followSet = ast.followSet
    }

    override fun visitUnaryExpr(ast: UnaryExpr) {
        val child = ast.expr
        val operator = ast.operator

        when (operator.type) {
            TokenType.ANY_TIMES, TokenType.AT_LEAST_ONCE -> {
                child.followSet = ast.followSet.union(child.firstSet)
            }
            TokenType.OPTIONAL -> {
                child.followSet = ast.followSet
            }
            else -> {
                throw LightRegexException("Unknown unary expr operator:" + ast.operator)
            }
        }
    }

    override fun visitToken(ast: AbstractToken) {
//        nfa.addTrans(ast.state!!, ast.followSet.toList())
    }

}