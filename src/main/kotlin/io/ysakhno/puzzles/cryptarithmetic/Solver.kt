package io.ysakhno.puzzles.cryptarithmetic

import io.ysakhno.puzzles.cryptarithmetic.parser.eval

/**
 * Given a [formula], like `ODD + ODD = EVEN`, fills in digits to solve it and returns a sequence of all possible
 * solutions if there are any.
 *
 * @param formula the formula to solve.  Must be solvable and written in form that would imply equality/inequality of
 * left and right parts.
 * @return a sequence of formulas with digits filled-in, or empty sequence if the input [formula] is not solvable or has
 * parsing problems.
 */
fun solve(formula: String): Sequence<String> = formula.fillIn().filter { it.isValid }

/**
 * Given a [formula], like `ODD + ODD = EVEN`, fills in digits to solve it.
 *
 * @param formula the formula to solve.  Must be solvable and written in form that would imply equality/inequality of
 * left and right parts.
 * @return a string with digits filled-in, or empty string if the input [formula] is not solvable or has parsing
 * problems.
 */
fun solveFirst(formula: String): String = solve(formula).firstOrNull().orEmpty()

/** Generates all possible filling-ins of letters in formula with digits. */
private fun String.fillIn() = sequence {
    val letters = filter { it in 'A'..'Z' }.toSet().joinToString(separator = "")
    for (digits in "0123456789".permutations(letters.length)) {
        yield(translate(makeTrans(letters, digits)))
    }
}

private val NUMBERS_WITH_LEADING_ZEROES_REGEX = "\\b0[0-9]".toRegex()

/**
 * Determines whether the formula represented by this string is valid.
 *
 * The formula is deemed to be valid if and only if it does not have numbers with leading zeroes, and the equality
 * checks out mathematically.  For example, the formula `2*2=4` is valid, while `2*2=5` is not.
 */
private val String.isValid
    get() = !contains(NUMBERS_WITH_LEADING_ZEROES_REGEX) && eval(this)
