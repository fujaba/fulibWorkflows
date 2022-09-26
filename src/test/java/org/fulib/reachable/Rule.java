package org.fulib.reachable;

import org.fulib.patterns.model.Pattern;

import java.util.ArrayList;
import java.util.function.BiConsumer;

public class Rule {

    private Pattern pattern;
    private Pattern rhs;
    private BiConsumer<Graph, ArrayList<Object>> op;
    private String name;
    private String patternConstraint;

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

    public Pattern getRhs()
    {
        return rhs;
    }

    public Rule setRhs(Pattern rhs)
    {
        this.rhs = rhs;
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

    public String getPatternConstraint()
    {
        return patternConstraint;
    }

    public Rule setPatternConstraint(String patternConstraint)
    {
        this.patternConstraint = patternConstraint;
        return this;
    }
}
