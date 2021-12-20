grammar FulibWorkflows;

// fulibWorkflows parser rules

file: workflows+ ;

workflows: workflow NEWLINE eventNote* ;

eventNote: ( normalNote | extendedNote | page) NEWLINE? ;

workflow: MINUS 'workflow' COLON NAME ;

normalNote: MINUS NORMALNOTEKEY COLON NAME ;

extendedNote: MINUS EXTENDEDNOTEKEY COLON NAME NEWLINE attribute+ ;

page: MINUS 'page' LISTCOLON NEWLINE pageList ;

attribute: SPACES NAME COLON value NEWLINE? ;

value: NAME | NUMBER | LIST;

pageList: pageName NEWLINE element* ;

pageName: SPACES MINUS 'name' COLON NAME ;

element: SPACES MINUS ELEMENTKEY COLON NAME NEWLINE ;

// Atomar
NORMALNOTEKEY: 'externalSystem' | 'service' | 'command' | 'policy' | 'user' | 'problem' ;

EXTENDEDNOTEKEY:  'event' | 'data' ;

ELEMENTKEY: 'text' | 'input' | 'password' | 'button' ;

NAME: ([A-Za-zäÄöÖüÜß] [0-9]* [-/_,.']* [ ]* [0-9]* [-/_,.'>]* [ ]*)+ ;

MINUS: '- ' ;

COLON: ': ' ;

LISTCOLON: ':' ;

KEY: [A-Za-z.]+ ;

NEWLINE: [\r\n]+ | [\n]+ ;

LIST: '[' (.)*? ']';

NUMBER: [0-9]+ ;

SPACES: [ ]+ ;