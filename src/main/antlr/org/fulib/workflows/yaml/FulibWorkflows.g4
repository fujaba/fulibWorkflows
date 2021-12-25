grammar FulibWorkflows;

// Non-Terminals

file: workflows+ ;

workflows: workflow NEWLINE eventNote* ;

eventNote: ( normalNote | extendedNote | page) NEWLINE? ;

workflow: MINUS 'workflow' COLON NAME ;

normalNote: MINUS NORMALNOTEKEY COLON NAME ;

extendedNote: MINUS EXTENDEDNOTEKEY COLON NAME NEWLINE attribute* ;

page: MINUS 'page' LISTCOLON NEWLINE pageList ;

attribute: INDENTATION NAME COLON value NEWLINE? ;

value: NAME | NUMBER | LIST;

pageList: pageName NEWLINE element* ;

pageName: INDENTATION MINUS 'pageName' COLON NAME ;

element: INDENTATION MINUS ELEMENTKEY COLON NAME NEWLINE ;

// Terminals
NORMALNOTEKEY: 'externalSystem' | 'service' | 'command' | 'policy' | 'user' | 'problem' ;

EXTENDEDNOTEKEY:  'event' | 'data' ;

ELEMENTKEY: 'text' | 'input' | 'password' | 'button' ;

NAME: ([A-Za-zäÄöÖüÜß] [0-9]* [-/_,.']* [ ]* [0-9]* [-/_,.'>]* [ ]*)+ ;

MINUS: '- ' ;

COLON: ': ' ;

LISTCOLON: ':' ;

KEY: [A-Za-z.]+ ;

NEWLINE: [\r\n]+ | [\n]+ ;

LIST: '[' (.)*? ']' ;

NUMBER: [0-9]+ ;

INDENTATION: [\t]+ | [ ]+ ;
