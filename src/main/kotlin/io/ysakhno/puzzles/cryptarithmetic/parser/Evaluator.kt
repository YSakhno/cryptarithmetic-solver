package io.ysakhno.puzzles.cryptarithmetic.parser

import io.ysakhno.puzzles.cryptarithmetic.parser.TokenType.ASTERISK
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
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind.AT_LEAST_ONCE
import kotlin.contracts.InvocationKind.EXACTLY_ONCE
import kotlin.contracts.contract

/** Specifies a radix to be used for conversion of decimal numbers to and from textual representation. */
const val DECIMAL = 10

private class Evaluator(text: CharSequence) {

    private val tokenizer = Tokenizer(text)

    private var curTokenIdx = 0

    private val current get() = tokenizer.tokens[curTokenIdx]

    fun evaluateExpression(): Int = evaluateEqualityExpression()

    /**
     * Generic handler for the parser of a binary rule with left associativity.
     *
     * @param parser the function that will parse and evaluate left- and right-hand sides of the rule.
     * @param accepted list of accepted token types that can occur between the left- and right-hand sides of the
     * expression.
     * @return the parsed and evaluated expression.
     */
    @OptIn(ExperimentalContracts::class)
    private inline fun handleLeftAssociativeBinaryRule(parser: () -> Int, vararg accepted: TokenType): Int {
        contract {
            callsInPlace(parser, AT_LEAST_ONCE)
        }

        var left = parser()

        while (current.type in accepted) {
            left = consume().performBinaryOperation(left, parser())
        }

        return left
    }

    private fun evaluateEqualityExpression() = handleLeftAssociativeBinaryRule(
        ::evaluateAdditiveExpression,
        EQUALS,
        LESS_THAN,
        LESS_EQUALS,
        GREATER_THAN,
        GREATER_EQUALS,
    )

    /**
     * Parses and evaluates the `AdditiveExpression` production.
     *
     * ```
     *     AdditiveExpression
     *         : MultiplicativeExpression
     *         | AdditiveExpression ( <PLUS> | <MINUS> ) MultiplicativeExpression
     *         ;
     * ```
     */
    private fun evaluateAdditiveExpression() =
        handleLeftAssociativeBinaryRule(::evaluateMultiplicativeExpression, PLUS, MINUS)

    /**
     * Parses and evaluates the `MultiplicativeExpression` production.
     *
     * ```
     *     MultiplicativeExpression
     *         : PrimaryExpression
     *         | MultiplicativeExpression ( <ASTERISK> | <SLASH> ) PrimaryExpression
     *         ;
     * ```
     */
    private fun evaluateMultiplicativeExpression() =
        handleLeftAssociativeBinaryRule(::evaluatePrimaryExpression, ASTERISK, SLASH)

    /**
     * Parses and evaluates the `PrimaryExpression` production.
     *
     * ```
     *     PrimaryExpression
     *         : <LEFT_PAREN> Expression <RIGHT_PAREN>
     *         | Number
     *         ;
     * ```
     */
    private fun evaluatePrimaryExpression() = when (current.type) {
        LEFT_PAREN -> consume(LEFT_PAREN).thenDo(::evaluateExpression).thenConsume(RIGHT_PAREN)
        NUMBER -> evaluateNumber()
        else -> parsingError {
            "Unexpected token ${current.type} (${current.text}) while evaluating primary expression"
        }
    }

    /**
     * Parses and evaluates the `Number` production.
     *
     * ```
     *     Number
     *         : <NUMBER>
     *         ;
     * ```
     */
    private fun evaluateNumber() = consume(NUMBER).text.toString().toInt(DECIMAL)

    @OptIn(ExperimentalContracts::class)
    inline fun <T> withFinalChecks(block: Evaluator.() -> T): T {
        contract {
            callsInPlace(block, EXACTLY_ONCE)
        }
        return block().also {
            check(current.type == EOE) {
                "All input text should have been parsed, but currently sitting at ${current.type} (${current.text})."
            }
        }
    }

    @OptIn(ExperimentalContracts::class)
    private inline fun parsingError(lazyMessage: () -> String): Nothing {
        contract {
            callsInPlace(lazyMessage, kotlin.contracts.InvocationKind.AT_MOST_ONCE)
        }
        throw ParsingException(lazyMessage())
    }

    private fun consume() = if (current.type != EOE) current.also { ++curTokenIdx }
    else parsingError { "End of the expression reached while expecting a token" }

    private fun consume(tokenType: TokenType) = when (current.type) {
        tokenType -> consume()
        EOE -> parsingError { "End of the expression reached while looking for $tokenType" }
        else -> parsingError { "Was expecting $tokenType, but found ${current.type} (${current.text})" }
    }

    private fun Int.thenConsume(tokenType: TokenType) = also { consume(tokenType) }
}

@OptIn(ExperimentalContracts::class)
@Suppress("UnusedReceiverParameter")
private inline fun <R> Token.thenDo(block: () -> R): R {
    contract {
        callsInPlace(block, EXACTLY_ONCE)
    }
    return block()
}

/**
 * Evaluates the specified [expression] and returns the result of the evaluation.  If the expression is malformed, or
 * contains an arithmetic error, `false` is returned.
 */
fun eval(expression: CharSequence) = try {
    Evaluator(expression).withFinalChecks(Evaluator::evaluateExpression) != 0
} catch (ignored: ArithmeticException) {
    false
}
