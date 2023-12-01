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

    @Suppress("CyclomaticComplexMethod") // the method is complex for performance reasons
    private fun tokenize(text: CharSequence) = buildList {
        var curPos = 0
        @Suppress("LoopWithTooManyJumpStatements") // the loop is complex for performance reasons
        while (curPos < text.length) {
            when (text[curPos]) {
                '(' -> add(Token(LEFT_PAREN, "("))
                ')' -> add(Token(RIGHT_PAREN, ")"))
                '^' -> add(Token(CIRCUMFLEX, "^"))
                '*' -> add(Token(ASTERISK, "*"))
                '/' -> add(Token(SLASH, "/"))
                '+' -> add(Token(PLUS, "+"))
                '-' -> add(Token(MINUS, "-"))
                '=' -> add(Token(EQUALS, "="))

                '<' -> if (curPos + 1 < text.length && text[curPos + 1] == '=') {
                    add(Token(LESS_EQUALS, "<="))
                    ++curPos
                } else add(Token(LESS_THAN, "<"))

                '>' -> if (curPos + 1 < text.length && text[curPos + 1] == '=') {
                    add(Token(GREATER_EQUALS, ">="))
                    ++curPos
                } else add(Token(GREATER_THAN, ">"))

                in '0'..'9' -> {
                    val start = curPos
                    do {
                        ++curPos
                    } while (curPos < text.length && text[curPos] in '0'..'9')
                    add(Token(NUMBER, text.subSequence(start, curPos)))
                    continue
                }

                in '\u0001'..'\u0020' -> {
                    do {
                        ++curPos
                    } while (curPos < text.length && text[curPos] in '\u0001'..'\u0020')
                    continue
                }

                else -> throw ParsingException("Unrecognizable token sequence at position $curPos: ${text[curPos]}")
            }
            ++curPos
        }
        add(eoe())
    }
}
