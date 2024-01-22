package io.ysakhno.puzzles.cryptarithmetic.parser

import io.kotest.assertions.fail
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.WithDataTestName
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.filter
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.nonNegativeInt
import io.kotest.property.arbitrary.pair
import io.kotest.property.checkAll
import io.kotest.property.forAll
import io.ysakhno.puzzles.cryptarithmetic.parser.TokenType.CIRCUMFLEX
import io.ysakhno.puzzles.cryptarithmetic.parser.TokenType.EQUALS
import io.ysakhno.puzzles.cryptarithmetic.parser.TokenType.GREATER_EQUALS
import io.ysakhno.puzzles.cryptarithmetic.parser.TokenType.GREATER_THAN
import io.ysakhno.puzzles.cryptarithmetic.parser.TokenType.LESS_EQUALS
import io.ysakhno.puzzles.cryptarithmetic.parser.TokenType.LESS_THAN

/** Specifies operands of a binary computation and expected result of the computation. */
private data class BinaryComputation(val lhs: Int, val op: String, val rhs: Int, val expected: Int) : WithDataTestName {

    /** Specifies the name of the test represented by this class. */
    override fun dataTestName() = "computing $lhs$op$rhs should return $expected"
}

/** A convenience function that creates a new instance of [BinaryComputation] for exponentiation operation. */
private fun exponentiation(lhs: Int, rhs: Int, expected: Int) = BinaryComputation(lhs, "^", rhs, expected)

/**
 * Unit tests for the [TokenType] enumeration class.
 *
 * @author Yuri Sakhno
 */
class TokenTypeTest : FunSpec({
    context("binaryOperation of CIRCUMFLEX") {
        val operationUnderTest = CIRCUMFLEX.binaryOperation ?: fail("operation is not defined for CIRCUMFLEX")
        withData(
            exponentiation(1, 0, 1),
            exponentiation(1, 1, 1),
            exponentiation(1, 2, 1),
            exponentiation(2, 0, 1),
            exponentiation(2, 1, 2),
            exponentiation(2, 2, 4),
            exponentiation(2, 3, 8),
            exponentiation(3, 0, 1),
            exponentiation(3, 1, 3),
            exponentiation(3, 2, 9),
            exponentiation(3, 3, 27),
            // Base 0
            exponentiation(0, 1, 0),
            exponentiation(0, 2, 0),
            exponentiation(0, 3, 0),
            // Some negative bases too
            exponentiation(-1, 1, -1),
            exponentiation(-1, 2, 1),
            exponentiation(-1, 3, -1),
            exponentiation(-2, 1, -2),
            exponentiation(-2, 2, 4),
            exponentiation(-2, 3, -8),
            // Known discrepancy: 0 to the power 0 is 1, according to the current implementation
            exponentiation(0, 0, 1),
        ) { (base, _, power, expected) ->
            operationUnderTest(base, power) shouldBe expected
        }
        test("should compute correct result (property-based testing)") {
            forAll(Arb.int(range = -32_768..32_768), Arb.nonNegativeInt(max = 31)) { base, exp ->
                operationUnderTest(base, exp) == base raisedTo exp
            }
        }
    }
    context("comparison operations") {
        val operationEquals = EQUALS.binaryOperation ?: fail("operation in not defined for EQUALS")
        val operationLessThan = LESS_THAN.binaryOperation ?: fail("operation in not defined for LESS_THAN")
        val operationGreaterThan = GREATER_THAN.binaryOperation ?: fail("operation in not defined for GREATER_THAN")
        val operationLessEquals = LESS_EQUALS.binaryOperation ?: fail("operation in not defined for LESS_EQUALS")
        val operationGreaterEquals =
            GREATER_EQUALS.binaryOperation ?: fail("operation in not defined for GREATER_EQUALS")

        val twoIntegersGen = Arb.pair(Arb.int(), Arb.int())
            .map { if (it.first > it.second) it.second to it.first else it }
            .filter { (a, b) -> a != b }

        test("should return correct result when one operand is less than the other") {
            checkAll(twoIntegersGen) { (lesser, greater) ->
                withClue("$lesser = $greater") { operationEquals(lesser, greater) shouldBe 0 }
                withClue("$greater = $lesser") { operationEquals(greater, lesser) shouldBe 0 }
                withClue("$lesser < $greater") { operationLessThan(lesser, greater) shouldBe 1 }
                withClue("$greater < $lesser") { operationLessThan(greater, lesser) shouldBe 0 }
                withClue("$greater > $lesser") { operationGreaterThan(greater, lesser) shouldBe 1 }
                withClue("$lesser > $greater") { operationGreaterThan(lesser, greater) shouldBe 0 }
                withClue("$lesser <= $greater") { operationLessEquals(lesser, greater) shouldBe 1 }
                withClue("$greater <= $lesser") { operationLessEquals(greater, lesser) shouldBe 0 }
                withClue("$greater >= $lesser") { operationGreaterEquals(greater, lesser) shouldBe 1 }
                withClue("$lesser >= $greater") { operationGreaterEquals(lesser, greater) shouldBe 0 }
            }
        }
        test("should return correct result when the two operands are equal") {
            checkAll(twoIntegersGen) { (integer) ->
                withClue("$integer = $integer") { operationEquals(integer, integer) shouldBe 1 }
                withClue("$integer = $integer") { operationEquals(integer, integer) shouldBe 1 }
                withClue("$integer < $integer") { operationLessThan(integer, integer) shouldBe 0 }
                withClue("$integer < $integer") { operationLessThan(integer, integer) shouldBe 0 }
                withClue("$integer > $integer") { operationGreaterThan(integer, integer) shouldBe 0 }
                withClue("$integer > $integer") { operationGreaterThan(integer, integer) shouldBe 0 }
                withClue("$integer <= $integer") { operationLessEquals(integer, integer) shouldBe 1 }
                withClue("$integer <= $integer") { operationLessEquals(integer, integer) shouldBe 1 }
                withClue("$integer >= $integer") { operationGreaterEquals(integer, integer) shouldBe 1 }
                withClue("$integer >= $integer") { operationGreaterEquals(integer, integer) shouldBe 1 }
            }
        }
    }
})

/** Raises this integer value to the specified integer exponent (power). */
private infix fun Int.raisedTo(exp: Int) = (1..exp).fold(1) { acc, _ -> acc * this }
