package io.github.light0x00.lightregex.syntax

import io.github.light0x00.lightregex.*
import io.github.light0x00.lightregex.syntax.TokenType.*
import io.github.light0x00.lightregex.lexcical.ILexer

/*
//存在 And 优先级高于 Or 的情况，这样会生成错误 AST
primary_expr -> literal | range_literal | '(' expr ')'
unary_expr -> primary_expr | primary_expr '*'
binary_expr ->
    unary_expr {Reduce ELSE}
    ｜
    unary_expr '｜' binary_expr {Shift IF lookahead is '|'}
    |
    unary_expr binary_expr {Shift IF lookahead not in: EOF,')','|' }
expr -> binary_expr {Follow=(')',EOF)}
S -> expr EOF

//版本3 解决 Or And 优先级问题， Or 优先于 And  (最终采纳版)
primary_expr -> literal | range_literal | '(' expr ')'
unary_expr -> primary_expr | primary_expr '*'
or_expr ->
    unary_expr {Reduce ELSE}
    |
    unary_expr '｜' or_expr {Shift IF lookahead is '|'}
and_expr ->
    or_expr {Reduce ELSE}
    |
    or_expr and_expr {Shift IF lookahead not in: EOF,')' }
expr -> and_expr {Follow=(')',EOF)}
S -> expr EOF
 */
val BINARY_EXPR_FOLLOW_SET: Set<TokenType> = setOf(TokenType.EOF, RIGHT_PARENTHESIS)

/**
 * @author light
 * @since 2023/3/29
 */
class Parser(private val lexer: ILexer) {

    private fun parsePrimary(): AST {
        val lookahead = lexer.lookahead()
        return when (lookahead.type) {
            LITERAL, LITERAL_ANY, LITERAL_RANGE, LITERAL_SEQUENCE -> {
                lexer.next()
            }
            LEFT_PARENTHESIS -> {
                lexer.next()
                val ast = parseExpr()
                lexer.expectNext(RIGHT_PARENTHESIS)
                ast
            }
            else ->
                throw LightRegexException(readUnexpectedErrorMsg(lexer, """string literal or "(""""))
        }
    }

    private fun parseUnaryExpr(): AST {
        var ast: AST = parsePrimary()
        if (lexer.lookahead().type == STAR) {
            ast = UnaryExpr(ast, lexer.next())
        }
        return ast
    }

    private fun parseOrExpr(): AST {
        val ast = parseUnaryExpr()
        return when (lexer.lookahead().type) {
            OR -> {
                OrExpr(ast, lexer.next(), parseOrExpr())
            }
            else -> {
                ast
            }
        }
    }

    /**
     * 自定底向上生成 And 树，前一个 And 会作为后一个 And 的左节点，树的生长方向为右上
     */
    private fun parseAndExpr(): AST {
        val ast = parseOrExpr()
        //需要 Reduce 的情况
        if (lexer.lookahead().type in BINARY_EXPR_FOLLOW_SET) {
            return ast
        }
        //需要继续 Shift 生成 And 的情况
        var lastAnd = AndExpr(left = ast, right = parseOrExpr())
        while (lexer.lookahead().type !in BINARY_EXPR_FOLLOW_SET) {
            lastAnd = AndExpr(left = lastAnd, right = parseOrExpr())
        }
        return lastAnd
    }

    private fun parseExpr(): AST {
        return parseAndExpr()
    }

    fun parse(): RegExpr {
        return parseExpr().let {
            lexer.expectNext(TokenType.EOF)
            RegExpr(it, Accept())
        }
    }
}