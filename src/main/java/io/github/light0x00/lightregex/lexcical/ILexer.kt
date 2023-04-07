package io.github.light0x00.lightregex.lexcical

import io.github.light0x00.lightregex.*
import io.github.light0x00.lightregex.syntax.Token
import io.github.light0x00.lightregex.syntax.TokenType


/**
 * @author light
 * @since 2023/3/29
 */
interface ILexer : Iterator<Token>, ILocalizable {

    fun lookahead(): Token

    fun lookahead(n: Int): Token

    override fun hasNext(): Boolean {
        return lookahead().type !== TokenType.EOF
    }

    fun expectNext(expectation: TokenType): Token {
        return next().also {
            if (it.type != expectation) {
                throw LightRegexException(readUnexpectedErrorMsg(this, expectation.toString()))
            }
        }

    }

}