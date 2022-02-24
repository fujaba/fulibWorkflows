package org.fulib.tools;

public class Edge {
    private String srcId;
    private String srcLabel;
    private String tgtId;
    private String tgtLabel;

    public String getSrcId() {
        return srcId;
    }

    public Edge setSrcId(String srcId) {
        this.srcId = srcId;
        return this;
    }

    public String getSrcLabel() {
        return srcLabel;
    }

    public Edge setSrcLabel(String srcLabel) {
        this.srcLabel = srcLabel;
        return this;
    }

    public String getTgtId() {
        return tgtId;
    }

    public Edge setTgtId(String tgtId) {
        this.tgtId = tgtId;
        return this;
    }

    public String getTgtLabel() {
        return tgtLabel;
    }

    public Edge setTgtLabel(String tgtLabel) {
        this.tgtLabel = tgtLabel;
        return this;
    }

}
