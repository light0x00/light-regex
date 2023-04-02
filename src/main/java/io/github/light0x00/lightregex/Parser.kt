package io.github.light0x00.lightregex

import io.github.light0x00.lightregex.TokenType.LITERAL

// (a|b|c)*abb
// (a|b|c)*a*b*b
// a|b|c*a*b*b
//a*b*
//a.b.c*
/*
primary_expr -> normal_chars | '.' | '(' expr ')'
unary_expr -> primary_expr | primary_expr '*'
unary_and_expr -> unary_expr {IF lookahead in ['|',EOF]} | unary_expr unary_and_expr {ELSE}
binary_expr -> unary_and_expr ｜ unary_and_expr '｜' binary_expr
expr -> binary_expr EOF


//版本2 解决重复问题
//存在 And 优先级高于 Or 的情况，这样会生成错误 AST
primary_expr -> normal_chars | '.' | '(' expr ')'
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
primary_expr -> normal_chars | '.' | '(' expr ')'
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
val Binary_Expr_Follow_Set: Set<Token> = setOf(EOF, RIGHT_Parenthesis)

/**
 * @author light
 * @since 2023/3/29
 */
class Parser(private val lexer: ILexer) {

    private fun parsePrimary(): AST {
        val lookahead = lexer.lookahead()
        return if (lookahead.type == LITERAL) {
            PrimaryExpr(lexer.next())
        } else {
            when (lookahead.lexeme) {
                "(" -> {
                    lexer.next()
                    val ast = parseExpr()
                    lexer.expectNext(RIGHT_Parenthesis)
                    ast
                }
                "." -> {
                    PrimaryExpr(lexer.next())
                }
                else ->
                    throw LightRegexException(readUnexpectedErrorMsg(lexer, "string literal or ( or ."))
            }
        }
    }

    private fun parseUnaryExpr(): AST {
        var ast: AST = parsePrimary()
        if (lexer.lookahead() == STAR) {
            ast = UnaryExpr(ast, lexer.next())
        }
        return ast
    }

    private fun parseOrExpr(): AST {
        val ast = parseUnaryExpr()
        return when (lexer.lookahead()) {
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
        if (lexer.lookahead() in Binary_Expr_Follow_Set) {
            return ast
        }
        //需要继续 Shift 生成 And 的情况
        var lastAnd = AndExpr(left = ast, right = parseOrExpr())
        while (lexer.lookahead() !in Binary_Expr_Follow_Set) {
            lastAnd = AndExpr(left = lastAnd, right = parseOrExpr())
        }
        return lastAnd
    }

    /**
     * 自顶向下生成 And 树，由于是右递归文法，所以树向右下方生长
     */
    private fun parseAndExprR(): AST {
        val ast = parseOrExpr()
        return when (lexer.lookahead()) {
            //goto And Expr
            !in Binary_Expr_Follow_Set -> {
                AndExpr(ast, parseAndExprR())
            }
            //reduce
            else -> {
                ast
            }
        }
    }

    private fun parseExpr(): AST {
        return parseAndExpr()
    }

    fun parse(): AST {
        return parseExpr().also {
            lexer.expectNext(EOF)
        }
    }
}