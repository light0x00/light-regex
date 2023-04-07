package io.github.light0x00.lightregex.visitor

import io.github.light0x00.lightregex.*
import io.github.light0x00.lightregex.syntax.*

/**
 * @author light
 * @since 2023/4/6
 */
class FollowSetVisitor : AbstractVisitor() {

    override fun visitRegExpr(ast: RegExpr) {
        ast.expr.followSet = ast.accept.firstSet
    }

    override fun visitAccept(ast: Accept) {

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
        when (ast.operator.type) {
            TokenType.STAR -> {
                ast.expr.followSet = ast.followSet.union(ast.expr.firstSet)
            }
            else -> {
                throw LightRegexException("Unknown unary expr operator:" + ast.operator)
            }
        }
    }

    override fun visitToken(ast: Token) {

    }

}