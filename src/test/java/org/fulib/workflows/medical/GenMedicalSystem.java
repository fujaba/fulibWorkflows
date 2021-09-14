package org.fulib.workflows.medical;

import org.fulib.FulibTools;
import org.fulib.builder.ClassModelManager;
import org.fulib.workflows.WorkflowGenerator;
import org.fulib.workflows.html.HtmlGenerator3;
import org.junit.Test;

public class GenMedicalSystem {
    private ClassModelManager mm;

    @Test
    public void generateMedicalSystem() {
        genSystem("FamilyDoctorDegen");
        genSystem("MarburgExpertSystem");
    }

    private void genSystem(String system) {
        String packageName = system.toLowerCase();
        mm = new ClassModelManager();
        mm.setMainJavaDir("test/src/main/java");
        mm.setPackageName("uks.debuggen.medical." + packageName);

        String fileName = String.format("test/src/gen/resources/workflows/medical/%s/%1$s.es.yaml", system);

        // html
        HtmlGenerator3 generator = new HtmlGenerator3();
        generator.dumpObjectDiagram = (f, o) -> {
            FulibTools.objectDiagrams().dumpSVG(f, o);
        };
        generator.generateViewFiles(fileName, system);

        // java
        WorkflowGenerator workflowGenerator = new WorkflowGenerator();
        workflowGenerator.dumpObjectDiagram = (o) -> {
            FulibTools.objectDiagrams().dumpSVG(String.format("tmp/%s/%s.svg", system, system), o);
        };
        workflowGenerator.loadWorkflow(mm, fileName);

        FulibTools.objectDiagrams().dumpSVG(String.format("tmp/%s/%sEventStormingModel.svg", system, system),
                workflowGenerator.getEventModel().getEventStormingBoard());

        workflowGenerator.generate();

        System.out.println();

    }
}
