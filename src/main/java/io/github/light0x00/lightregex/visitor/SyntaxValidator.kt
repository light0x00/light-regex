package io.github.light0x00.lightregex.visitor

import io.github.light0x00.lightregex.ast.*

/**
 * @author light
 * @since 2023/4/10
 */
class SyntaxValidator : AbstractVisitor() {
    override fun visitRegExpr(ast: RegExpr) {

    }

    override fun visitAccept(ast: Accept) {
    }

    override fun visitAndExpr(ast: AndExpr) {
    }

    override fun visitOrExpr(ast: OrExpr) {
    }

    override fun visitUnaryExpr(ast: UnaryExpr) {
    }

    override fun visitToken(ast: Token) {
    }
}