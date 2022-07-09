package org.fulib.tools;

public class Node {
    private String id;
    private String label;
    private String attrText;
    private String href;

    public String getHref() {
        return href;
    }

    public Node setHref(String href) {
        this.href = href;
        return this;
    }


    public DGraph toGraph() {
        return null;
    }

    public String getId() {
        return id;
    }

    public Node setId(String id) {
        this.id = id;
        return this;
    }

    public String getLabel() {
        return label;
    }

    public Node setLabel(String label) {
        this.label = label;
        return this;
    }

    public String getAttrText() {
        return attrText;
    }

    public Node setAttrText(String attrText) {
        this.attrText = attrText;
        return this;
    }
}
