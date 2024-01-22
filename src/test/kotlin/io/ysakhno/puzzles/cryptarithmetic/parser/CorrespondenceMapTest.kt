package io.ysakhno.puzzles.cryptarithmetic.parser

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldMatch
import io.kotest.property.Arb
import io.kotest.property.arbitrary.Codepoint
import io.kotest.property.arbitrary.ascii
import io.kotest.property.arbitrary.char
import io.kotest.property.arbitrary.filter
import io.kotest.property.arbitrary.filterNot
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.intArray
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.nonNegativeInt
import io.kotest.property.arbitrary.of
import io.kotest.property.arbitrary.pair
import io.kotest.property.arbitrary.string
import io.kotest.property.arbitrary.withEdgecases
import io.kotest.property.checkAll
import io.kotest.property.exhaustive.exhaustive
import io.kotest.property.exhaustive.filter
import io.kotest.property.forAll

/**
 * Unit tests for the [CorrespondenceMap] class and its helper functions.
 *
 * @author Yuri Sakhno
 */
class CorrespondenceMapTest : BehaviorSpec({
    Given("Class CorrespondenceMap") {
        When("its default constructor called") {
            And("the array parameter has incorrect length") {
                Then("fails") {
                    val intArrayGen =
                        Arb.intArray(length = Arb.int(range = 0..100), content = Arb.nonNegativeInt(max = 9))
                            .filter { it.size != 26 }

                    checkAll(intArrayGen) { ints ->
                        val exception = shouldThrowExactly<IllegalArgumentException> {
                            CorrespondenceMap(ints)
                        }

                        assertSoftly(exception) {
                            message shouldBe "Correspondence array must have exactly 26 elements"
                            withClue("not caused by other exception") { cause.shouldBeNull() }
                        }
                    }
                }
            }
        }
        When("a character is retrieved from the map") {
            val map = buildCorrespondence("TAXI", listOf(1, 7, 2, 9))

            And("the character is a digit") {
                Then("corresponding integer value is retrieved") {
                    forAll(iterations = 100, ('0'..'9').toList().exhaustive()) { ch ->
                        map[ch] == ch.digitToInt()
                    }
                }
            }
            And("the character is mapped (any of A, I, T, X)") {
                Then("correct mapped integer value is retrieved") {
                    assertSoftly {
                        map['T'] shouldBe 1
                        map['A'] shouldBe 7
                        map['X'] shouldBe 2
                        map['I'] shouldBe 9
                    }
                }
            }
            And("the character is not mapped (none of A, I, T, X") {
                Then("zero (0) is retrieved") {
                    forAll(iterations = 100, ('A'..'Z').toList().exhaustive().filter { it !in "TAXI".toSet() }) { ch ->
                        map[ch] == 0
                    }
                }
            }
            And("the character is not a digit and not uppercase Latin letter") {
                Then("fails") {
                    val indexingCharacterGen = Arb.char()
                        .filterNot { it in '0'..'9' }
                        .filterNot { it in 'A'..'Z' }

                    checkAll(indexingCharacterGen) { ch ->
                        val exception = shouldThrowExactly<IllegalArgumentException> {
                            map[ch]
                        }

                        assertSoftly(exception) {
                            message shouldBe "Index must be a decimal digit or a Latin letter"
                            withClue("not caused by other exception") { cause.shouldBeNull() }
                        }
                    }
                }
            }
        }
    }
    Given("function buildCorrespondence()") {
        When("lengths of its parameters differ") {
            Then("fails") {
                val srcAndDstGen = Arb.pair(
                    Arb.string(maxSize = NUMBER_OF_LETTERS_IN_ALPHABET, codepoints = Codepoint.uppercaseAZ()),
                    Arb.list(Arb.nonNegativeInt(max = 9), range = 0..NUMBER_OF_LETTERS_IN_ALPHABET),
                ).filter { (src, dst) -> src.length != dst.size }

                checkAll(srcAndDstGen) { (src, dst) ->
                    val exception = shouldThrowExactly<IllegalArgumentException> {
                        buildCorrespondence(src, dst)
                    }

                    assertSoftly(exception) {
                        message shouldBe "Character sequence src must have the same length as list dst"
                        withClue("not caused by other exception") { cause.shouldBeNull() }
                    }
                }
            }
        }
        When("parameter src has non-uppercase or non-Latin characters") {
            Then("fails") {
                val srcAndDstGen = Arb.pair(
                    Arb.string(minSize = 1, maxSize = NUMBER_OF_LETTERS_IN_ALPHABET, codepoints = Codepoint.ascii()),
                    Arb.list(Arb.nonNegativeInt(max = 9), range = 1..NUMBER_OF_LETTERS_IN_ALPHABET),
                ).map { (src, dst) ->
                    when {
                        src.length < dst.size -> src to dst.subList(0, src.length)
                        src.length > dst.size -> src.subSequence(0, dst.size) to dst
                        else -> src to dst
                    }
                }.filterNot { (src) -> src.all { it in 'A'..'Z' } }

                checkAll(srcAndDstGen) { (src, dst) ->
                    val exception = shouldThrowExactly<ArrayIndexOutOfBoundsException> {
                        buildCorrespondence(src, dst)
                    }

                    assertSoftly(exception) {
                        message shouldMatch "Index -?\\d+ out of bounds for length 26"
                        withClue("not caused by other exception") { cause.shouldBeNull() }
                    }
                }
            }
        }
    }
})

/** Custom helper generator for codepoints in the range `A`-`Z`, with uppercase letters. */
private fun Codepoint.Companion.uppercaseAZ(): Arb<Codepoint> = Arb.of(('A'.code..'Z'.code).map(::Codepoint))
    .withEdgecases(Codepoint('A'.code))
