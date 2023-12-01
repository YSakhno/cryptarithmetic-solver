package io.ysakhno.puzzles.cryptarithmetic.parser

/**
 * Represents the token parsed from the input expression.
 *
 * @property type the type of this token
 * @property range the range, in original expression text, where this particular token appears.
 * @author Yuri Sakhno
 */
data class Token(val type: TokenType, val range: IntRange)

/**
 * Gets the binary operation (such as addition, subtraction, etc.) if any such operation is defined for the
 * [type][Token.type] of this particular token.
 *
 * @throws AssertionError if this property was accessed on a token that does not have a defined binary operation.
 */
val Token.binaryOperation: BinaryOperation
    get() = type.binaryOperation ?: throw AssertionError("Tokens of type $type do not have a binary operation")
