package io.ysakhno.puzzles.cryptarithmetic.parser

import io.ysakhno.puzzles.cryptarithmetic.parser.TokenType.EOE

/**
 * Represents the token parsed from the input expression.
 *
 * @property type the type of this token
 * @property text the text that corresponds to this token.
 * @author Yuri Sakhno
 */
data class Token(val type: TokenType, val text: CharSequence)

/**
 * Performs the binary operation (such as addition, subtraction, etc.) if any such operation is defined for the
 * particular token [type].
 *
 * @param lhs the left hand side operand to perform the operation on.
 * @param rhs the right hand side operand to perform the operation on.
 * @return the result of the operation.
 * @throws AssertionError if this method was called on a token that does not have a defined binary operation.
 */
fun Token.performBinaryOperation(lhs: Int, rhs: Int) = type.binaryOperation?.let { it(lhs, rhs) }
    ?: throw AssertionError("Tokens of type $type do not have a binary operation")

/** Constant value for the end-of-expression token. */
val EOE_TOKEN = Token(EOE, "")

/** Returns the end-of-expression token.  Always returns the same token object. */
fun eoe() = EOE_TOKEN
