## fulibWorkflows v0.1.0

- initial release

## fulibWorkflows v0.2.0

- Add simulations
- Get proper types from values

## fulibWorkflows v0.3.0

### General
#### Complete rework of fulibWorkflows
- Parsing *.es.yaml content via ANTLR4 Grammar
- Removed Java code generation
- Optimized fulibWorkflows for the corresponding web-editor

#### Events
- Removed the following events:
  - board
  - subprocess
  - boundedContext
  - broker
  - class
  - query
- Renamed the following events:
  - action -> user
  - page/label -> page/text
- Added the following event:
  - problem

#### Generation
- Rework of html page generation
- Added fxml page generation
- Rework of objectdiagram generation
- Rework of classdiagram generation

## fulibWorkflows v0.3.1

### Fixes
- Fixed fatal error for loading of String templates in a jar
