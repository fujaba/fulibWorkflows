package org.fulib.workflows.utils;

import org.fulib.classmodel.Clazz;

/**
 * Helper class for managing data needed to build associations with fulib
 */
public class Association {
    /**
     * Source Class for an association
     */
    public Clazz srcClazz;
    /**
     * Source Name for an association
     */
    public String srcName;
    /**
     * Source Cardinality for an association
     */
    public int srcCardi;
    /**
     * Target Class for an association
     */
    public Clazz tgtClazz;
    /**
     * Target name for an association
     */
    public String tgtName;
    /**
     * Target Cardinality for an association
     */
    public int tgtCardi;
}
