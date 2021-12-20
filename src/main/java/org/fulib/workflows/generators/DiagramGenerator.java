package org.fulib.workflows.generators;

import org.fulib.workflows.events.BaseNote;
import org.fulib.workflows.events.Board;
import org.fulib.workflows.events.Data;
import org.fulib.workflows.events.Workflow;
import org.fulib.workflows.generators.constructors.ClassDiagramConstructor;
import org.fulib.workflows.generators.constructors.ObjectDiagramConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The DiagramGenerator manages the building and generation of object and class diagrams.
 */
public class DiagramGenerator {

    /**
     * Builds and generates diagrams from an event storming Board
     * @param board generated by the fulibWorkflows yaml parser
     */
    public void buildAndGenerateDiagram(Board board) {
        Map<String, String> generatedDiagrams = buildDiagrams(board);

        for (String key : generatedDiagrams.keySet()) {
            generateDiagram(generatedDiagrams.get(key), key);
        }
    }

    /**
     * Builds object and class structure and files from event storming Board
     * @param board generated by the fulibWorkflows yaml parser
     * @return Map containing all object and classdiagrams as string value, key consists of an index and classdiagram/object
     */
    public Map<String, String> buildDiagrams(Board board) {
        ObjectDiagramConstructor diagramConstructor = new ObjectDiagramConstructor();

        Map<String, String> resultMap = new HashMap<>();

        List<String> diagrams = new ArrayList<>();

        List<Data> previousData = new ArrayList<>();

        // ObjectDiagrams
        for (Workflow workflow : board.getWorkflows()) {
            for (BaseNote note : workflow.getNotes()) {
                if (note instanceof Data) {
                    previousData.add((Data) note);

                    // Always use current note and all previous to represent the objectDiagram according to the timeline
                    String diagramString = diagramConstructor.buildObjectDiagram(previousData, note.getIndex());

                    if (diagramString != null) {
                        diagrams.add(diagramString);
                    }
                }
            }
        }

        for (int i = 0; i < diagrams.size(); i++) {
            String diagram = diagrams.get(i);
            resultMap.put(i + "_diagram", diagram);
        }

        ClassDiagramConstructor classDiagramConstructor = new ClassDiagramConstructor();

        // ClassDiagram
        resultMap.put("classDiagram", classDiagramConstructor.buildClassDiagram(previousData));

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
