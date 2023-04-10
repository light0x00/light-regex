package io.github.light0x00.lightregex.ast

enum class TokenType(val label: String) {
    SINGLE_LITERAL("SINGLE_LITERAL"), SINGLE_LITERAL_ANY("."), SINGLE_LITERAL_RANGE("SINGLE_LITERAL_RANGE"),

    REPEAT_TIMES_RANGE("REPEAT_TIMES_RANGE"),

    OPTIONAL("?"),AT_LEAST_ONCE("+"), ANY_TIMES("*"), OR("|"), LEFT_PARENTHESIS("("), RIGHT_PARENTHESIS(")"),
    LEFT_SQUARE_BRACKET("["), RIGHT_SQUARE_BRACKET("]"),
    HYPHEN("-"),
    EOF("$")
}