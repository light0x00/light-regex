package io.github.light0x00.lightregex.common

import io.github.light0x00.lightregex.lexcical.ILocalizable


fun readUnexpectedErrorMsg(lexer: ILocalizable, expected: Int): String {
    return readUnexpectedErrorMsg(lexer, Character.toString(expected))
}

fun readUnexpectedErrorMsg(
    lexer: ILocalizable,
    expected: String,
    actual: String = ":\n" + lexer.nearbyChars()
): String {
    return """
                |${expected} expected ,but got ${actual} at line ${lexer.line()} column ${lexer.column()}
            """.trimMargin()
}

fun readErrorMsg(lexer: ILocalizable, msg: String): String {
    return """
                |$msg
                |${lexer.nearbyChars()}
                | at line ${lexer.line()} column ${lexer.column()}
                """.trimMargin()
}

/**
 * @author light
 * @since 2023/3/25
 */
class LightRegexException(message: String?) : RuntimeException(message) {
}