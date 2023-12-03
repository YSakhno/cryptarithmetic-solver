package io.ysakhno.puzzles.cryptarithmetic.parser

import io.ysakhno.puzzles.cryptarithmetic.parser.TokenType.VARIABLE

/**
 * The top level expression that needs to be evaluated.
 *
 * @property text the text that represents this expression.
 * @property correspondence the correspondence map that specifies letter-to-digit correspondence.  Each unique Latin
 * letter in the [text] should be assigned a digit in order for the expression to be successfully evaluated later.
 * @author Yuri Sakhno
 */
data class TopExpression(val text: CharSequence, val correspondence: CorrespondenceMap)

/** The base interface that all expression types must implement. */
sealed interface Expression {

    /** Evaluates this expression using the specified [topExpression] and returns the result of the evaluation. */
    fun eval(topExpression: TopExpression): Int
}

/**
 * Represents a binary expression.  Each side of the binary expression is itself an instance of [Expression].
 *
 * @param lhs left hand side of the expression (first operand).
 * @param op lambda-function that performs a binary operation assigned to this expression on its two operands.
 * @param rhs right hand side of the expression (second operand).
 */
class BinaryExpression(
    private val lhs: Expression,
    private val op: BinaryOperation,
    private val rhs: Expression,
) : Expression {

    override fun eval(topExpression: TopExpression) = op(lhs.eval(topExpression), rhs.eval(topExpression))
}

/** Represents the literal expression, which always evaluates to the value of the pre-computed literal number. */
class LiteralExpression(private val number: Int) : Expression {

    override fun eval(topExpression: TopExpression) = number
}

/**
 * Represents the variable part in the expression.  This expression evaluates to a number value based on the
 * [top level expression][TopExpression] supplied to its [eval] function.
 *
 * @param variableToken the token of type [VARIABLE][VARIABLE] that was parsed from the original expression.
 */
class VariableExpression(variableToken: Token) : Expression {

    private val tokenRange = variableToken.range

    init {
        require(variableToken.type == VARIABLE) { "Token must be of type ${VARIABLE.name}" }
    }

    override fun eval(topExpression: TopExpression): Int {
        var number = 0
        for (i in tokenRange) {
            number *= DECIMAL
            number += topExpression.correspondence[topExpression.text[i]]
        }
        return number
    }
}

/**
 * Evaluates the pre-parsed expression using the specified correspondence map.
 *
 * Note: if [parsedExpression] was not obtained by parsing [text], the behavior of this function is undefined.
 *
 * @param text the text used to retrieve characters of the variables referenced in the expression.  This text must be
 * the same as the one that was originally used to obtain the [parsedExpression].
 * @param parsedExpression the parsed expression that needs to be evaluated.  This expression must be parsed from
 * [text].
 * @param correspondence the correspondence map that specifies letter-to-digit correspondence.  Each unique Latin letter
 * in the [text] should be assigned a digit in order for the expression to be successfully evaluated.
 * @return the result of evaluating the expression using the specified correspondence map; or `0` if any arithmetic
 * error (such as division by zero) occurred.
 */
internal fun eval(text: CharSequence, parsedExpression: Expression, correspondence: CorrespondenceMap): Int = try {
    parsedExpression.eval(TopExpression(text, correspondence))
} catch (ignored: ArithmeticException) {
    0
}
