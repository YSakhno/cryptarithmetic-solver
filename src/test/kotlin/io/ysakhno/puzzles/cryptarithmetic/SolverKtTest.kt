package io.ysakhno.puzzles.cryptarithmetic

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.WithDataTestName
import io.kotest.datatest.withData
import io.kotest.matchers.sequences.shouldContainAllInAnyOrder
import io.kotest.matchers.sequences.shouldContainExactly
import io.kotest.matchers.shouldBe

/** Specifies a puzzle and corresponding set of exhaustive solutions for use in data-driven tests. */
private data class Solution(val puzzle: String, val formulas: List<String>) : WithDataTestName {

    /** Specifies the name of the test represented by this class. */
    override fun dataTestName() = "for '$puzzle'"
}

/** A convenience function that creates a new instance of [Solution] based on supplied arguments. */
private fun solution(puzzle: String, formula: String, vararg moreFormulas: String) =
    Solution(puzzle, listOf(*moreFormulas) + formula)

/**
 * Unit tests for the [solve] functions.
 *
 * @author Yuri Sakhno
 */
class SolverKtTest : FunSpec({
    context("multiple solutions") {
        withData(
            solution("X + X = X * X", "0 + 0 = 0 * 0", "2 + 2 = 2 * 2"),
            solution("A^2 + B^2 = C^2", "3^2 + 4^2 = 5^2", "4^2 + 3^2 = 5^2"),
            solution(
                "A^2 + BE^2 = BY^2",
                "5^2 + 12^2 = 13^2",
                "7^2 + 24^2 = 25^2",
                "8^2 + 15^2 = 17^2",
                "9^2 + 12^2 = 15^2",
                "9^2 + 40^2 = 41^2",
            ),
            solution("ATOM = (A + TO + M)^2", "1296 = (1 + 29 + 6)^2", "6724 = (6 + 72 + 4)^2"),
            solution("I+I = ME", "5+5 = 10", "6+6 = 12", "7+7 = 14", "8+8 = 16", "9+9 = 18"),
            solution(
                "A + B + C = A * B * C",
                "1 + 2 + 3 = 1 * 2 * 3",
                "1 + 3 + 2 = 1 * 3 * 2",
                "2 + 1 + 3 = 2 * 1 * 3",
                "2 + 3 + 1 = 2 * 3 * 1",
                "3 + 1 + 2 = 3 * 1 * 2",
                "3 + 2 + 1 = 3 * 2 * 1",
            ),
            solution("ODD + ODD = EVEN", "655 + 655 = 1310", "855 + 855 = 1710"),
            solution("USE + LESS = SONNY", "517 + 9711 = 10228", "715 + 9511 = 10226", "814 + 9411 = 10225"),
            solution(
                "TWO + TWO = FOUR",
                "734 + 734 = 1468",
                "765 + 765 = 1530",
                "836 + 836 = 1672",
                "846 + 846 = 1692",
                "867 + 867 = 1734",
                "928 + 928 = 1856",
                "938 + 938 = 1876",
            ),
        ) { spec ->
            val solutions = solve(spec.puzzle)
            solutions shouldContainAllInAnyOrder spec.formulas.asSequence()
        }
    }
    context("single solution") {
        withData(
            solution("I + BB = ILL", "1 + 99 = 100"),
            solution("TO + GO = OUT", "21 + 81 = 102"),
            solution("USE + LESS = KIDDY", "876 + 9677 = 10553"),
            solution("BASE + BALL = GAMES", "7483 + 7455 = 14938"),
            solution("SEND + MORE = MONEY", "9567 + 1085 = 10652"),
            solution("NO + GUN + NO = HUNT", "87 + 908 + 87 = 1082"),
            solution("CROSS + ROADS = DANGER", "96233 + 62513 = 158746"),
            solution("GREEN + ORANGE = COLORS", "83446 + 135684 = 219130"),
            solution("ADAM + AND + EVE = MOVED", "8581 + 875 + 939 = 10395"),
            solution("FORTY + TEN + TEN = SIXTY", "29786 + 850 + 850 = 31486"),
            solution("TAURUS + PISCES = SCORPIO", "859091 + 461371 = 1320462"),
            solution("HEAR + THOSE + THREE = CHEERS", "2785 + 62397 + 62577 = 127759"),
            solution("SIX + SIX + SIX = NINE + NINE", "942 + 942 + 942 = 1413 + 1413"),
            solution("LOTS + SOLD + TO + OLD = FOOLS", "5478 + 8453 + 74 + 453 = 14458"),
            solution("OLD + SALT + TOLD + TALL = TALES", "394 + 8091 + 1394 + 1099 = 10978"),
            solution("BILL + WILLIAM + MONICA = CLINTON", "9600 + 1600634 + 457623 = 2067857"),
            solution("CEZANNE + MANET + MATISSE = ARTISTS", "4385003 + 15036 + 1567223 = 5967262"),
            solution("FIVE + FIVE + NINE + ELEVEN = THIRTY", "4027 + 4027 + 5057 + 797275 = 810386"),
            solution("NINE + SEVEN + SEVEN + SEVEN = THIRTY", "3239 + 49793 + 49793 + 49793 = 152618"),
            solution("THREE + THREE + THREE + ELEVEN = TWENTY", "73544 + 73544 + 73544 + 494046 = 714678"),
            solution("II^Z = IZI", "11^2 = 121"),
            solution("GO * ON = TROT", "27 * 73 = 1971"),
            solution("G * G - E = EE", "6 * 6 - 3 = 33"),
            solution("LINDON * B = JOHNSON", "570140 * 6 = 3420840"),
            solution("O+CX*Z*C*Z*CX+O=COZY", "0+16*2*1*2*16+0=1024"),
            solution("TAXI*2 - (T^3 + TX^3) = I^3 + TY^3", "1729*2 - (1^3 + 12^3) = 9^3 + 10^3"),
            solution("N^3 + RX^3 = R^3 + RM^3", "9^3 + 10^3 = 1^3 + 12^3"),
            solution("ATOM = (A + TO + M)^T", "1296 = (1 + 29 + 6)^2"),
            solution("ATOM = (A + TO + M)^O", "6724 = (6 + 72 + 4)^2"),
            solution("O^O^P = OMG", "2^2^3 = 256"),
            solution("S^LO = LOST", "2^10 = 1024"),
            solution("X / X = X", "1 / 1 = 1"),
            solution("ABCDE / 4 = EDCBA", "87912 / 4 = 21978"),
        ) { spec ->
            val solution = solve(spec.puzzle)
            solution shouldContainExactly spec.formulas.asSequence()
        }
    }
    test("longest puzzle") {
        val puzzle = """
            SO+MANY+MORE+MEN+SEEM+TO+SAY+THAT+
            THEY+MAY+SOON+TRY+TO+STAY+AT+HOME+
            SO+AS+TO+SEE+OR+HEAR+THE+SAME+ONE+
            MAN+TRY+TO+MEET+THE+TEAM+ON+THE+
            MOON+AS+HE+HAS+AT+THE+OTHER+TEN
            =TESTS
        """.trimIndent().replace("\\s+", "")
        val solution = solveFirst(puzzle)

        solution shouldBe """
            31+2764+2180+206+3002+91+374+9579+
            9504+274+3116+984+91+3974+79+5120+
            31+73+91+300+18+5078+950+3720+160+
            276+984+91+2009+950+9072+16+950+
            2116+73+50+573+79+950+19508+906
            =90393
        """.trimIndent().replace("\\s+", "")
    }
})
