package org.fulib.reachable;

import java.util.ArrayList;
import java.util.function.BiConsumer;

import org.fulib.patterns.model.Pattern;

public class Rule {

    private Pattern pattern;
    private BiConsumer<Graph, ArrayList<Object>> op;

    public Pattern getPattern() {
        return pattern;
    }

    public BiConsumer<Graph, ArrayList<Object>> getOp() {
        return op;
    }

    public Rule setPattern(Pattern pattern) {
        this.pattern = pattern;
        return this;
    }

    public Rule setOp(BiConsumer<Graph, ArrayList<Object>> op) {
        this.op = op;
        return this;
    }

}
