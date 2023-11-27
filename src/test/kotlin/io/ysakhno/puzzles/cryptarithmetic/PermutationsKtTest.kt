package io.ysakhno.puzzles.cryptarithmetic

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.sequences.shouldBeEmpty

/**
 * Unit tests for the [permutations] functions.
 *
 * @author Yuri Sakhno
 */
class PermutationsKtTest : FunSpec({
    context("Function permutations()") {
        test("permutations of empty string") {
            val permutations = "".permutations().toList()

            permutations.shouldContainExactly("")
        }
        test("permutations of 1-character string") {
            val permutations = "A".permutations().toList()

            permutations.shouldContainExactly("A")
        }
        test("permutations of 2-character string") {
            val permutations = "AB".permutations().toList()

            permutations.shouldContainExactly("AB", "BA")
        }
        test("permutations of 3-character string") {
            val permutations = "ABC".permutations().toList()

            permutations.shouldContainExactly("ABC", "ACB", "BAC", "BCA", "CAB", "CBA")
        }
        test("permutations of 2 characters from 4-character string") {
            val permutations = "ABCD".permutations(2).toList()

            permutations.shouldContainExactly("AB", "AC", "AD", "BA", "BC", "BD", "CA", "CB", "CD", "DA", "DB", "DC")
        }
        test("should return unique objects (r = 0)") {
            val result = listOf(1, 2, 3).permutations(0).toList()

            result.shouldContainExactly(listOf(emptyList()))
        }
        test("should return unique objects (r = 1)") {
            val result = listOf(1, 2, 3).permutations(1).toList()

            result.shouldContainExactly(listOf(1), listOf(2), listOf(3))
        }
        test("should return unique objects (r = 2)") {
            val result = listOf(1, 2, 3).permutations(2).toList()

            result.shouldContainExactly(
                listOf(1, 2),
                listOf(1, 3),
                listOf(2, 1),
                listOf(2, 3),
                listOf(3, 1),
                listOf(3, 2),
            )
        }
        test("should return unique objects (r = 3)") {
            val result = listOf(1, 2, 3).permutations().toList()

            result.shouldContainExactly(
                listOf(1, 2, 3),
                listOf(1, 3, 2),
                listOf(2, 1, 3),
                listOf(2, 3, 1),
                listOf(3, 1, 2),
                listOf(3, 2, 1),
            )
        }
        context("should return empty sequence if r > length") {
            for (n in 0..5) {
                for (r in n + 1..6) {
                    test("length = $n; r = $r") {
                        val list = listOf(1, 2, 3, 4, 5, 6, 7).take(n)
                        val permutations = list.permutations(r)

                        permutations.shouldBeEmpty()
                    }
                }
            }
        }
    }
})
