# Quickstart Guide
## Workflow files location
- create your *.es.yaml files in src/gen/resources/workflows
- Files can be created top level or in nested directories

## Keyword completion via JSON Schema
### IntelliJ
- Create new JSON Schema Mapping
- Settings/Language & Frameworks/ Schemas and DTDs/JSON Schema Mappings
- Define a name for the mapping (not important)
- Add this url
- https://raw.githubusercontent.com/fujaba/fulibWorkflows/main/schema/fulibWorklows.schema.json
- Select Schema Version 7
- Add Another File path pattern: *.es.yaml

### Virtual Studio Code
- go to settings -> search for json.schemas -> select workspace -> Edit in settings.json
- With this a new file and folder will appear -> .vscode/settings.json
- add the following to the file -> Or create a new entry if there are already json schemas set
```json
{
  "yaml.schemas": {
    "https://raw.githubusercontent.com/fujaba/fulibWorkflows/main/schema/fulibWorklows.schema.json": ["*.es.yaml"]
  }
}
```

## TODO Generation options 
1. Only data and command post its
2. Everything combining gui, model and service

## TODO Start Generation
Probably run gradle:build?