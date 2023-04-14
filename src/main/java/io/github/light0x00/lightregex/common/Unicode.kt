package io.github.light0x00.lightregex.common

class Unicode {

    companion object {
        const val STAR = '*'.code
        const val OR = '|'.code
        const val LEFT_PARENTHESIS = '('.code
        const val RIGHT_PARENTHESIS = ')'.code
        const val DOT = '.'.code
        const val LEFT_SLASH = '\\'.code

        const val RIGHT_SLASH = '/'.code
        const val LINE_FEED = '\n'.code
        const val LEFT_SQUARE_BRACKET = '['.code
        const val RIGHT_SQUARE_BRACKET = ']'.code
        const val HYPHEN = '-'.code
        const val LEFT_CURLY_BRACKET = '{'.code
        const val RIGHT_CURLY_BRACKET = '}'.code
        const val COMMA = ','.code
        const val QUESTION_MARK = '?'.code
        const val PLUS_SIGN = '+'.code
        const val WEDGE = '^'.code
        const val DOLLAR_SIGN = '$'.code

        /**
         * unicode 最大码点为 0x10FFFF http://www.unicode.org/L2/L2000/00079-n2175.htm
         * 所以用0xFFFFFFFF 表示读取到了结尾
         */
        const val EOF = Int.MAX_VALUE

        fun toString(code: Int): String {
            return if (code == EOF) {
                "EOF"
            } else {
                Character.toString(code)
            }
        }

        fun isValidUnicode(code: Int): Boolean {
            return code in 0..0x10FFFF;
        }

        fun isLetter(code: Int): Boolean {
            return isLetterSmallCase(code) || isLetterCapitalCase(code)
        }

        fun isLetterCapitalCase(code: Int): Boolean {
            return code in 0x41..0x5A
        }

        fun isLetterSmallCase(code: Int): Boolean {
            return code in 0x61..0x7A
        }

        fun isDigit(code: Int): Boolean {
            return code in 0x30..0x39
        }

        fun isHexLiteral(code: Int): Boolean {
            return isDigit(code) || code in 0x41..0x46
        }

        fun unicodeHexToString(unicode: String): String {
            return String(Character.toChars(Integer.parseInt(unicode, 16)))
        }
    }
}