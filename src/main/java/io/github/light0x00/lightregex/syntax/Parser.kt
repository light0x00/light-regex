package io.github.light0x00.lightregex.syntax

import io.github.light0x00.lightregex.ast.*
import io.github.light0x00.lightregex.common.LightRegexException
import io.github.light0x00.lightregex.common.Unicode
import io.github.light0x00.lightregex.common.readErrorMsg
import io.github.light0x00.lightregex.lexcical.*

//import io.github.light0x00.lightregex.ast.TokenType.*

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
val AND_EXPR_FOLLOW_SET: Set<TokenType> = setOf(TokenType.END, TokenType.EOF, TokenType.RIGHT_PARENTHESIS)

/**
 * @author light
 * @since 2023/3/29
 */

class Parser(private val lexer: IDynamicLexer) {

    private fun parseShorthand(): AST {
        val token = lexer.expectNext(TokenType.SHORTHAND_SYMBOL)
        token as ShorthandToken
        return when (token.symbol) {
            's'.code -> {
                SingleToken(Unicode.SPACE)
            }
            'w'.code -> {
                OrExpr(
                    OrExpr(LiteralRangeToken(0x41, 0x51), LiteralRangeToken(0x61, 0x7a)),
                    LiteralRangeToken(0x30, 0x39)
                )
            }
            'd'.code -> {
                LiteralRangeToken(0x30, 0x39)
            }
            else -> {
                throw LightRegexException("Unrecognized shorthand:$token")
            }
        }
    }

    private fun parseRangeLiteral(): AST {
        //中括号表达式内可以存在 速记符号 [\wa-z]
        if (lexer.lookahead().type == TokenType.SHORTHAND_SYMBOL) {
            return parseShorthand()
        }
        var literal = lexer.expectNext(TokenType.SINGLE_LITERAL)
        literal as SingleToken
        if (lexer.lookahead().type == TokenType.HYPHEN) {
            lexer.skip() //消耗掉 -
            val literal2 = lexer.expectNext(TokenType.SINGLE_LITERAL)
            literal2 as SingleToken
            literal = LiteralRangeToken(literal.lexeme, literal2.lexeme)
        }
        return literal

    }

    private fun parseSquareBracketExpr(): AST {
        lexer.expectNext(TokenType.LEFT_SQUARE_BRACKET)
        val originalTokenizers = lexer.switchTokenizers(TOKENIZER_SET_FOR_SQUARE_BRACKET_EXPR)

        var ast = parseRangeLiteral()
        while (lexer.lookahead().type != TokenType.RIGHT_SQUARE_BRACKET) {
            ast = OrExpr(ast, parseRangeLiteral())
        }
        lexer.skip() //消耗掉 ]

        lexer.switchTokenizers(originalTokenizers)
        return ast
    }

    private fun parsePrimary(): AST {
        val lookahead = lexer.lookahead()
        return when (lookahead.type) {
            TokenType.SINGLE_LITERAL, TokenType.ANY_LITERAL -> {
                lexer.next()
            }

            TokenType.LEFT_PARENTHESIS -> {
                lexer.skip()
                val ast = parseExpr()
                lexer.expectNext(TokenType.RIGHT_PARENTHESIS)
                ast
            }

            TokenType.LEFT_SQUARE_BRACKET -> {
                parseSquareBracketExpr()
            }

            TokenType.SHORTHAND_SYMBOL -> {
                parseShorthand()
            }

            else ->
                throw LightRegexException(readErrorMsg(lexer, """Unexpected metacharacter: ${lookahead.type}"""))
        }
    }

    private fun parseUnaryExpr2(): AST {
        var ast: AST = parsePrimary()

        when (lexer.lookahead().type) {
            TokenType.ANY_TIMES, TokenType.REPEAT_TIMES_RANGE -> {
                ast = UnaryExpr(ast, lexer.next())
            }

            else -> {
            }
        }
        return ast
    }

    private fun parseUnaryExpr(): AST {
        var ast: AST = parsePrimary()

        when (lexer.lookahead().type) {
            TokenType.ANY_TIMES, TokenType.OPTIONAL, TokenType.AT_LEAST_ONCE -> {
                ast = UnaryExpr(ast, lexer.next())
            }

            TokenType.REPEAT_TIMES_RANGE -> {
                val operator = lexer.next() as RepeatTimesRangeToken
                val toCopy = ast;
                for (i in 2..operator.min) {
                    ast = AndExpr(ast, toCopy.copy())
                }
                if (operator.infinite) {
                    ast = AndExpr(ast, UnaryExpr(toCopy.copy(), Token(TokenType.ANY_TIMES)))
                } else {
                    for (i in 1..operator.max - operator.min) {
                        ast = AndExpr(ast, UnaryExpr(toCopy.copy(), Token(TokenType.OPTIONAL)))
                    }
                }
            }

            else -> {
            }
        }
        return ast
    }

    private fun parseOrExpr(): AST {
        val ast = parseUnaryExpr()
        return when (lexer.lookahead().type) {
            TokenType.OR -> {
                lexer.skip() //消耗掉 '|'
                OrExpr(ast, parseOrExpr())
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
        var ast = parseOrExpr()
        //需要继续 Shift 生成 And 的情况
        while (lexer.lookahead().type !in AND_EXPR_FOLLOW_SET) {
            ast = AndExpr(left = ast, right = parseOrExpr())
        }
        return ast
    }

    private fun parseExpr(): AST {
        return parseAndExpr()
    }

    fun parse(): RegExpr {
        val matchFromStart = lexer.skipNextIfMatch(TokenType.START)
        val expr = parseExpr()
        val matchToEnd = lexer.skipNextIfMatch(TokenType.END)
        lexer.expectNext(TokenType.EOF)
        return RegExpr(expr, Accept(matchToEnd), matchFromStart)
    }
}