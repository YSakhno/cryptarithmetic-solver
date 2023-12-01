package io.ysakhno.puzzles.cryptarithmetic.parser

/**
 * The correspondence map used to define Latin letter to decimal digit correspondence.
 *
 * @author Yuri Sakhno
 */
typealias CorrespondenceMap = Map<Char, Int>

/**
 * Builds a [correspondence map][CorrespondenceMap] by matching each character in [src] to a digit represented by each
 * character at the same position in [dst].
 *
 * @throws IllegalArgumentException if character sequences [src] and [dst] do not have the same length.
 */
fun buildCorrespondence(src: CharSequence, dst: CharSequence): CorrespondenceMap {
    require(src.length == dst.length) { "Character sequences src and dst must be of the same length" }
    return buildMap {
        for (i in src.indices) {
            val ch = src[i]
            put(ch, dst[i] - '0')
        }
    }
}
