package org.fulib.workflows.generators;

import java.nio.file.Path;

import org.junit.Test;
import org.junit.runner.JUnitCore;

public class MicroShopGen {

    public static void main(String[] args) {
        JUnitCore.main(MicroShopGen.class.getName());
    }
    @Test
    public void testOneWorkflow() {
        BoardGenerator boardGenerator = new BoardGenerator();
        boardGenerator.generateBoardFromFile(Path.of("src/gen/resources/microshop.es.yaml"));
    }
}
