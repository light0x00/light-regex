package io.github.light0x00.lightregex.ast

import java.util.*

class RepeatTimesRangeToken(val min: Int, val max: Int = min, val infinite: Boolean = false) :
    AbstractToken(TokenType.REPEAT_TIMES_RANGE) {

    override fun toString(): String {
        return if (infinite) {
            "{$min,∞}"
        } else if (max != min) {
            return "{$min,${max}}"
        } else {
            return "{$min}"
        }
    }

    override fun equals(other: Any?): Boolean {
        return if (other is RepeatTimesRangeToken) {
            min == other.min && max == other.max && infinite == other.infinite
        } else {
            false
        }
    }

    override fun hashCode(): Int {
        return Objects.hash(min, max)
    }

    override fun copy(): RepeatTimesRangeToken {
        return RepeatTimesRangeToken(min, max, infinite)
    }
}