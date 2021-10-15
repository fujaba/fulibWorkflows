package org.fulib.workflows;

import org.fulib.FulibTools;
import org.fulib.builder.ClassModelManager;
import org.fulib.workflows.html.HtmlGenerator3;
import org.junit.Test;

public class TestStudyRight {

    private ClassModelManager mm;

    @Test
    public void testStartGenerator() {
        mm = new ClassModelManager();
        mm.setMainJavaDir("test/src/main/java");
        mm.setPackageName("uks.debuggen.studyright");

        // html
        HtmlGenerator3 generator = new HtmlGenerator3();
        generator.dumpObjectDiagram = (f, o) -> {
            FulibTools.objectDiagrams().dumpSVG(f, o);
        };
        String filename = "test/src/gen/resources/workflows/StudyRight.es.yaml";
        generator.generateViewFiles(filename, "StudyRight");

        // java
        WorkflowGenerator workflowGenerator = new WorkflowGenerator();
        workflowGenerator.loadWorkflow(mm, filename);

        FulibTools.objectDiagrams().dumpSVG("tmp/StudyRight/StudyRightModel.svg",
                workflowGenerator.getEventModel().getEventStormingBoard());

        workflowGenerator.generate();

        System.err.println();
    }
}
