package org.fulib.workflows.generators.constructors;

import org.fulib.FulibTools;
import org.fulib.workflows.events.Data;
import org.fulib.yaml.YamlIdMap;
import org.fulib.yaml.YamlObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class DiagramConstructor {
    public String buildDiagram(List<Data> notes, int index) {
        // TODO
        YamlIdMap yamlIdMap = new YamlIdMap();
        YamlObject yamlObject = new YamlObject();

        for (Data note : notes) {
            for (Integer integer : note.getData().keySet()) {
                yamlObject.with(note.getName(), note.getData().get(integer).a);
                yamlObject.with(note.getName(), note.getData().get(integer).b);
            }
        }

        String test = yamlIdMap.encode(yamlObject);

        String fileName = FulibTools.objectDiagrams().dumpSVG("tmp/test/diagram_" + index, yamlObject);

        String result = null;

        try {
            result = Files.readString(Path.of(fileName));

            Files.deleteIfExists(Path.of(fileName));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
