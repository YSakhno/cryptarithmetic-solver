package io.ysakhno.puzzles.cryptarithmetic.parser

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
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind.AT_LEAST_ONCE
import kotlin.contracts.InvocationKind.EXACTLY_ONCE
import kotlin.contracts.contract

/** Specifies a radix to be used for conversion of decimal numbers to and from textual representation. */
const val DECIMAL = 10

/**
 * A parser for the arithmetic expression supplied through the [text] parameter.
 *
 * **Implementation note:** This class implements an LL(1) top-down parsing algorithm to parse the input text.
 *
 * @author Yuri Sakhno
 */
private class Parser(private val text: CharSequence) {

    /**
     * An instance of the tokenizer used to extract individual tokens from the input [text].
     */
    private val tokenizer = Tokenizer(text)

    /**
     * Index of the current (lookahead) token in the list of all tokens tokenized from the original input text.
     *
     * @see Tokenizer.tokens
     */
    private var curTokenIdx = 0

    /** Convenient computed property to access the current token (a.k.a. lookahead). */
    private val current get() = tokenizer.tokens[curTokenIdx]

    /**
     * Parses production of the `Expression` nonterminal, which is also the start symbol of the grammar.
     *
     * ```
     *     Expression
     *         : EqualityExpression
     *         ;
     * ```
     */
    fun parseExpression(): Expression = parseEqualityExpression()

    /**
     * Generic handler for the parser of a binary rule with left associativity.
     *
     * @param parser the function that will parse left- and right-hand sides of the rule.
     * @param accepted list of accepted token types that can occur between the left- and right-hand sides of the
     * expression.
     * @return the parsed expression.
     */
    @OptIn(ExperimentalContracts::class)
    private inline fun handleLeftAssociativeBinaryRule(
        parser: () -> Expression,
        vararg accepted: TokenType,
    ): Expression {
        contract {
            callsInPlace(parser, AT_LEAST_ONCE)
        }

        var left = parser()

        while (current.type in accepted) {
            left = BinaryExpression(left, consume().binaryOperation, parser())
        }

        return left
    }

    /**
     * Parses production of the `EqualityExpression` nonterminal.
     *
     * ```
     *     EqualityExpression
     *         : AdditiveExpression
     *         | EqualityExpression ( <EQUALS>
     *                              | <LESS_THAN>
     *                              | <LESS_EQUALS>
     *                              | <GREATER_THAN>
     *                              | <GREATER_EQUALS>
     *                              ) AdditiveExpression
     *         ;
     * ```
     */
    private fun parseEqualityExpression() = handleLeftAssociativeBinaryRule(
        ::parseAdditiveExpression,
        EQUALS,
        LESS_THAN,
        LESS_EQUALS,
        GREATER_THAN,
        GREATER_EQUALS,
    )

    /**
     * Parses production of the `AdditiveExpression` nonterminal.
     *
     * ```
     *     AdditiveExpression
     *         : MultiplicativeExpression
     *         | AdditiveExpression ( <PLUS> | <MINUS> ) MultiplicativeExpression
     *         ;
     * ```
     */
    private fun parseAdditiveExpression() =
        handleLeftAssociativeBinaryRule(::parseMultiplicativeExpression, PLUS, MINUS)

    /**
     * Parses production of the `MultiplicativeExpression` nonterminal.
     *
     * ```
     *     MultiplicativeExpression
     *         : ExponentiationExpression
     *         | MultiplicativeExpression ( <ASTERISK> | <SLASH> ) ExponentiationExpression
     *         ;
     * ```
     */
    private fun parseMultiplicativeExpression() =
        handleLeftAssociativeBinaryRule(::parseExponentiationExpression, ASTERISK, SLASH)

    /**
     * Parses production of the `ExponentiationExpression` nonterminal.
     *
     * ```
     *     ExponentiationExpression
     *         : PrimaryExpression
     *         | PrimaryExpression <CIRCUMFLEX> ExponentiationExpression
     *         ;
     * ```
     */
    private fun parseExponentiationExpression(): Expression = parsePrimaryExpression().let { left ->
        if (current.type != CIRCUMFLEX) left
        else BinaryExpression(left, consume().binaryOperation, parseExponentiationExpression())
    }

