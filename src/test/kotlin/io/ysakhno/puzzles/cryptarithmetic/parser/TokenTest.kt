package io.ysakhno.puzzles.cryptarithmetic.parser

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.string.shouldMatch
import io.ysakhno.puzzles.cryptarithmetic.parser.TokenType.ASTERISK
import io.ysakhno.puzzles.cryptarithmetic.parser.TokenType.CIRCUMFLEX
import io.ysakhno.puzzles.cryptarithmetic.parser.TokenType.EOE
import io.ysakhno.puzzles.cryptarithmetic.parser.TokenType.EQUALS
import io.ysakhno.puzzles.cryptarithmetic.parser.TokenType.GREATER_EQUALS
import io.ysakhno.puzzles.cryptarithmetic.parser.TokenType.GREATER_THAN
import io.ysakhno.puzzles.cryptarithmetic.parser.TokenType.LEFT_PAREN
import io.ysakhno.puzzles.cryptarithmetic.parser.TokenType.LESS_EQUALS
import io.ysakhno.puzzles.cryptarithmetic.parser.TokenType.LESS_THAN
import io.ysakhno.puzzles.cryptarithmetic.parser.TokenType.MINUS
import io.ysakhno.puzzles.cryptarithmetic.parser.TokenType.NUMBER
import io.ysakhno.puzzles.cryptarithmetic.parser.TokenType.PLUS
import io.ysakhno.puzzles.cryptarithmetic.parser.TokenType.RIGHT_PAREN
import io.ysakhno.puzzles.cryptarithmetic.parser.TokenType.SLASH
import io.ysakhno.puzzles.cryptarithmetic.parser.TokenType.VARIABLE

/**
 * Unit tests for the [Token] data class.
 *
 * @author Yuri Sakhno
 */
class TokenTest : FunSpec({
    context("Extension property binaryOperation") {
        context("a token does not represent a binary operation") {
            withData(EOE, NUMBER, VARIABLE, LEFT_PAREN, RIGHT_PAREN) { tokenType ->
                val token = Token(tokenType, 0..0)
                val exception = shouldThrowExactly<AssertionError> { token.binaryOperation }

                assertSoftly(exception) {
                    message shouldMatch "Tokens of type [A-Z_]+ do not have a binary operation"
                    withClue("not caused by other exception") { cause.shouldBeNull() }
                }
            }
        }
        context("a token represents a binary operation") {
            withData(
                CIRCUMFLEX,
                ASTERISK,
                SLASH,
                PLUS,
                MINUS,
                LESS_THAN,
                EQUALS,
                GREATER_THAN,
                LESS_EQUALS,
                GREATER_EQUALS,
            ) { tokenType ->
                val token = Token(tokenType, 0..0)
                token.binaryOperation.shouldNotBeNull()
            }
        }
    }
})
