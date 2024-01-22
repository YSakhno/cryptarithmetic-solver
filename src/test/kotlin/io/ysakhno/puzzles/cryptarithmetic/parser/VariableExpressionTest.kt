package io.ysakhno.puzzles.cryptarithmetic.parser

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.Exhaustive
import io.kotest.property.arbitrary.intRange
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.pair
import io.kotest.property.checkAll
import io.kotest.property.exhaustive.enum
import io.kotest.property.exhaustive.filter
import io.ysakhno.puzzles.cryptarithmetic.parser.TokenType.VARIABLE

/**
 * Unit tests for the [VariableExpression] class.
 *
 * @author Yuri Sakhno
 */
class VariableExpressionTest : ShouldSpec({
    context("Default constructor") {
        should("throw exception with tokens of incorrect type") {
            val tokenGen =
                Arb.pair(Exhaustive.enum<TokenType>().filter { it != VARIABLE }.toArb(), Arb.intRange(0..100))
                    .map { (tokenType, range) -> Token(tokenType, range) }
            checkAll(tokenGen) { token ->
                val exception = shouldThrowExactly<IllegalArgumentException> { VariableExpression(token) }

                assertSoftly(exception) {
                    message shouldBe "Token must be of type VARIABLE"
                    withClue("not caused by other exception") { cause.shouldBeNull() }
                }
            }
        }
    }
    context("Function eval()") {
        should("return 0 for empty input range") {
            val text = "ABCDEFGHI"
            val topExpression = TopExpression(text, buildCorrespondence(text, (1..9).toList()))

            for (i in 0..text.length) {
                val range = i..<i
                val token = Token(VARIABLE, range)
                val variableExpression = VariableExpression(token)

                withClue("for range $range") { variableExpression.eval(topExpression) shouldBe 0 }
            }
        }
    }
})
