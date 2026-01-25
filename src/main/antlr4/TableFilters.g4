grammar TableFilters;

@header {
package com.github.giamgiammi.StructuredFileViewer.filters.generated;
}

/*********************************************************************
 *  ANTLR4 GRAMMAR FOR A LOGICAL FILTER LANGUAGE
 *
 *  Supported features:
 *  - Logical expressions with NOT / AND / OR (case-insensitive)
 *      Precedence: NOT (highest) → AND → OR (lowest)
 *  - Parentheses to control precedence
 *  - Comparisons of the form: column OPERATOR value
 *  - Column identifiers:
 *      * simple identifier          → columnName
 *      * double-quoted identifier  → "column name"
 *      * positional index           → $0, $1, ...
 *  - Values are always single-quoted strings with backslash escapes
 *  - Supported comparison operators:
 *      =      (equals)
 *      <>     (not equals)
 *      LIKE
 *      ILIKE
 *      REGEX
 *  - REGEX follows Java Pattern rules (not syntactically validated here,
 *    they are only treated as strings)
 *
 *  This grammar is intended as an extensible base.
 *********************************************************************/

// ==========================
//            PARSER
// ==========================

// Main entry point
// A full expression is an OR-expression followed by end of file
expr
    : orExpr EOF
    ;

// OR has the lowest precedence
orExpr
    : andExpr (OR andExpr)*
    ;

// AND has higher precedence than OR
andExpr
    : notExpr (AND notExpr)*
    ;

// NOT has the highest precedence
// It is a unary prefix operator: NOT <expression>
notExpr
    : NOT notExpr          // example: NOT NOT a = 'x'
    | primary
    ;

// Atomic logical unit:
//  - a parenthesized sub-expression
//  - or a simple comparison
primary
    : '(' expr ')'
    | comparison
    ;

// Simple comparison: <column> <operator> <value>
comparison
    : column op value
    ;

// Column reference:
//  - simple identifier (no $)
//  - double-quoted identifier (may contain any escaped character)
//  - positional reference $n
column
    : IDENT
    | QUOTED_IDENT
    | DOLLAR_INDEX
    ;

// Compared value: always a single-quoted string
value
    : STRING
    ;

// Allowed comparison operators
op
    : EQ
    | NEQ          // <>
    | LIKE
    | ILIKE
    | REGEX
    ;


// ==========================
//             LEXER
// ==========================

// --- Logical keywords (case-insensitive) ---
// Character classes are used to make keywords case-insensitive
AND   : [Aa][Nn][Dd];
OR    : [Oo][Rr];
NOT   : [Nn][Oo][Tt];

// --- Textual comparison operators (case-insensitive) ---
LIKE  : [Ll][Ii][Kk][Ee];
ILIKE : [Ii][Ll][Ii][Kk][Ee];
REGEX : [Rr][Ee][Gg][Ee][Xx];

// Symbolic comparison operators
EQ  : '=';     // equals
NEQ : '<>';    // not equals

// --- Positional column reference: $n with n >= 0 ---
// Accepts one or more digits after $
DOLLAR_INDEX : '$' DIGIT+;

// --- Simple column identifier ---
// Must not start with a digit and must not contain $
IDENT : [a-zA-Z_][a-zA-Z0-9_]*;

// --- Double-quoted column identifier ---
// Allows any character except unescaped " and \
// Escape is done with backslash: \" , \\ , etc.
QUOTED_IDENT
    : '"' ( '\\' . | ~["\\] )* '"'
    ;

// --- Single-quoted string literal (value) ---
// Backslash escapes are supported here as well
// Valid examples: 'abc\'def' , 'a\\b'
STRING
    : '\'' ( '\\' . | ~['\\] )* '\''
    ;

// --- Fragments ---
fragment DIGIT : [0-9];

// --- Whitespace (ignored) ---
WS : [ \t\r\n]+ -> skip;
