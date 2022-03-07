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

## fulibWorkflows v0.3.2

## Breaking Changes
- Renamed public BoardGenerator methods. New Methods:
  - generateBoardFromFile
  - generateBoardFromString
  - generateAndReturnHTMLsFromFile
  - generateAndReturnHTMLsFromString

## General
- Added JavaDoc to not generated classes

## Generation
- Restyled the generated event storming board
- Add \t for indentation

## Fixes
- Only generate tmp or test directory if needed
- Only generate classdiagram if there is a class in the classmodel
- Add padding for buttons to generated html pages
- Use `id` instead of `fx:id` in the generated fxml files

## fulibWorkflows v0.3.3

## Fixes
- Remove href from <a> html element
- Content of an event storming board is always aligned to the left
- Lower shadow effect on cards

## fulibWorkflows v0.3.4

## General
- Rename name item of pages to pageName
- Add a custom exception for object diagram creation

## Fixes
- JSON-Schema for problem note fixed
- Events are allowed to not have additional attributes

## fulibWorkflows v0.3.5

## General
- Additional special characters allowed
- New additions to pages
- Changed Antlr Grammar
- PageName must be unique in a workflow file

## fulibWorkflows v0.3.6

## General
- Removed Antlr Grammar and Parser
- Used Snakeyaml instead for parsing and building needed data model
- Added possibility to generate for local or web
- Only show last added object (and all correctly connected to that one) in the object diagrams
- Reworked appearance of page notes in the event storming board

## fulibWorkflows v0.4.0

## Breaking Changes
- Renamed the `fill` attribute in page to `value`

## General
- Added possibility to set the generation directory via field in BoardGenerator

## Fixes
- If just using the generationAndReturn method the tmp folder didn't get deleted for objectdiagrams

## fulibWorkflows v0.4.1

## General
- Updated generation templates to bootstrap v5
- Updated gradle to v7.4
- Changed page/board interaction for web generation for usage in fulib.org

## fulibWorkflows v0.4.2

## General
- Added dark mode to generated html files
- Added lint input prior used in web editor
- Accepts list as type for attributes for data notes

## fulibWorkflows v0.4.3

## General
- Added code into html stg for fulib.org theme switch

## fulibWorkflows v0.4.4

## Fixes
- Fix Regex for removing tabs as indentation for yaml parser
