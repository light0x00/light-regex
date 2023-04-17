package io.github.light0x00.lightregex.lexcical

import io.github.light0x00.lightregex.ast.AbstractToken
import io.github.light0x00.lightregex.common.LightRegexException
import io.github.light0x00.lightregex.common.Unicode
import io.github.light0x00.lightregex.common.assertTrue
import io.github.light0x00.lightregex.common.readErrorMsg
import io.github.light0x00.lightregex.ast.MetaToken
import java.util.*

/**
 * @author light
 * @since 2023/3/29
 */
class GeneralLexer(
    private val reader: IReader,
    private var tokenizers: SortedSet<ITokenizer> = TOKENIZER_SET
) : IDynamicLexer,
    ILocalizable by reader {

    private val lookaheads = LinkedList<AbstractToken>()

    override fun switchTokenizers(newTokenizers: SortedSet<ITokenizer>): SortedSet<ITokenizer> {
        val old = tokenizers
        tokenizers = newTokenizers;
        return old
    }

    override fun lookahead(n: Int): AbstractToken {
        assertTrue(n > 0)
        if (lookaheads.size < n) {
            for (i in 1..n - lookaheads.size) {
                lookaheads.offer(tokenize())
            }
        }
        return lookaheads[n - 1]
    }

    override fun skip(n: Int) {
        for (i in 1..n) {
            next()
        }
    }

    override fun next(): AbstractToken {
        return if (lookaheads.isEmpty()) tokenize() else lookaheads.poll()
    }

    private fun tokenize(): AbstractToken {
        val lookahead = reader.lookahead()
        if (lookahead == Unicode.EOF) {
            return EOF_TOKEN
        }
        for (t in tokenizers) {
            if (t.support(reader::lookahead)) {
                return t.tokenize(reader)
            }
        }
        throw LightRegexException(readErrorMsg(this, "Unrecognized character"))
    }

}
