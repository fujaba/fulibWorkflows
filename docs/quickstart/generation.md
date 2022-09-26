# Generation

The code needed to start the generation is pretty simple and can be used in `src/main/java` or `src/test/java`. That
only depends upon if you added the dependency for `implementation` or `testImplementation`, the usage is the same.

Either way the following code snippet shows you what you need to know.

## Generation methods

```java
import java.nio.file.Path;

public class Generate {
    public void generateFulibWorkflows() {
        BoardGenerator boardGenerator = new BoardGenerator();

        Path filePath = Path.of("src/gen/resources/workflow.es.yaml"); // Path to the es.yaml file

        String yamlContent;
        try {
            yamlContent = Files.readString(filePath);
            boardGenerator.generateBoardFromString(yamlContent);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        // First is the generation of files
        boardGenerator.generateBoardFromFile(filePath);
        boardGenerator.generateBoardFromString(yamlContent);
        
        // Second is the generation of files and returning their content
        Map<String, String> generatedFiles = boardGenerator.generateAndReturnHTMLsFromFile(yamlContent);
        Map<String, String> generatedFiles = boardGenerator.generateAndReturnHTMLsFromString(yamlContent);
    }
}
```