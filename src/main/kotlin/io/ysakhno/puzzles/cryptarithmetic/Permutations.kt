package io.ysakhno.puzzles.cryptarithmetic

/**
 * Produces a sequence of [r]-length permutations of elements in this iterable.
 *
 * If [r] is less than `0`, then `r` defaults to the length of the iterable and all possible full-length permutations
 * are generated.  If `r` is greater than the length of the iterable, this function will return an empty sequence.
 *
 * The permutation lists are emitted in lexicographic order according to the order of the input iterable.  So, if the
 * input iterable is sorted, the output lists will be produced in sorted order.
 *
 * Elements are treated as unique based on their position, not on their value.  So if the input elements are unique,
 * there will be no repeated values within a permutation.
 */
fun <E> Iterable<E>.permutations(r: Int = -1): Sequence<List<E>> = sequence {
    val pool = toList()
    val n = pool.size
    val r2 = if (r < 0) n else r

    if (r2 > n) return@sequence

    val indices = (0..<n).toMutableList()
    val cycles = (n downTo n - r2 + 1).toList().toIntArray()

    yield(indices.subList(0, r2).map { pool[it] })

    outer@while (true) {
        for (i in r2 - 1 downTo 0) {
            if (--cycles[i] != 0) {
                indices.swapAt(i, n - cycles[i])
                yield(indices.subList(0, r2).map { pool[it] })
                continue@outer
            }
            indices.add(indices.removeAt(i))
            cycles[i] = n - i
        }
        return@sequence
    }
}

/**
 * Produces a sequence of [r]-length permutations of characters from this [character sequence][CharSequence].
 *
 * If [r] is less than `0`, then `r` defaults to the length of the input character sequence and all possible full-length
 * permutations are generated.  If `r` is greater than the length of the input character sequence, this function will
 * return an empty sequence.
 *
 * The permutation strings are emitted in lexicographic order according to the order of the input character sequence.
 * So, if the input character sequence is sorted, the output strings will be produced in sorted order.
 *
 * Characters are treated as unique based on their position, not on their value.  So if the input characters are unique
 * (case-sensitively), there will be no repeated values within a permutation.
 */
fun CharSequence.permutations(r: Int = length): Sequence<String> =
    toList().permutations(r).map { it.joinToString(separator = "") }

/** Swaps elements in this list at indices [index1] and [index2]. */
private fun <E> MutableList<E>.swapAt(index1: Int, index2: Int) {
    this[index1] = this[index2].also { this[index2] = this[index1] }
}
