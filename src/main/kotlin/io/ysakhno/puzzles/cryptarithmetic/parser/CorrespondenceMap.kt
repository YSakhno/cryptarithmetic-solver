package io.ysakhno.puzzles.cryptarithmetic.parser

/** The number of Latin letters in the alphabet. */
private const val NUMBER_OF_LETTERS_IN_ALPHABET: Int = 26

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

    /** Returns the number (digit) corresponding to the given Latin letter, or `0` if there is no correspondence. */
    operator fun get(ch: Char): Int {
        require(ch in 'A'..'Z') { "Index must be a Latin letter" }
        return correspondence[ch - 'A']
    }
}

/**
 * Builds a [correspondence map][CorrespondenceMap] by matching each character in [src] to a digit represented by each
 * character at the same position in [dst].
 *
 * @throws IllegalArgumentException if character sequences [src] and [dst] do not have the same length.
 */
fun buildCorrespondence(src: CharSequence, dst: CharSequence): CorrespondenceMap {
    require(src.length == dst.length) { "Character sequences src and dst must be of the same length" }
    val correspondence = IntArray(NUMBER_OF_LETTERS_IN_ALPHABET)
    for (i in src.indices) {
        correspondence[src[i] - 'A'] = dst[i] - '0'
    }
    return CorrespondenceMap(correspondence)
}
