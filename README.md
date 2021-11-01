# fulibWorkflows
fulibWorkflows provides an analyzer for event modelling and the generation of an example implementation.

## Installation
### Gradle
```
dependencies {
    implementation 'org.fulib:fulibWorkflows:0.2.0'
}
```

### Maven
```
<dependency>
  <groupId>org.fulib</groupId>
  <artifactId>fulibWorkflows</artifactId>
  <version>0.2.0</version>
</dependency>
```

## Usage

Workflow files are intended to be placed in the `src/gen/resources/workflows` directory, with the `.es.yaml` extension.

Editors are able to provide completion for the different events via json schema. 
Intellij automatically uses the fulibWorkflows schema from [schemastore](https://www.schemastore.org/json/) when a file has the `.es.yaml` extension.
For VSCode the extension `YAML` from Redhat must be installed.  

For further information on how to use fulibWorkflows check out the [Documentation](https://github.com/fujaba/fulib/blob/master/README.md). 

# License
[MIT](https://github.com/fujaba/fulibWorkflows/blob/main/LICENSE.md)