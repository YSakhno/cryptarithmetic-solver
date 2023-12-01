package io.ysakhno.puzzles.cryptarithmetic.parser

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
                '(' -> add(Token(LEFT_PAREN, curPos..curPos))
                ')' -> add(Token(RIGHT_PAREN, curPos..curPos))
                '^' -> add(Token(CIRCUMFLEX, curPos..curPos))
                '*' -> add(Token(ASTERISK, curPos..curPos))
                '/' -> add(Token(SLASH, curPos..curPos))
                '+' -> add(Token(PLUS, curPos..curPos))
                '-' -> add(Token(MINUS, curPos..curPos))
                '=' -> add(Token(EQUALS, curPos..curPos))

                '<' -> if (curPos + 1 < text.length && text[curPos + 1] == '=') {
                    add(Token(LESS_EQUALS, curPos..curPos + 1))
                    ++curPos
                } else add(Token(LESS_THAN, curPos..curPos))

                '>' -> if (curPos + 1 < text.length && text[curPos + 1] == '=') {
                    add(Token(GREATER_EQUALS, curPos..curPos + 1))
                    ++curPos
                } else add(Token(GREATER_THAN, curPos..curPos))

                in '0'..'9' -> {
                    val start = curPos
                    do {
                        ++curPos
                    } while (curPos < text.length && text[curPos] in '0'..'9')
                    add(Token(NUMBER, start..<curPos))
                    continue
                }

                in 'A'..'Z' -> {
                    val start = curPos
                    do {
                        ++curPos
                    } while (curPos < text.length && text[curPos] in 'A'..'Z')
                    add(Token(VARIABLE, start..<curPos))
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
        add(Token(EOE, text.length..text.length))
    }
}
