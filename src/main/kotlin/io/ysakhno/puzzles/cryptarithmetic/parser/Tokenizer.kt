package io.ysakhno.puzzles.cryptarithmetic.parser

import io.ysakhno.puzzles.cryptarithmetic.parser.TokenType.ASTERISK
import io.ysakhno.puzzles.cryptarithmetic.parser.TokenType.CIRCUMFLEX
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
import io.ysakhno.puzzles.cryptarithmetic.parser.TokenType.WHITESPACE
import org.intellij.lang.annotations.Language

private data class TokenSpec(val regex: Regex, val tokenType: TokenType)

@Suppress("FunctionMinLength") // The name of this function is a deliberate play on the similarity to word 'to'
private infix fun String.ts(type: TokenType) = TokenSpec(toRegex(), type)

@Language("RegExp")
private val TOKEN_SPECS = listOf(
    // Spaces, tabs, line separators, etc.
    """[\001-\040]+""" ts WHITESPACE,

    // Syntactic elements
    "\\(" ts LEFT_PAREN,
    "\\)" ts RIGHT_PAREN,

    ">=" ts GREATER_EQUALS,
    "<=" ts LESS_EQUALS,

    // Single-character operators
    "\\^" ts CIRCUMFLEX,
    "\\*" ts ASTERISK,
    "/" ts SLASH,
    "\\+" ts PLUS,
    "-" ts MINUS,
    "<" ts LESS_THAN,
    "=" ts EQUALS,
    ">" ts GREATER_THAN,

    "(?:0|[1-9][0-9]*)\\b" ts NUMBER,
)

/**
 * Tokenizes the expression and stores the result (list of tokens) in the [tokens] property.
 *
 * @param expression the expression to tokenize (break up into individual tokens).
 * @constructor Constructs the tokenizer with the specified expression and performs tokenization.
 * @author Yuri Sakhno
 */
class Tokenizer(expression: CharSequence) {

    /** The list of tokens tokenized from the original expression. */
    val tokens: List<Token> = tokenize(expression)

    private fun matchTokenRegexp(text: CharSequence, curPos: Int) = TOKEN_SPECS.firstNotNullOfOrNull { tokenSpec ->
        tokenSpec.regex.matchAt(text, curPos)?.let { it.value to tokenSpec.tokenType }
    } ?: throw ParsingException("Unrecognizable token sequence at position $curPos")

    private fun tokenize(text: CharSequence) = buildList {
        var curPos = 0
        while (curPos < text.length) {
            val (value, tokenType) = matchTokenRegexp(text, curPos)
            if (!tokenType.isSkipped) add(Token(tokenType, value))
            curPos += value.length
        }
        add(eoe())
    }
}
