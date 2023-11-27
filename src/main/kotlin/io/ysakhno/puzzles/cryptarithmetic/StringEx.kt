package io.ysakhno.puzzles.cryptarithmetic

/** Type that represents translation dictionary for use with [CharSequence.translate]. */
typealias TranslationDictionary = Map<Char, CharSequence>

/**
 * Builds a translation dictionary usable for [CharSequence.translate].
 *
 * The [character sequences][CharSequence] [src] and [dst] must be of equal length, and in the resulting dictionary,
 * each character in `src` will be mapped to the character at the same position in `dst`.  If [rm] is not an empty
 * character sequence, its characters will be mapped to an empty character sequence in the resulting dictionary.
 *
 * @throws IllegalArgumentException if character sequences [src] and [dst] do not have the same length, or `src`
 * contains duplicate characters (case-sensitively).
 */
fun makeTrans(src: CharSequence, dst: CharSequence, rm: CharSequence = ""): TranslationDictionary {
    require(src.length == dst.length) { "Character sequences src and dst must be of the same length" }
    return buildMap {
        for (i in src.indices) {
            val ch = src[i]
            require(ch !in this) { "All characters of src must be distinct. Found repetition at index $i." }
            put(ch, dst[i].toString())
        }
        for (r in rm) {
            put(r, "")
        }
    }
}

/** See [makeTrans] function. */
@Deprecated(
    level = DeprecationLevel.WARNING,
    message = "Use equivalent function 'makeTrans', named according to Kotlin naming conventions",
    replaceWith = ReplaceWith("makeTrans(src, dst, rm)"),
)
fun maketrans(src: CharSequence, dst: CharSequence, rm: CharSequence = "") = makeTrans(src, dst, rm)

/**
 * Returns a string copy of this [character sequence][CharSequence] in which each character has been mapped through the
 * given translation [dictionary].
 *
 * When indexed by a [character][Char] from the input character sequence, the supplied dictionary can do any of the
 * following: return a string, to map the character to one or more other characters; return empty string, to delete the
 * character from the resulting string.  When the mapping for the particular character is absent in the dictionary, the
 * character from the input character sequence is mapped to itself.
 *
 * Function [makeTrans] can be used to create a translation map from character-to-character mappings in different
 * formats.
 */
fun CharSequence.translate(dictionary: TranslationDictionary): String = buildString(length) {
    for (ch in this@translate) {
        if (ch in dictionary) {
            append(dictionary[ch])
        } else {
            append(ch)
        }
    }
}
