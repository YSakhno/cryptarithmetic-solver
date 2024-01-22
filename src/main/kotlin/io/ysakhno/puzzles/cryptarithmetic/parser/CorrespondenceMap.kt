package io.ysakhno.puzzles.cryptarithmetic.parser

/** The number of Latin letters in the alphabet. */
internal const val NUMBER_OF_LETTERS_IN_ALPHABET: Int = 26

/**
 * The correspondence map used to define Latin letter to decimal digit correspondence.
 *
 * @author Yuri Sakhno
 */
class CorrespondenceMap internal constructor(private val correspondence: IntArray) {

    init {
        require(correspondence.size == NUMBER_OF_LETTERS_IN_ALPHABET) {
            "Correspondence array must have exactly $NUMBER_OF_LETTERS_IN_ALPHABET elements"
        }
    }

    /**
     * Returns the number (digit) corresponding to the given Latin letter [ch], or `0` if there is no correspondence.
     *
     * As a matter of courtesy to the user, this function accepts decimal digits ('`0`' through '`9`') as the input
     * character [ch], in which case it returns the integer matching that digit.  For example, for decimal digit
     * character '`7`', it returns the integer number `7`.
     */
    operator fun get(ch: Char): Int {
        require(ch in '0'..'9' || ch in 'A'..'Z') { "Index must be a decimal digit or a Latin letter" }
        return if (ch in '0'..'9') ch.digitToInt(DECIMAL) else correspondence[ch - 'A']
    }
}

/**
 * Builds a [correspondence map][CorrespondenceMap] by matching each character in [src] to a digit represented by each
 * number at the same position in [dst].
 *
 * @throws IllegalArgumentException if character sequence [src] does not have the same length as list [dst].
 */
fun buildCorrespondence(src: CharSequence, dst: List<Int>): CorrespondenceMap {
    require(src.length == dst.size) { "Character sequence src must have the same length as list dst" }
    val correspondence = IntArray(NUMBER_OF_LETTERS_IN_ALPHABET)
    for (i in src.indices) {
        correspondence[src[i] - 'A'] = dst[i]
    }
    return CorrespondenceMap(correspondence)
}
