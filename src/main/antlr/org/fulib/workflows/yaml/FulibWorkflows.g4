grammar FulibWorkflows;

// fulibWorkflows parser rules

list: workflow NEWLINE eventNote*;

eventNote: ( normalNote | extendedNote | page) NEWLINE? ;

workflow: MINUS 'workflow' COLON NAME ;

normalNote: MINUS NORMALNOTEKEY COLON NAME ;

extendedNote: MINUS EXTENDEDNOTEKEY COLON NAME NEWLINE attribute+ ;

page: MINUS 'page' LISTCOLON NEWLINE pageList;

attribute: SPACES NAME COLON value NEWLINE?;

value: NAME | NUMBER;

pageList: pageName NEWLINE element*;

pageName: SPACES MINUS 'name' COLON NAME;

element: SPACES MINUS ELEMENTKEY COLON NAME NEWLINE;

// Atomar
NORMALNOTEKEY: 'externalSystem' | 'service' | 'command' | 'policy' | 'user' | 'problem' ;

EXTENDEDNOTEKEY:  'event' | 'class' | 'data' ;

ELEMENTKEY: 'text' | 'input' | 'password' | 'button' ;

NAME: ([A-Za-z]+ [ ]*)+ ;

MINUS: '- ' ;

COLON: ': ' ;

LISTCOLON: ':';

KEY: [A-Za-z]+ ;

NEWLINE: [\r\n]+ | [\n]+ ;

NUMBER: [0-9]+ ;

SPACES: [ ]+;