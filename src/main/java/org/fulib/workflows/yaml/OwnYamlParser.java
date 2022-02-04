package org.fulib.workflows.yaml;

import org.fulib.workflows.events.Board;
import org.yaml.snakeyaml.Yaml;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OwnYamlParser {
    private Board board;

    public void parseYAML(String yamlInput) {
        Yaml yaml = new Yaml();
        List<Object> loadedEvents = yaml.load(yamlInput);

        for (Object loadedEvent : loadedEvents) {
            Map<String, Object> singleEventMap = (HashMap<String, Object>) loadedEvent;

            for (Map.Entry<String, Object> entry : singleEventMap.entrySet()) {
                String key = entry.getKey();
                String valueType = entry.getValue().getClass().getSimpleName();

                switch (valueType) {
                    case "String":
                        String value = entry.getValue();

                        System.out.println(key + ": " + value);
                        break;
                    case "Integer":
                        String value = entry.getValue();

                        System.out.println(key + ": " + value);
                        break;
                    case "ArrayList":
                        String value = entry.getValue();

                        System.out.println(key + ": " + value);
                        break;
                }
            }
        }
    }

    public Board getBoard() {
        return board;
    }
}
