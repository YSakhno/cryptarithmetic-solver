package io.ysakhno.puzzles.cryptarithmetic

import java.nio.charset.StandardCharsets.UTF_8
import java.util.Scanner

/** A [Regex] used to determine if the line of text contains only comment (and should be skipped). */
private val COMMENT_REGEX = "^\\s*#".toRegex()

/** Determines if this character sequence is either blank or a comment, and returns `true` if it is. */
private fun CharSequence?.isCommentOrBlank() = isNullOrBlank() || COMMENT_REGEX.containsMatchIn(this)

/** The padding to align solutions with the puzzle above in the output. */
private const val OUTPUT_PADDING = 15

/** Solves a single puzzle reporting the solution(s) to standard output (console or terminal). */
private fun solveAndReport(puzzle: String) {
    val solutions = solve(puzzle)
    var hasNoSolutions = true

    solutions.onEachIndexed { idx, _ ->
        if (idx == 0) {
            println("Solution(s) for '$puzzle':")
            hasNoSolutions = false
        }
    }.forEachIndexed { idx, solution ->
        print("    #${idx + 1} ".padEnd(OUTPUT_PADDING, '.'))
        print("  ")
        println(solution)
    }

    if (hasNoSolutions) {
        println("Puzzle '$puzzle' has no solutions")
    }
}

/** Application's main entry point. */
fun main() {
    println("Cryptarithmetic puzzle solver by Yuri Sakhno")
    println("Input the puzzles to solve, one per line:")
    println()

    Scanner(System.`in`, UTF_8).use { scanner ->
        while (scanner.hasNextLine()) {
            scanner.nextLine().takeUnless(CharSequence::isCommentOrBlank)?.let(::solveAndReport)
        }
    }
}
