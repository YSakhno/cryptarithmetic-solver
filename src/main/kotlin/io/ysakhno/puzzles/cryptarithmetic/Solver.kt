package io.ysakhno.puzzles.cryptarithmetic

import io.ysakhno.puzzles.cryptarithmetic.parser.DECIMAL
import io.ysakhno.puzzles.cryptarithmetic.parser.buildCorrespondence
import io.ysakhno.puzzles.cryptarithmetic.parser.eval
import io.ysakhno.puzzles.cryptarithmetic.parser.parse

/** Regular expression that finds all letters that occur at the beginning of variables in an expression. */
private val FIRST_LETTERS = "\\b[A-Z](?=[0-9A-Z])".toRegex()

/** List of all decimal digits, from `0` to `9`, as integer numbers. */
private val DECIMAL_DIGITS = (0..<DECIMAL).toList()

/**
 * Given a [formula], like `ODD + ODD = EVEN`, fills in digits to solve it and returns a sequence of all possible
 * solutions if there are any.
 *
 * If `formula` contains more than 10 different uppercase Latin letters, such formula is considered unsolvable, as there
 * is no way to assign 10 different decimal digits to the set of letters in this case.
 *
 * @param formula the formula to solve.  Must be solvable and written in form that would imply equality/inequality of
 * left and right parts.
 * @return a sequence of formulas with digits filled-in, or empty sequence if the input [formula] is not solvable or has
 * parsing problems.
 */
fun solve(formula: String): Sequence<String> = sequence {
    val letters = formula.filter { it in 'A'..'Z' }.toSet().joinToString(separator = "")
    if (letters.length > DECIMAL) return@sequence
    val expression = parse(formula)
    val lettersThatCannotBeZero =
        FIRST_LETTERS.findAll(formula).map { it.value.first() }.distinct().toList().toCharArray()

    for (digits in DECIMAL_DIGITS.permutations(letters.length)) {
        val corr = buildCorrespondence(letters, digits)
        if (lettersThatCannotBeZero.none { corr[it] == 0 } && eval(formula, expression, corr) != 0) {
            yield(formula.translate(makeTrans(letters, digits.joinToString(separator = ""))))
        }
    }
}

/**
 * Given a [formula], like `ODD + ODD = EVEN`, fills in digits to solve it.
 *
 * If `formula` contains more than 10 different uppercase Latin letters, such formula is considered unsolvable, as there
 * is no way to assign 10 different decimal digits to the set of letters in this case.
 *
 * @param formula the formula to solve.  Must be solvable and written in form that would imply equality/inequality of
 * left and right parts.
 * @return a string with digits filled-in, or empty string if the input [formula] is not solvable or has parsing
 * problems.
 */
fun solveFirst(formula: String): String = solve(formula).firstOrNull().orEmpty()
