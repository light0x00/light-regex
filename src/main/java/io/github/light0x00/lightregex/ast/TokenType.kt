package io.github.light0x00.lightregex.ast

enum class TokenType(val label: String) {
    SINGLE_LITERAL("SINGLE_LITERAL"), ANY_LITERAL("."), RANGE_LITERAL("SINGLE_LITERAL_RANGE"),
    SHORTHAND_SYMBOL("SHORTHAND_SYMBOL"),
    REPEAT_TIMES_RANGE("REPEAT_TIMES_RANGE"),

    OPTIONAL("?"),AT_LEAST_ONCE("+"), ANY_TIMES("*"), OR("|"), LEFT_PARENTHESIS("("), RIGHT_PARENTHESIS(")"),
    LEFT_SQUARE_BRACKET("["), RIGHT_SQUARE_BRACKET("]"),
    HYPHEN("-"),
    START("^"),
    END("$"),
    EOF("EOF")
}