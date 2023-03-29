package io.github.light0x00.lightregex

/**
 * @author light
 * @since 2023/3/29
 */
class Parser {

    // (a|b|c)*abb

    /*

    primary_expr -> normal_chars | expr
    parenthesize_expr -> '(' primary_expr ')'
    unary_expr -> primary_expr | primary_expr '*'
    binary_expr -> unary_expr ｜ unary_expr '｜' binary_expr
    expr -> binary_expr


    primary_expr -> normal_chars | expr
    parenthesize_expr -> '(' primary_expr ')'
    unary_expr -> primary_expr | primary_expr '*'
    binary_expr -> unary_expr ｜ unary_expr '｜' binary_expr
    expr -> binary_expr
     */
    // (a|b|c)*abb
    /*

    primary_expr -> normal_chars | expr
    parenthesize_expr -> '(' primary_expr ')'
    unary_expr -> primary_expr | primary_expr '*'
    binary_expr -> unary_expr ｜ unary_expr '｜' binary_expr
    expr -> binary_expr


    primary_expr -> normal_chars | expr
    parenthesize_expr -> '(' primary_expr ')'
    unary_expr -> primary_expr | primary_expr '*'
    binary_expr -> unary_expr ｜ unary_expr '｜' binary_expr
    expr -> binary_expr
     */
    fun parsePrimary(reader: IReader) {

    }


    fun parseParenthesizeExpr(reader: IReader?) {}
}