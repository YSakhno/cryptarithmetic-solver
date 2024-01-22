package io.ysakhno.puzzles.cryptarithmetic.parser

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf

/**
 * Unit tests for the [ParsingException] class.
 *
 * @author Yuri Sakhno
 */
class ParsingExceptionTest : ShouldSpec({
    should("be possible to create with message") {
        val exception = ParsingException("test message")

        assertSoftly(exception) {
            shouldBeInstanceOf<ParsingException>()
            message shouldBe "test message"
            withClue("not caused by other exception") { cause.shouldBeNull() }
        }
    }
    should("be throwable") {
        val exception = shouldThrowExactly<ParsingException> {
            throw ParsingException("test thrown exception")
        }

        assertSoftly(exception) {
            message shouldBe "test thrown exception"
            withClue("not caused by other exception") { cause.shouldBeNull() }
        }
    }
})
