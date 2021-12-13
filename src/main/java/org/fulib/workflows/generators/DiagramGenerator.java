package org.fulib.workflows.generators;

import org.fulib.workflows.events.BaseNote;
import org.fulib.workflows.events.Board;
import org.fulib.workflows.events.Data;
import org.fulib.workflows.events.Workflow;
import org.fulib.workflows.generators.constructors.DiagramConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class DiagramGenerator {
    public void buildAndGenerateDiagram(Board board) {
        Map<String, String> generatedDiagrams = buildDiagrams(board);

        for (String key : generatedDiagrams.keySet()) {
            generateDiagram(generatedDiagrams.get(key), key);
        }
    }

    public Map<String, String> buildDiagrams(Board board) {
        DiagramConstructor diagramConstructor = new DiagramConstructor();

        Map<String, String> resultMap = new HashMap<>();

        List<String> diagrams = new ArrayList<>();

        for (Workflow workflow : board.getWorkflows()) {
            for (BaseNote note : workflow.getNotes()) {
                if (note instanceof Data) {
                    // Big TODO because i have to know every data before that
                    String diagramString = diagramConstructor.buildDiagram((Data) note);

                    if (diagramString != null) {
                        diagrams.add(diagramString);
                    }
                }
            }
        }

        for (int i = 0; i < diagrams.size(); i++) {
            String page = diagrams.get(i);
            resultMap.put(i + "_diagram", page);
        }

        return resultMap;
    }

    private void generateDiagram(String diagramContent, String fileName) {
        try {
            String outputDirectory = "tmp/diagrams/";
            Files.createDirectories(Path.of(outputDirectory));

            String outputDiagramFilePath = outputDirectory + fileName + ".svg";
            if (!Files.exists(Path.of(outputDiagramFilePath))) {
                Files.createFile(Path.of(outputDiagramFilePath));
            }
            Files.writeString(Path.of(outputDiagramFilePath), diagramContent);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
