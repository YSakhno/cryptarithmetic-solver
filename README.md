# Cryptarithmetic Solver

A cryptarithmetic solver implementation in Kotlin.  Adapted from a Python version, as presented in the Udacity's [Design
of Computer Programs](https://learn.udacity.com/courses/cs212) course.

Bear in mind that the solver does not do any intelligent inference of any kind to solve the puzzles.  Instead, the
solver takes the brute force approach, trying all possible assignments of digits to letters, accepting only the ones
that pass.  The solver does not stop after finding the first passing solution; all combinations are tried exhaustively,
until there are no more combinations to try.  This way, if a puzzle has more than one solution (as is the case with the
puzzle `I+I=ME`, for example), all such solutions will be discovered and printed out.

This application was developed for the purposes of practicing in solving (semi)practical problems and for the
entertainment value (while developing it).  Also, it was kind of interesting to investigate how Kotlin stacks up against
Python in solving algorithmic problems, not only from the language standpoint, but also from the standpoint of available
and ready-to-use implementations of algorithms in standard libraries.


## Building locally

To build the application, all you have to have is a local installation of JDK 11 or later (presumably, any vendor should
work, but if in doubt, [this](https://adoptium.net/temurin/releases/?version=11&package=jdk) is a good place to start).

After you've procured a suitable JDK for your platform, run the following command in the root directory of the project
to build it:

```bash
./gradlew build
```

After the project is built (you see message `BUILD SUCCESSFUL` output in the console), the built artifact is located at
`./build/libs/cryptarithmetic-solver-1.0-SNAPSHOT-all.jar`.  This is a so-called 'fat' (Uber) JAR, which contains all
dependencies needed to run the application (apart from the JDK).  It can be run using the `java` program as outlined in
the section **Running the application** below.


## Running the application

To run the application, you also need the JDK 11 or later (refer to section **Building locally** for a basic info on how
to get one).  Then, issue the following command in the terminal:

```bash
java -jar cryptarithmetic-solver-1.0-SNAPSHOT-all.jar
```

The application reads puzzles from the standard input and prints out solutions to standard output, both of which can be
redirected (to read from a file, for example).


## Supported expressions

Basic arithmetic expressions are supported (addition, subtraction, multiplication, division) as well as exponentiation.

### Limitations

All numbers must be integer values, and should fall in the range -2,147,483,648 to 2,147,483,647, inclusive, at all
stages of the computation.  Division is an integer operation, returning the integer quotient as the answer, without a
remainder.  Exponentiation supports only integers (both as a base and as an exponent), and the exponent cannot be
negative (otherwise the result is undefined).

Specifying negative numbers directly in the expression (as "-_n_") is currently not supported, because there is no
"unary minus" operator.  If negation is needed, it can be achieved using the following pattern: "(0-_n_)" (quotes shown
for the purpose of clarity only, but parentheses are generally required).

### Defining terms

Terms in the puzzles are defined using decimal digits `0` through `9`, or uppercase Latin letters `A` through `Z`, or a
mixture of these.  Each particular letter will have to be substituted by some digit.  When all letters are substituted
by digits, an expression can be evaluated for correctness.  Each letter can correspond only to one digit, and no digit
can be represented by more than one letter in a given expression (if expression includes some explicit (unencoded)
digits, the same digits may still be hidden behind letter(s) in the same expression).

Terms are separated from each other by operators, or by whitespace (although it is a syntax error to have 2 terms appear
in the expression without an operator appearing between them).

### Priority of operators

The 'usual' precedence of operations is the same as could be reasonably expected.  Refer to the table below for more
formal definition.  It lists all supported operators in the order of _decreasing_ precedence (i.e. from highest to
lowest).  Operators having the same number in the **Precedence** column, have the same precedence and are evaluated
according to their associativity.

| Precedence | Operator  | Name / Meaning           | Associativity |
|------------|-----------|--------------------------|---------------|
| 1          | `(`...`)` | Parentheses              | None          |
| 2          | `^`       | Exponentiation           | Right-to-left |
| 3          | `*`       | Multiplication           | Left-to-right |
| 3          | `/`       | Integer division         | Left-to-right |
| 4          | `+`       | Addition                 | Left-to-right |
| 4          | `-`       | Subtraction              | Left-to-right |
| 5          | `=`       | Equal to                 | Left-to-right |
| 5          | `<`       | Less than                | Left-to-right |
| 5          | `<=`      | Less than or equal to    | Left-to-right |
| 5          | `>`       | Greater than             | Left-to-right |
| 5          | `>=`      | Greater than or equal to | Left-to-right |

The comparison operators (equal, less, greater, etc.), when evaluated, return a truth value encoded as an integer, where
`0` means `false` (the condition does not hold), and `1` means `true` (the condition holds).  Consequently, the whole
expression is correct if it evaluates to a non-zero value.
