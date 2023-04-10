package io.github.light0x00.lightregex.visitor

import io.github.light0x00.lightregex.*
import io.github.light0x00.lightregex.ast.*

/**
 * @author light
 * @since 2023/4/2
 */
class FirstSetVisitor : AbstractVisitor() {

    var stateId = 1

    override fun visitRegExpr(ast: RegExpr) {
        ast.firstSet =
            if (ast.expr.nullable) {
                ast.expr.firstSet.union(ast.accept.firstSet)
            } else {
                ast.expr.firstSet
            }
        ast.nullable = ast.expr.nullable
    }

    override fun visitAccept(ast: Accept) {
        ast.firstSet = setOf(Transition(EOFInput(), ACCEPT_STATE))
    }

    override fun visitAndExpr(ast: AndExpr) {
        ast.firstSet =
            if (ast.left.nullable) {
                ast.left.firstSet.union(ast.right.firstSet)
            } else {
                ast.left.firstSet
            }
        ast.nullable = ast.left.nullable && ast.right.nullable
    }

    override fun visitOrExpr(ast: OrExpr) {
        ast.firstSet = ast.left.firstSet.union(ast.right.firstSet)
        ast.nullable = ast.left.nullable || ast.right.nullable
    }

    override fun visitUnaryExpr(ast: UnaryExpr) {
        when (ast.operator.type) {
            TokenType.ANY_TIMES,TokenType.OPTIONAL -> {
                ast.firstSet = ast.expr.firstSet
                ast.nullable = true
            }
            TokenType.AT_LEAST_ONCE-> {
                ast.firstSet = ast.expr.firstSet
            }
            else -> {
                throw LightRegexException("Unknown unary expr operator:" + ast.operator)
            }
        }
    }

    override fun visitToken(ast: Token) {
        val input = when (ast.type) {
            TokenType.SINGLE_LITERAL -> {
                ast as LiteralToken
                LiteralInput(ast.lexeme)
            }
            TokenType.SINGLE_LITERAL_ANY -> {
                AnyInput()
            }
            else -> {
                throw LightRegexException("Unknown token type:" + ast.type)
            }
        }
        ast.state = NFAState(stateId++)
        ast.firstSet = setOf(Transition(input, ast.state!!))
    }

}