grammar TestExpr;

stat: expr EOF;

expr
: simpleExpr
| '(' expr ')'
|  expr LOGICAL_OP expr
;
// (a.g == b ) && c > 1L && (b && d && e <= -1.2) && (f == null) && g || true && (c.1."uaoe-_u".c.2424 > 1)



simpleExpr
:INVOKE_CHAIN op=(EQUAL_OP|COMPARISON_OP) right=(NUMBER|TEXT|CHAR)   #literalComparisonOperation
|INVOKE_CHAIN op=(LOGICAL_OP|COMPARISON_OP|EQUAL_OP) INVOKE_CHAIN               #varOperation
|INVOKE_CHAIN EQUAL_OP NULL                                      #nullCheck
|INVOKE_CHAIN LOGICAL_OP BOOLEAN                                    #literalLogicalOperation
;

EQUAL_OP:('=='|'!=');
COMPARISON_OP:('>'|'>='|'<'|'<=');
LOGICAL_OP:('&&'|'||');

NULL: 'null';
NUMBER:([1-9][0-9]*|'0')'L'?
| [1-9][0-9]* '.' [0-9]+
| [0-9] '.' [0-9]+
| '-' NUMBER
;
CHAR: '\'' ~[\\'] '\''
|'\'' '\\\\' '\''
|'\'' '\\'~[\\] '\''
;
TEXT:'""'
| '"' '\\\\'+ '"'
| '"' .*?  ~[\\]('\\\\')+ '"'
| '"' .*? ~[\\] '"'
;

BOOLEAN: 'true'
|'false'
;

//INVOKE_CHAIN:([a-zA-Z][a-zA-Z0-9_]*('["'[0-9a-zA-Z]*'"]'|'['[0-9][0-9]*']')?)('.'[a-zA-Z][a-zA-Z0-9_]*('["'[0-9a-zA-Z]*'"]'|'['[0-9][0-9]*']')?)*;
INVOKE_CHAIN:([a-zA-Z][a-zA-Z0-9_]*)('.'([a-zA-Z][a-zA-Z0-9_]*|'"'[a-zA-Z0-9_-]*'"'|[0-9][0-9]*))*;

WS:[ \t]+ -> skip;