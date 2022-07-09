package org.fulib.reachable;

import java.util.ArrayList;
import java.util.function.BiConsumer;

import org.fulib.patterns.model.Pattern;
import org.fulib.patterns.model.PatternObject;

public class Rule {

    private Pattern pattern;
    private BiConsumer<Graph, ArrayList<Object>> op;
    private String name;

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

    public String getName() {
        return name;
    }

    public Rule setName(String name) {
        this.name = name;
        return this;
    }

}
