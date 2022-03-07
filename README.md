# fulibWorkflows

FulibWorkflows is a library that provides the generation of event storming boards, mockups(html/fxml) and object/classdiagrams.
For the description of workflows inside an event storming board a yaml like syntax is provided.

This tool shall help with event storming and assist the requirements engineering at the start of a project.
Besides the standard events from event storming as described by Alberto Brandolini, there are a few new events to
make it possible to generate the previously mentioned diagrams.

For a detailed description of available events and the limitations of the yaml like syntax take a look at the
[definitions](docs/definitions/README.md).

## Installation
### Gradle
`build.gradle`:

```
repositories {
    // ...
    mavenCentral()
}

dependencies {
    // ...
    
    // https://mvnrepository.com/artifact/org.fulib/fulibWorkflows
    implementation group: 'org.fulib', name: 'fulibWorkflows', version: '0.4.4'
}
```

### Maven
```
<dependency>
  <groupId>org.fulib</groupId>
  <artifactId>fulibWorkflows</artifactId>
  <version>0.4.4</version>
</dependency>
```

## Usage
Check out the [Quickstart Guide](docs/quickstart/README.md) or the [detailed documentation](docs/definitions/README.md) to learn how to use fulibWorkflows.

Also take a look at the [limitations](docs/limitations/README.md) for the fulibWorkflows syntax.

# License
[MIT](https://github.com/fujaba/fulibWorkflows/blob/main/LICENSE.md)
