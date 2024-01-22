package io.ysakhno.puzzles.cryptarithmetic.parser

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe

/**
 * Unit tests for the [Parser] class.
 *
 * @author Yuri Sakhno
 */
class ParserTest : FunSpec({
    test("should fail when encounters unexpected token while parsing PrimaryExpression") {
        val expression = "-10 + 10 = 0"
        val exception = shouldThrowExactly<ParsingException> { parse(expression) }

        assertSoftly(exception) {
            message shouldBe "Unexpected token MINUS (-) while parsing primary expression"
            withClue("not caused by other exception") { cause.shouldBeNull() }
        }
    }
    test("should fail when the expression is incorrectly parenthesized (unexpected token reached)") {
        val expression = "(2+2 2 = 8"
        val exception = shouldThrowExactly<ParsingException> { parse(expression) }

        assertSoftly(exception) {
            message shouldBe "Was expecting RIGHT_PAREN, but found NUMBER (2)"
            withClue("not caused by other exception") { cause.shouldBeNull() }
        }
    }
    test("should fail when the expression is incorrectly parenthesized (end reached)") {
        val expression = "(2+2*2"
        val exception = shouldThrowExactly<ParsingException> { parse(expression) }

        assertSoftly(exception) {
            message shouldBe "End of the expression reached while looking for RIGHT_PAREN"
            withClue("not caused by other exception") { cause.shouldBeNull() }
        }
    }
    test("should fail when not all text was consumed during parsing") {
        val expression = "BAD REP"
        val exception = shouldThrowExactly<IllegalStateException> { parse(expression) }

        assertSoftly(exception) {
            message shouldBe "All input text should have been parsed, but currently sitting at VARIABLE (REP)"
            withClue("not caused by other exception") { cause.shouldBeNull() }
        }
    }
})
