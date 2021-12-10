grammar FulibWorkflows;

// fulibWorkflows parser rules

list: workflow (eventNote)* ;

eventNote: externalSystem | service | command | event | policy | user | classDef | data | page | problem ;

workflow: MINUS 'workflow' COLON NAME ;

externalSystem: MINUS 'externalSystem' COLON NAME ;

service: MINUS 'service' COLON NAME ;

command: MINUS 'command' COLON NAME ;

event: MINUS 'event' COLON NAME attributes;

policy: MINUS 'policy' COLON NAME ;

user: MINUS 'user' COLON NAME ;

classDef: MINUS 'class' COLON NAME attributes;

data: MINUS 'data' COLON NAME attributes;

page: MINUS 'page' COLON NAME pageList;

problem: MINUS 'problem' COLON NAME ;

attributes: INDENT KEY COLON value;

value: NAME | NUMBER;

pageList: ;

// Atomar
NAME: [A-Za-z ]+ ;

MINUS: '- ' ;

COLON: ': ' ;

KEY: [A-Za-z]+ ;

INDENT: [ \t]+ ;

NUMBER: [0-9]+ ;