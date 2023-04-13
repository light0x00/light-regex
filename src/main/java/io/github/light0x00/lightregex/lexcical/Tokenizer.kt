package io.github.light0x00.lightregex.lexcical

import io.github.light0x00.lightregex.ast.LiteralToken
import io.github.light0x00.lightregex.ast.RepeatTimesRangeToken
import io.github.light0x00.lightregex.ast.Token
import io.github.light0x00.lightregex.ast.TokenType
import io.github.light0x00.lightregex.common.LightRegexException
import io.github.light0x00.lightregex.common.Unicode
import io.github.light0x00.lightregex.common.readErrorMsg
import io.github.light0x00.lightregex.common.readUnexpectedErrorMsg

/**
 * @author light
 * @since 2023/4/9
 */

/**
 * 方括号内的特殊符号
 */
private val SPECIAL_SYMBOL_INSIDE_SQUARE_BRACKET_EXPR = mapOf(
    Unicode.HYPHEN to TokenType.HYPHEN,
    Unicode.LEFT_SQUARE_BRACKET to TokenType.LEFT_SQUARE_BRACKET,
    Unicode.RIGHT_SQUARE_BRACKET to TokenType.RIGHT_SQUARE_BRACKET
)

/**
 * 方括号外的特殊符号
 */
private val SPECIAL_SYMBOL = mapOf(
    Unicode.STAR to TokenType.ANY_TIMES,
    Unicode.OR to TokenType.OR,
    Unicode.LEFT_PARENTHESIS to TokenType.LEFT_PARENTHESIS,
    Unicode.RIGHT_PARENTHESIS to TokenType.RIGHT_PARENTHESIS,
    Unicode.DOT to TokenType.ANY_LITERAL,
    Unicode.LEFT_SQUARE_BRACKET to TokenType.LEFT_SQUARE_BRACKET,
    Unicode.RIGHT_SQUARE_BRACKET to TokenType.RIGHT_SQUARE_BRACKET,
    Unicode.QUESTION_MARK to TokenType.OPTIONAL,
    Unicode.PLUS_SIGN to TokenType.AT_LEAST_ONCE,
    Unicode.WEDGE to TokenType.START,
    Unicode.DOLLAR_SIGN to TokenType.END,
)

val TOKENIZER_SET = sortedSetOf(
    //转义
    UnicodeEscapeTokenizer(),
    //匹配字符
    SingleTokenizer(),
    //特殊字符
    SpecialSymbolTokenizer(SPECIAL_SYMBOL),
    //转义
    EscapeTokenizer(),
    //处理 {m,n}
    RepeatTimesRangeTokenizer()
)

val TOKENIZER_SET_FOR_SQUARE_BRACKET_EXPR = sortedSetOf(
    //转义
    UnicodeEscapeTokenizer(),
    SingleTokenizer(),
    /**
     * 方括号内，特殊符号的定义不同
     */
    SpecialSymbolTokenizer(SPECIAL_SYMBOL_INSIDE_SQUARE_BRACKET_EXPR),
    EscapeTokenizer(),
)

interface ITokenizer : Comparable<ITokenizer> {
    val precedence: Int
        get() = 0

    fun support(lookahead: (i: Int) -> Int): Boolean
    fun tokenize(reader: IReader): Token

    override fun compareTo(other: ITokenizer): Int {
        return if (other.precedence > this.precedence) 1 else -1
    }
}

private class UnicodeEscapeTokenizer : ITokenizer {

    override fun support(lookahead: (Int) -> Int): Boolean {
        return lookahead(1) == Unicode.LEFT_SLASH && lookahead(2) == 'u'.code
    }

    override fun tokenize(reader: IReader): Token {
        reader.skip(2) //消耗掉 '\','u'
        reader.match('{'.code) //消耗掉 '{'
        val unicode = StringBuilder(4)
        while (reader.lookahead() != '}'.code) {
            if (!Unicode.isHexLiteral(reader.lookahead())) {
                throw LightRegexException(readErrorMsg(reader, "Invalid hex literal"))
            }
            unicode.append(Character.toChars(reader.read()))
        }
        reader.skip() //消耗掉 "}"
        val code = Integer.parseInt(unicode.toString(), 16)
        if (!Unicode.isValidUnicode(code)) {
            throw LightRegexException(readErrorMsg(reader, "Invalid Unicode code-point:$code"))
        }
        return LiteralToken(code)
    }
}

class EscapeTokenizer : ITokenizer {

    override val precedence: Int
        get() = -1

    override fun support(lookahead: (i: Int) -> Int): Boolean {
        return lookahead(1) == Unicode.LEFT_SLASH
    }

    override fun tokenize(reader: IReader): Token {
        reader.skip()
        return LiteralToken(reader.read())
    }
}

class SpecialSymbolTokenizer(val symbolTable: Map<Int, TokenType>) : ITokenizer {

    override fun support(lookahead: (Int) -> Int): Boolean {
        return symbolTable.contains(lookahead(1))
    }

    override fun tokenize(reader: IReader): Token {
        return Token(symbolTable[reader.read()]!!)
    }
}

private class SingleTokenizer : ITokenizer {
    override val precedence: Int
        get() = Int.MIN_VALUE

    override fun support(lookahead: (Int) -> Int): Boolean {
        return lookahead(1) != Unicode.EOF
    }

    override fun tokenize(reader: IReader): Token {
        return LiteralToken(reader.read())
    }
}


private class RepeatTimesRangeTokenizer : ITokenizer {
    override fun support(lookahead: (i: Int) -> Int): Boolean {
        return lookahead(1) == Unicode.LEFT_CURLY_BRACKET
    }


    override fun tokenize(reader: IReader): Token {
        reader.skip()
        //m
        val min = readDigit(reader) ?: throw LightRegexException(readUnexpectedErrorMsg(reader, "digit"))
        val symbol = reader.read()
        //}
        if (symbol == Unicode.RIGHT_CURLY_BRACKET)
            return RepeatTimesRangeToken(min)
        //,
        if (symbol != Unicode.COMMA) {
            throw LightRegexException(readUnexpectedErrorMsg(reader, """ "," or "}" """))
        }
        //n
        val max = readDigit(reader)
        reader.match(Unicode.RIGHT_CURLY_BRACKET)
        return RepeatTimesRangeToken(min, max ?: min, infinite = max == null)

    }

    fun readDigit(reader: IReader): Int? {
        val sb = StringBuilder()
        while (Unicode.isDigit(reader.lookahead())) {
            sb.appendCodePoint(reader.read())
        }
        return sb.toString().toIntOrNull()
    }

}
