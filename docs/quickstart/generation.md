# Generation
TODO -> What is described here -> Generation via gradle tasks or via a junit test

## Gen via Gradle Task
TODO

## Gen Html via JUnit-Test
If you just want to generate a specific workflow you can generate one Workflow Html via the test below.

```java
public class GenTemplate {

    @Test
    public void genTemplateWorkflow() {
        HtmlGenerator3 generator = new HtmlGenerator3();
        generator.generateViewFiles("src/gen/resources/workflows/template.es.yaml", "Template");
    }
}
```

The first parameter of the ``generateViewFiles()``-method is the file path to the workflow you want 
to generate.

The second parameter is the name that the generated files will have. 

## Gen Java via JUnit-Test
TODO