    /**
     * Parses production of the `PrimaryExpression` nonterminal.
     *
     * ```
     *     PrimaryExpression
     *         : <LEFT_PAREN> Expression <RIGHT_PAREN>
     *         | Number
     *         | Variable
     *         ;
     * ```
     */
    private fun parsePrimaryExpression() = when (current.type) {
        LEFT_PAREN -> consume(LEFT_PAREN).thenDo(::parseExpression).thenConsume(RIGHT_PAREN)
        NUMBER -> parseNumber()
        VARIABLE -> parseVariable()
        else -> throw ParsingException(
            "Unexpected token ${current.type} (${text.subSequence(current.range)}) while parsing primary expression",
        )
    }

    /**
     * Parses production of the `Number` nonterminal.
     *
     * ```
     *     Number
     *         : <NUMBER>
     *         ;
     * ```
     */
    private fun parseNumber() = LiteralExpression(text.parseRangeAsInt(consume(NUMBER).range))

    /**
     * Parses production of the `Variable` nonterminal.
     *
     * ```
     *     Variable
     *         : <VARIABLE>
     *         ;
     * ```
     */
    private fun parseVariable() = VariableExpression(consume(VARIABLE))

    @OptIn(ExperimentalContracts::class)
    inline fun <T> withFinalChecks(block: Parser.() -> T): T {
        contract {
            callsInPlace(block, EXACTLY_ONCE)
        }
        return block().also {
            check(current.type == EOE) {
                "All input text should have been parsed, but currently sitting at ${
                    current.type
                } (${text.subSequence(current.range)})"
            }
        }
    }

    /**
     * Consumes the current (lookahead) token regardless of its type (unless it is a special [EOE][TokenType.EOE] token)
     * and advances the [lookahead][current] to the next token.
     *
     * @throws ParsingException if the current token is the `EOE` (end-of-expression) token.
     */
    private fun consume() = if (current.type != EOE) current.also { ++curTokenIdx }
    else throw ParsingException("End of the expression reached while expecting a token")

    /**
     * Consumes the current (lookahead) token if it has type specified by [tokenType] and advances the
     * [lookahead][current] to the next token.
     *
     * @throws ParsingException if the current token's type is not the same as [tokenType].
     */
    private fun consume(tokenType: TokenType) = when (current.type) {
        tokenType -> consume()
        EOE -> throw ParsingException("End of the expression reached while looking for $tokenType")
        else -> throw ParsingException(
            "Was expecting $tokenType, but found ${current.type} (${text.subSequence(current.range)})",
        )
    }

    /** Convenience function for more streamlined implementation of the parser's rules. */
    private fun Expression.thenConsume(tokenType: TokenType) = also { consume(tokenType) }
}

/** Convenience function for more streamlined implementation of the parser's rules. */
@OptIn(ExperimentalContracts::class)
@Suppress("UnusedReceiverParameter")
private inline fun <R> Token.thenDo(block: () -> R): R {
    contract {
        callsInPlace(block, EXACTLY_ONCE)
    }
    return block()
}

/**
 * Parses the sub-sequence specified by [range] as a decimal integer.
 *
 * @throws IllegalArgumentException if the sub-sequence contains characters that do not represent a decimal digit
 * ('`0`' through '`9`').
 * @throws IndexOutOfBoundsException if [range] entirely or partially lies outside the bounds of this sequence.
 */
private fun CharSequence.parseRangeAsInt(range: IntRange): Int {
    var number = 0
    for (i in range) {
        number *= DECIMAL
        number += this[i].digitToInt(DECIMAL)
    }
    return number
}

/**
 * Parses the specified [expression] and returns the result of the parsing.  If the expression is malformed, an
 * exception will be thrown.
 */
@Throws(ParsingException::class)
fun parse(expression: CharSequence) = Parser(expression).withFinalChecks(Parser::parseExpression)
