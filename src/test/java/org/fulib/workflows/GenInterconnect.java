package org.fulib.workflows;

import org.fulib.FulibTools;
import org.fulib.builder.ClassModelManager;
import org.fulib.workflows.html.HtmlGenerator3;
import org.junit.Test;

public class GenInterconnect {
    private ClassModelManager mm;

    @Test
    public void generateInterconnectSystem() {
        mm = new ClassModelManager();
        mm.setMainJavaDir("test/src/main/java");
        mm.setPackageName("uks.debuggen.interconnect");

        String fileName = "test/src/gen/resources/workflows/interconnect/InterconnectOverview.es.yaml";

        // html
        HtmlGenerator3 generator = new HtmlGenerator3();
        generator.dumpObjectDiagram = (f, o) -> {
            FulibTools.objectDiagrams().dumpSVG(f, o);
        };
        generator.generateViewFiles(fileName, "Interconnect");

        // java
        WorkflowGenerator workflowGenerator = new WorkflowGenerator();
        workflowGenerator.dumpObjectDiagram = (o) -> {
            FulibTools.objectDiagrams().dumpSVG("tmp/Interconnect/InterconnectBoard.svg", o);
        };
        workflowGenerator.loadWorkflow(mm, fileName);

        FulibTools.objectDiagrams().dumpSVG("tmp/Interconnect/InterconnectEventStormingModel.svg",
                workflowGenerator.getEventModel().getEventStormingBoard());

        workflowGenerator.generate();

        System.out.println();
    }
}
