package io.ysakhno.puzzles.cryptarithmetic.parser

import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrowExactly
import io.kotest.assertions.withClue
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.Shrinker
import io.kotest.property.arbitrary.Codepoint
import io.kotest.property.arbitrary.arbitrary
import io.kotest.property.arbitrary.enum
import io.kotest.property.arbitrary.filter
import io.kotest.property.arbitrary.filterNot
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.nonNegativeInt
import io.kotest.property.arbitrary.of
import io.kotest.property.arbitrary.string
import io.kotest.property.arbitrary.withEdgecases
import io.kotest.property.checkAll
import io.ysakhno.puzzles.cryptarithmetic.parser.TokenType.ASTERISK
import io.ysakhno.puzzles.cryptarithmetic.parser.TokenType.CIRCUMFLEX
import io.ysakhno.puzzles.cryptarithmetic.parser.TokenType.EOE
import io.ysakhno.puzzles.cryptarithmetic.parser.TokenType.EQUALS
import io.ysakhno.puzzles.cryptarithmetic.parser.TokenType.GREATER_EQUALS
import io.ysakhno.puzzles.cryptarithmetic.parser.TokenType.GREATER_THAN
import io.ysakhno.puzzles.cryptarithmetic.parser.TokenType.LEFT_PAREN
import io.ysakhno.puzzles.cryptarithmetic.parser.TokenType.LESS_EQUALS
import io.ysakhno.puzzles.cryptarithmetic.parser.TokenType.LESS_THAN
import io.ysakhno.puzzles.cryptarithmetic.parser.TokenType.MINUS
import io.ysakhno.puzzles.cryptarithmetic.parser.TokenType.NUMBER
import io.ysakhno.puzzles.cryptarithmetic.parser.TokenType.PLUS
import io.ysakhno.puzzles.cryptarithmetic.parser.TokenType.RIGHT_PAREN
import io.ysakhno.puzzles.cryptarithmetic.parser.TokenType.SLASH
import io.ysakhno.puzzles.cryptarithmetic.parser.TokenType.VARIABLE
import kotlin.random.nextInt

/**
 * Similar to the [Token] class, but more suitable for generating test data.
 *
 * @property type the type of the current token.
 * @property text the text associated with the token.
 * @property isWhitespace whether the current token is nothing but whitespaces.
 * @author Yuri Sakhno
 */
private data class TestToken(val type: TokenType?, val text: CharSequence, val isWhitespace: Boolean) {

    init {
        require(!isWhitespace || type == null) { "Whitespace tokens must not have a type" }
        require(isWhitespace || type != null) { "Non-whitespace tokens must have a type" }
    }

    /** Constructs a non-whitespace token with a token [type] and [text]. */
    constructor(type: TokenType, text: CharSequence) : this(type, text, false)

    /** Constructs a whitespace token with associated [text]. */
    constructor(text: CharSequence) : this(null, text, true)
}

/**
 * Unit tests for the [Tokenizer] class.
 *
 * @author Yuri Sakhno
 */
class TokenizerTest : FunSpec({
    test("should tokenize the input text correctly") {
        checkAll(Arb.tokens()) { testTokens ->
            val expectedTokenTypes = testTokens.mapNotNull(TestToken::type) + EOE
            val expectedLexemes = testTokens.filterNot(TestToken::isWhitespace).map(TestToken::text)
            val text = testTokens.joinToString(separator = "", transform = TestToken::text)
            val parsedTokens = Tokenizer(text).tokens
            val tokenTypes = parsedTokens.map(Token::type)
            val lexemes = parsedTokens.filter { it.type != EOE }.map { text.subSequence(it.range) }

            tokenTypes shouldBe expectedTokenTypes
            lexemes shouldBe expectedLexemes
        }
    }
    test("should fail tokenizing when unknown characters are present") {
        val unknownCharacters = ('!'..'~') - ('0'..'9') - ('A'..'Z') - "()^*/+-<=>".toSet()
        val textGen = Arb.tokens().map { it.joinToString(separator = "", transform = TestToken::text) }

        checkAll(textGen, Arb.nonNegativeInt(), Arb.of(unknownCharacters)) { text, pos, unkCh ->
            val insertionPos = pos % (text.length + 1)
            val brokenText = text.substring(0, insertionPos) + unkCh + text.substring(insertionPos)

            val exception = shouldThrowExactly<ParsingException> { Tokenizer(brokenText) }

            assertSoftly(exception) {
                message shouldBe "Unrecognizable token sequence at position $insertionPos: $unkCh"
                withClue("not caused by other exception") { cause.shouldBeNull() }
            }
        }
    }
})

