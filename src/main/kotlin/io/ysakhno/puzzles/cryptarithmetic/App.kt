package io.ysakhno.puzzles.cryptarithmetic

/** The padding to align solutions with the puzzle above in the output. */
private const val OUTPUT_PADDING = 15

/** Application's main entry point. */
fun main() {
    val puzzle = "I + BB = ILL"
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
