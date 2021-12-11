grammar FulibWorkflows;

// fulibWorkflows parser rules

list: workflow NEWLINE eventNote*;

eventNote: ( normalNote | extendedNote | page) NEWLINE+ ;

workflow: MINUS 'workflow' COLON NAME ;

normalNote: MINUS NORMALNOTEKEY COLON NAME ;

extendedNote: MINUS EXTENDEDNOTEKEY COLON NAME NEWLINE attribute+ ;

page: MINUS 'page' COLON pageList;

attribute: NAME COLON value NEWLINE?;

value: NAME | NUMBER;

pageList: name NEWLINE element*;

name: MINUS 'name' COLON NAME;

element: MINUS ELEMENTKEY COLON NAME NEWLINE;

// Atomar
NORMALNOTEKEY: 'externalSystem' | 'service' | 'command' | 'policy' | 'user' | 'problem' ;

EXTENDEDNOTEKEY:  'event' | 'class' | 'data' ;

ELEMENTKEY: 'text' | 'input' | 'password' | 'button' ;

NAME: [A-Za-z ]+ ;

MINUS: '- ' ;

COLON: ':' [ \n] ;

KEY: [A-Za-z]+ ;

NEWLINE: [\r\n]+ | [\n]+ ;

NUMBER: [0-9]+ ;