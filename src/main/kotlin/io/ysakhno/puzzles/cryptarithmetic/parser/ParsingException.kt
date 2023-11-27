package io.ysakhno.puzzles.cryptarithmetic.parser

/**
 * Signals that an error has been reached unexpectedly while parsing the expression.
 *
 * @param message the detail message that describes this particular exception.
 * @constructor Constructs a new instance of the [ParsingException] with the specified detail message.
 * @author Yuri Sakhno
 */
class ParsingException(message: String) : Exception(message)
