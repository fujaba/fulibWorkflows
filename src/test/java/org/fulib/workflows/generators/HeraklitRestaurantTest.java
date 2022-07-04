package org.fulib.workflows.generators;

import java.nio.file.Path;

import org.junit.Test;
import org.junit.runner.JUnitCore;

public class HeraklitRestaurantTest {

    public static void main(String[] args) {
        JUnitCore.main(HeraklitRestaurantTest.class.getName());
    }
    @Test
    public void testOneWorkflow() {
        BoardGenerator boardGenerator = new BoardGenerator().setStandAlone();
        boardGenerator.generateBoardFromFile(Path.of("src/gen/resources/heraklit-restaurant.es.yaml"));
        boardGenerator.generateClassCode("src/test/java", "heraklitcafe");
    }
}
