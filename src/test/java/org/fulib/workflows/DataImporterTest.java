package org.fulib.workflows;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;

public class DataImporterTest {

    @Test
    public void testDataImporter() {
        DataImporter dataImporter = new DataImporter();
        dataImporter.importFromFile(Path.of("src/main/resources/workflow.es.yaml"));
    }
}
