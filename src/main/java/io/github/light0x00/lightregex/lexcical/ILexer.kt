package io.github.light0x00.lightregex.lexcical

import io.github.light0x00.lightregex.common.LightRegexException
import io.github.light0x00.lightregex.common.readUnexpectedErrorMsg
import io.github.light0x00.lightregex.ast.Token
import io.github.light0x00.lightregex.ast.TokenType
import java.util.*

val EOF_TOKEN = Token(TokenType.EOF)

/*
 * 需要可切换分词器的能力的原因在于：
 *
 * 一些符号位于中括号内时，要当作匹配字符处理，如 [/.*(]
 * 而当位于中括号之外时，则需要当作特殊字符
 *
 * 一些符号位于中括号内时，要当作特殊字符，如表示范围的符号"-" [a-z]
 * 而当位于中括号之外时，是普通匹配字符
 *
 * 语法分析时，需要两种分词逻辑
 */
interface IDynamicLexer : ILexer {
    /**
     * 使用新的分词器集合代替旧的，返回旧的分词器集合
     */
    fun switchTokenizers(newTokenizers: SortedSet<ITokenizer>): SortedSet<ITokenizer>
}

/**
 * @author light
 * @since 2023/3/29
 */
interface ILexer : Iterator<Token>, ILocalizable {

    fun lookahead(n: Int = 1): Token

    fun skip(n: Int = 1)

    fun skipNextIfMatch(type: TokenType): Boolean {
        return if (lookahead().type == type) {
            skip()
            true
        } else {
            false
        }
    }

    override fun hasNext(): Boolean {
        return lookahead() != EOF_TOKEN
    }

    fun expectNext(vararg expectation: TokenType): Token {
        return next().also {
            if (!expectation.contains(it.type)) {
                throw LightRegexException(
                    readUnexpectedErrorMsg(
                        this,
                        expectation.map { it.label }.joinToString(separator = " or ")
                    )
                )
            }
        }
    }

}