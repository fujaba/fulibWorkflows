package heraklitcafe.ops;

import java.nio.file.Path;
import java.util.Map;

import org.fulib.workflows.generators.BoardGenerator;

import heraklitcafe.data.Place;

public class HeraklitCafeOperating {

    private Map<String, Object> objMap;

    public static void main(String[] args) {
        new HeraklitCafeOperating().init();
    }

    public void init() {
        // es.yaml laden
        BoardGenerator boardGenerator = new BoardGenerator().setStandAlone();
        boardGenerator.generateBoardFromFile(Path.of("src/gen/resources/heraklit-restaurant.es.yaml"));

        // execute init workflow
        objMap = boardGenerator.loadObjectStructure(Place.class.getPackage().getName(), "default");
    }

}
