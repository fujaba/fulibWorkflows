## Introduction
Welcome to the official fulibWorkflows documentation.

You can learn how to define workflows and generate the corresponding files.

The specific syntax definitions of the workflows at [Definitions](definitions/README.md).

## HowTo setup fulibWorkflows
If you are using Gradle simply add `implementation 'org.fulib:fulibWorkflows:0.3.1'` to your `build.gradle`. (This is also described [here](../README.md))

Workflow files are intended to be placed in the `src/gen/resources/workflows` directory, with the `.es.yaml` extension.

Editors are able to provide completion for the different events via json schema. 
The json schema for fulibWorkflows is available via [schemastore.org](https://www.schemastore.org/json/).

#### Intellij
Intellij automatically uses the fulibWorkflows schema when a file has the `.es.yaml` extension.

#### VSCode
For VSCode the extension `YAML` from Redhat must be installed. With the extension you will have the same behaviour as if you were using Intellij.

## Limitations
Due to the current parsing of the .es.yaml file using an antlr4 grammer there are certain limitations for names and values described in [here](limitations/README.md).

- [Quickstart](quickstart/README.md)
- [Definitions](definitions/README.md)
- [Limitations](limitations/README.md)
