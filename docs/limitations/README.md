# Limitations
Due to the current grammar there are limitations to the syntax.

## General
A workflow description always has to have an EOF.
Between every event there has to be an empty line.

## Key limitations
A key is only allowed to consist of letters A-Z and a dot.
There are no spaces accepted. It has to be one word.

## Value limitations
Values can either be a string or a number.

### Number
Only positive integers are allowed.

### String
A string has to begin with a letter. (A-Z, Umlauts, ß).
After that a number can follow.
Allowed separators for words are Spaces and the following special characters: [-/_,.']

## Blocked keys
Special strings that are used as identifiers in the grammar are not allowed to use as keys in event and data notes.
These consist of the following strings:

- workflow, page, externalSystem, service, command, policy, user, problem 
- event, data 
- name, text, input, password, button 
