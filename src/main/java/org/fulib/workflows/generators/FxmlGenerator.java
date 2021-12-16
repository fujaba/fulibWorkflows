package org.fulib.workflows.generators;

import org.fulib.workflows.events.*;
import org.fulib.workflows.generators.constructors.FxmlConstructor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FxmlGenerator {
    public void buildAndGenerateFxmls(Board board) {
        Map<String, String> generatedFxmls = buildFxmls(board);

        for (String key : generatedFxmls.keySet()) {
            generateDiagram(generatedFxmls.get(key), key);
        }
    }

    public Map<String, String> buildFxmls(Board board) {
        FxmlConstructor fxmlConstructor = new FxmlConstructor();

        Map<String, String> resultMap = new HashMap<>();

        List<String> fxmls = new ArrayList<>();

        for (Workflow workflow : board.getWorkflows()) {
            for (BaseNote note : workflow.getNotes()) {
                if (note instanceof Page) {
                    String fxmlString = fxmlConstructor.buildFxml((Page) note);

                    if (fxmlString != null) {
                        fxmls.add(fxmlString);
                    }
                }
            }
        }

        for (int i = 0; i < fxmls.size(); i++) {
            String page = fxmls.get(i);
            resultMap.put(i + "_fxml", page);
        }

        return resultMap;
    }

    private void generateDiagram(String fxmlContent, String fileName) {
        try {
            String outputDirectory = "tmp/fxmls/";
            Files.createDirectories(Path.of(outputDirectory));

            String outputFxmlFilePath = outputDirectory + fileName + ".fxml";
            if (!Files.exists(Path.of(outputFxmlFilePath))) {
                Files.createFile(Path.of(outputFxmlFilePath));
            }
            Files.writeString(Path.of(outputFxmlFilePath), fxmlContent);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
