package io.github.light0x00.lightregex

class MatchResult {
    lateinit var ranges: MutableList<IntRange>

    override fun toString(): String {
        return ranges.toString()
    }
}