/** Convenience property to convert a character to a [codepoint][Codepoint]. */
private val Char.codepoint get() = Codepoint(code)

/** Returns an [Arb] that generates ASCII control character codepoints, and space (1-32). */
private fun Codepoint.Companion.controlAscii() =
    Arb.of((1..32).map(::Codepoint)).withEdgecases('\t'.codepoint, '\n'.codepoint, '\r'.codepoint, ' '.codepoint)

/**
 * Returns an [Arb] that generates decimal digits (`0`-`9`) and uppercase Latin letters (`A`-`Z`) codepoints suitable
 * for use in variables of puzzles.
 */
private fun Codepoint.Companion.variableAscii() =
    Arb.of((('0'.code..'9'.code) + ('A'.code..'Z'.code)).map(::Codepoint)).withEdgecases('0'.codepoint, 'A'.codepoint)

/**
 * Returns an [Arb] that generates decimal digits (`0`-`9`) codepoints suitable to represent unhidden values of puzzles.
 */
private fun Codepoint.Companion.decimalDigits() =
    Arb.of(('0'.code..'9'.code).map(::Codepoint)).withEdgecases('0'.codepoint)

/** Returns an [Arb] that generates strings consisting solely of whitespace characters. */
private fun Arb.Companion.whitespace(range: IntRange = 1..12) =
    Arb.string(range = range, codepoints = Codepoint.controlAscii())

/**
 * Returns an [Arb] that generates strings that can be used as variable names in expressions.
 *
 * Couple of points worth mentioning:
 *   * this generator will never return a string consisting solely of digits (as such strings are effectively not
 *     variables by definition);
 *   * this generator will never return a string that starts with a `0`.
 */
private fun Arb.Companion.variable(range: IntRange = 1..12) =
    Arb.string(range = range, codepoints = Codepoint.variableAscii())
        .filterNot { name -> name.all { it in '0'..'9' } }
        .filterNot { it[0] == '0' }

/**
 * Returns an [Arb] that generates strings that can be used as decimal numbers in expressions.
 *
 * This generator will never return a string that starts with a zero (`0`), unless the string is actually a lone
 * character `0` (i.e. literally the string `"0"`).
 */
private fun Arb.Companion.decimalNumber(range: IntRange = 1..12) =
    Arb.string(range = range, codepoints = Codepoint.decimalDigits())
        .filterNot { num -> num.length > 1 && num[0] == '0' }

/** Mapping of 'simple' token types (that always have the same string representation) to their string representation. */
private val TOKEN_TYPE_TO_STRING_MAPPING = mapOf(
    LEFT_PAREN to "(",
    RIGHT_PAREN to ")",
    CIRCUMFLEX to "^",
    ASTERISK to "*",
    SLASH to "/",
    PLUS to "+",
    MINUS to "-",
    LESS_THAN to "<",
    EQUALS to "=",
    GREATER_THAN to ">",
    LESS_EQUALS to "<=",
    GREATER_EQUALS to ">=",
)

