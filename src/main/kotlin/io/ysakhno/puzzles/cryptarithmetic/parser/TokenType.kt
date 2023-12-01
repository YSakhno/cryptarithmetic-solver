package io.ysakhno.puzzles.cryptarithmetic.parser

/** Functional type that describes a function that performs a binary operation on its two operands. */
typealias BinaryOperation = (lhs: Int, rhs: Int) -> Int

/**
 * Defines all known and supported token types.
 *
 * @property binaryOperation if not `null`, specifies a lambda-function that will perform a binary operation (operation
 * with two operands) corresponding to that particular token type.
 * @author Yuri Sakhno
 */
enum class TokenType(val binaryOperation: BinaryOperation? = null) {

    /** Ephemeral (imaginary) token emitted to signify the end of expression. */
    EOE,

    /** An integer number literal. */
    NUMBER,

    /** Variable that should be substituted with digits (and solved). */
    VARIABLE,

    /*
     * Priority (parenthesized expression)
     */

    /** Left (opening) parenthesis (`(`).  Used to start primary (prioritized) expression. */
    LEFT_PAREN,

    /** Right (closing) parenthesis (`)`).  Used to close primary (prioritized) expression. */
    RIGHT_PAREN,

    /*
     * Arithmetic operators
     */

    /** Circumflex (`^`) (a.k.a. "caret").  Used to provide exponentiation (raising to power) in the expression. */
    CIRCUMFLEX,

    /** Asterisk (`*`) (a.k.a. "star").  Used to denote arithmetic multiplication operation in the expression. */
    ASTERISK(binaryOperation = { lhs, rhs -> lhs * rhs }),

    /** Forward slash (`/`).  Used to denote integer division operation, i.e. without a remainder or fractional part. */
    SLASH(binaryOperation = { lhs, rhs -> lhs / rhs }),

    /** Plus sign (`+`).  Used to denote arithmetic addition operation in the expression. */
    PLUS(binaryOperation = { lhs, rhs -> lhs + rhs }),

    /** Minus sign (`-`) (a.k.a. "dash", a.k.a. "hyphen").  Used to denote arithmetic subtraction operation. */
    MINUS(binaryOperation = { lhs, rhs -> lhs - rhs }),

    /*
     * Comparison operators
     */

    /** Less-than sign (`<`).  Used to compare two parts of the expression for inequality. */
    LESS_THAN(binaryOperation = { lhs, rhs -> if (lhs < rhs) 1 else 0 }),

    /** Equals sign (`=`).  Used to compare two sides if the expression for equality. */
    EQUALS(binaryOperation = { lhs, rhs -> if (lhs == rhs) 1 else 0 }),

    /** Greater-than sign (`>`).  Used to compare two parts of the expression for inequality. */
    GREATER_THAN(binaryOperation = { lhs, rhs -> if (lhs > rhs) 1 else 0 }),

    /** Less than or equals operator (`<=`).  Used to compare to parts of the expression. */
    LESS_EQUALS(binaryOperation = { lhs, rhs -> if (lhs <= rhs) 1 else 0 }),

    /** Greater than or equals operator (`>=`).  Used to compare to parts of the expression. */
    GREATER_EQUALS(binaryOperation = { lhs, rhs -> if (lhs >= rhs) 1 else 0 }),
}
