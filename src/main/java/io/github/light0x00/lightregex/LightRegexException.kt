package io.github.light0x00.lightregex

fun readUnexpectedErrorMsg(reader: IReader, actual: String, expected: String): String {
    return """
                |${expected} expected ,but got ${actual} at line ${reader.line()} column ${reader.column()}
            """.trimMargin("|")
}

fun readUnexpectedErrorMsg(reader: IReader, expected: String): String {
    return readUnexpectedErrorMsg(reader, ":\n" + reader.nearbyChars(), expected)
}

fun readErrorMsg(reader: IReader, msg: String): String {
    return """
                |$msg
                |${reader.nearbyChars()}
                | at line ${reader.line()} column ${reader.column()}
                """.trimMargin()
}

/**
 * @author light
 * @since 2023/3/25
 */
class LightRegexException(message: String?) : RuntimeException(message) {
}