/** Returns an [Arb] that generates tokens or whitespace. */
private fun Arb.Companion.token(whitespaceProbability: Double = 0.2): Arb<TestToken> {
    require(whitespaceProbability in 0.0..1.0) { "Probability of whitespaces must be in the range 0..1, inclusive" }

    val tokenTypeGen = Arb.enum<TokenType>().filter { it != EOE }
    val whitespaceGen = Arb.whitespace()
    val variableGen = Arb.variable()
    val numberGen = Arb.decimalNumber()

    return arbitrary(
        fn = { rs ->
            val isWhitespace = (rs.random.nextDouble(0.0, 1.0) < whitespaceProbability)

            if (!isWhitespace) {
                val tokenType = tokenTypeGen.generate(rs).iterator().next().value
                val text = TOKEN_TYPE_TO_STRING_MAPPING[tokenType] ?: when (tokenType) {
                    NUMBER -> numberGen.bind()
                    VARIABLE -> variableGen.bind()
                    else -> ""
                }
                TestToken(tokenType, text)
            } else TestToken(whitespaceGen.bind())
        },
    )
}

/** Determines whether this token type is a `NUMBER` or a `VARIABLE`, or not. */
private val TokenType?.isNumberOrVariable get() = (this == VARIABLE || this == NUMBER)

/**
 * Determines whether this token type can appear in the text immediately after the token type specified by [prev]
 * without any whitespace appearing in between (or in the case if both token types are whitespaces, whether it is
 * sensible to emit two such tokens in a row).
 *
 * Some token types cannot appear immediately after another (without at least a single whitespace character separating
 * them), because in that case it will be impossible to distinguish them from one other in the text, and the tokenizer
 * will join the two tokens into one token (possibly of a different type).
 */
private fun TokenType?.canAppearAfter(prev: TokenType?): Boolean {
    if (isNumberOrVariable && prev.isNumberOrVariable) return false
    if (this == EQUALS && (prev == LESS_THAN || prev == GREATER_THAN)) return false
    return true
}

/**
 * A [Shrinker] for lists of tokens.
 *
 * The candidates can be:
 *   * an empty list
 *   * a list with the head (first) element removed
 *   * a list with the tail (last) element removed
 *   * the first half of the original list
 *   * the last half of the original list
 *   * just the first element of the original list
 *   * just the last element of the original list
 *
 * Each shrinking candidate is checked to be within the range of desired sizes (not smaller than [minSize]).
 *
 * @author Yuri Sakhno
 */
private class TokensShrinker(private val minSize: Int) : Shrinker<List<TestToken>> {

    /** Returns the "next level" of shrinks for the given value, or empty list if a "base case" has been reached. */
    override fun shrink(value: List<TestToken>): List<List<TestToken>> = when {
        value.isEmpty() -> emptyList()
        value.size == 1 -> if (minSize <= 0) listOf(emptyList()) else emptyList()
        else -> listOf(
            value.drop(1),
            value.dropLast(1),
            value.take(value.size / 2),
            value.takeLast(value.size / 2),
            value.take(1), // just the first element
            value.takeLast(1), // just the last element
        ).filter { it.size >= minSize }.distinct()
    }
}

/** Returns an [Arb] that generates lists of tokens (possibly intermingled with whitespaces). */
private fun Arb.Companion.tokens(range: IntRange = 0..100, whitespaceProbability: Double = 0.2): Arb<List<TestToken>> {
    val tokenGen = Arb.token(whitespaceProbability)

    return arbitrary(shrinker = TokensShrinker(range.first)) { rs ->
        val targetSize = rs.random.nextInt(range)
        val tokenIter = tokenGen.generate(rs).iterator()

        buildList(targetSize) {
            var prevTokenType: TokenType? = null
            var isPrevWhitespace = false

            while (size < targetSize) {
                val token = tokenIter.next().value
                val (tokenType, _, isWhitespace) = token

                if ((isWhitespace && isPrevWhitespace) || !tokenType.canAppearAfter(prevTokenType)) continue

                add(token)
                prevTokenType = tokenType
                isPrevWhitespace = isWhitespace
            }
        }
    }
}
