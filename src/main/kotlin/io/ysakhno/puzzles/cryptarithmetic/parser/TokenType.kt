package io.ysakhno.puzzles.cryptarithmetic.parser

/**
 * Defines all known and supported token types.
 *
 * @property binaryOperation if not `null`, specifies a lambda-function that will perform a binary operation (operation
 * with two operands) corresponding to that particular token type.
 * @property isSkipped whether tokens of this type should be skipped during parsing: `true` if they should be skipped,
 * `false` if they should not.
 * @author Yuri Sakhno
 */
enum class TokenType(
    val binaryOperation: ((lhs: Int, rhs: Int) -> Int)? = null,
    val isSkipped: Boolean = false,
) {
    EOE,
    WHITESPACE(isSkipped = true),
    NUMBER,

    // Priority (parenthesized expression)
    LEFT_PAREN,
    RIGHT_PAREN,

    // Arithmetic operators
    CIRCUMFLEX,
    ASTERISK(binaryOperation = { lhs, rhs -> lhs * rhs }),
    SLASH(binaryOperation = { lhs, rhs -> lhs / rhs }),
    PLUS(binaryOperation = { lhs, rhs -> lhs + rhs }),
    MINUS(binaryOperation = { lhs, rhs -> lhs - rhs }),

    // Comparison operators
    LESS_THAN(binaryOperation = { lhs, rhs -> if (lhs < rhs) 1 else 0 }),
    EQUALS(binaryOperation = { lhs, rhs -> if (lhs == rhs) 1 else 0 }),
    GREATER_THAN(binaryOperation = { lhs, rhs -> if (lhs > rhs) 1 else 0 }),
    GREATER_EQUALS(binaryOperation = { lhs, rhs -> if (lhs >= rhs) 1 else 0 }),
    LESS_EQUALS(binaryOperation = { lhs, rhs -> if (lhs <= rhs) 1 else 0 }),
}
