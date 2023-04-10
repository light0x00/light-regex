package io.github.light0x00.lightregex.visitor

import io.github.light0x00.lightregex.*
import io.github.light0x00.lightregex.ast.*

/**
 *
 * @author light
 * @since 2023/4/2
 */
abstract class AbstractVisitor : IVisitor {

    override fun visit(ast: AST) = dispatchToVisit(ast)

    protected fun dispatchToVisit(ast: AST) {
        when (ast) {
            is Token -> {
                visitToken(ast)
            }
            is AndExpr -> {
                visitAndExpr(ast)
            }
            is UnaryExpr -> {
                visitUnaryExpr(ast)
            }
            is OrExpr -> {
                visitOrExpr(ast)
            }
            is RegExpr -> {
                visitRegExpr(ast)
            }
            is Accept -> {
                visitAccept(ast)
            }
            else -> {
                throw LightRegexException("Unknown ast node type encountered:" + ast.javaClass)
            }
        }
    }

    protected abstract fun visitRegExpr(ast: RegExpr)

    protected abstract fun visitAccept(ast: Accept)

    protected abstract fun visitAndExpr(ast: AndExpr)

    protected abstract fun visitOrExpr(ast: OrExpr)

    protected abstract fun visitUnaryExpr(ast: UnaryExpr)

    protected abstract fun visitToken(ast: Token)


}