package io.github.light0x00.lightregex.lexcical

/**
 * @author light
 * @since 2023/3/24
 */
interface IReader : ILocalizable {

    /**
     * 向后读一个字符,当读到最后一个字符，继续读则返 Unicode.EOF
     */
    fun read(): Int

    /**
     * 向后看N个字符
     */
    fun lookahead(n: Int = 1): Int

    /**
     * 跳过N个字符
     */
    fun skip(n: Int = 1)

    /**
     * 匹配一个预期字符,如果成功返回该字符,否则排除异常
     */
    fun match(vararg expectation: Int): Int

}