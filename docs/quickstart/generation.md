# Generation

## Gen Files

The code needed to start the generation is pretty simple and can be used in `src/main/java` or `src/test/java`. That
only depends upon if you added the dependency for `implementation` or `testImplementation`.

Either way the following code snippet shows you what you need to know.

### implementation

```java
public class Generate {
    public void generateFulibWorkflows() {
        BoardGenerator boardGenerator = new BoardGenerator();
        // Generates the content of the files and returns those with the key of the map as an identifier
        Map<String, String> htmls = boardGenerator.generateAndReturnHTMLs(yamlData);
    }
}
```

### testImplementation

```java
public class GenTemplate {

    @Test
    public void genTemplateWorkflow() {
        BoardGenerator boardGenerator = new BoardGenerator();
        // Simply generates the files from a workflow file
        boardGenerator.generateBoardFromFile(Path.of("src/gen/resources/workflow.es.yaml"));
    }
}
```