# Table Filter Language Documentation

This language provides a SQL-like syntax to filter tabular data. It supports complex logical branching, pattern matching, and flexible column referencing.

## 1. Column Referencing

You can reference columns in three different ways:

- Identifiers (IDENT): Use the column name directly if it starts with a letter or underscore and contains only alphanumeric characters.
- Quoted Identifiers (QUOTED_IDENT): If a column name contains spaces or special characters, wrap it in double quotes (e.g., "Total Cost").
- Positional Indices (DOLLAR_INDEX): Reference a column by its position using $n.
  - \$1, \$2, ...: Refers to the first column, second column, etc.
  - \$0: A special index representing the row number (starting at 1).

## 2. Values and Literals
- Strings: All values must be wrapped in single quotes (e.g., 'active').
  - **All values are considered strings**, numeric types are not handled at this time
- Escaping: To include a single quote or backslash in a value, use a backslash (e.g., 'O\'Reilly' or 'C:\\Docs').
- Nulls: The keyword NULL is used to check for missing data
  - Note: Most of the time empty values consist of an empty string (i.e. '')

## 3. Comparison Operators

The language supports standard comparisons between a column and a value, or between two different columns.

| Operator | Description               | Logic Note                           |
|----------|---------------------------|--------------------------------------|
| =	       | Equals                    | Direct equality                      |
| <>	      | Not Equals                | Inequality                           |
| >, >=    | 	Greater than (or equal)  | String-based comparison              |
| <, <=    | Less than (or equal)      | String-based comparison              |
| LIKE     | Case-sensitive contains   | Checks if value exists within column |
| ILIKE    | Case-insensitive contains | Case-insensitive substring match     |
| REGEX    | Regular Expression        | Matches Java Pattern rules           |

Format:
- The only combination allowed are:
  - column *op* value
  - column *op* column
- Other combinations, (ex. `value op column`) are not supported
- All values are treated as strings, even `$0`

## 4. Logical Operators & Precedence

Filters can be combined using logical operators. They are evaluated in the following order of precedence:
- Parentheses ( ): Used to group expressions and override default order.
- NOT: Inverts the result of an expression.
- AND: Returns true if both conditions are met.
- OR: Returns true if at least one condition is met.

Example: (Status = 'Open' OR Status = 'Pending') AND NOT "Owner" = NULL.
