package org.fulib.workflows;

import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class DataImporter {

    public void importFromFile(Path yamlFile) {
        try {
            String yamlContent = Files.readString(yamlFile);
            importFromString(yamlContent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void importFromString(String yamlContent) {
        Yaml yaml = new Yaml();

        ArrayList<Object> test = yaml.load(yamlContent);
        System.out.println(test);
    }
}
