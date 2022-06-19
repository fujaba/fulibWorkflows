package org.fulib.tools;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;

import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;

public class GraphDiagram {
    private Graph root = new Graph().setId("root").toGraph();
    private List<Edge> edges = new ArrayList<>();

    private Graph currentGraph;
    private String edgesText;

    public GraphDiagram() {
        currentGraph = root;
    }

    public String toSVG() {
        STGroup stGroup = null;
        URL resource = GraphDiagram.class.getResource("graph.stg");
        if (resource != null) {
            stGroup = new STGroupFile(resource);
        } else {
            stGroup = new STGroupFile("src/main/resources/uks/gmde2122/graph.stg");
        }

        String modelsText = "";
        edgesText = "";
        modelsText = nodesOfGraphText(root, stGroup);

        for (Edge edge : this.edges) {
            ST est = stGroup.getInstanceOf("edge");
            est.add("src", edge.getSrcId());
            est.add("srclabel", edge.getSrcLabel());
            est.add("tgtlabel", edge.getTgtLabel());
            est.add("tgt", edge.getTgtId());
            est.add("color", "black");
            String oneEdge = est.render();
            edgesText += oneEdge;
        }

        ST st = stGroup.getInstanceOf("graph");
        st.add("title", root.getId());
        st.add("objects", modelsText);
        st.add("edges", edgesText);
        String dotString = st.render();
        // Files.writeString(Path.of("disjointModels/dotString.txt"), dotString,
        // StandardCharsets.UTF_8);
        String svgString = Graphviz.fromString(dotString).render(Format.SVG).toString();
        return svgString;
    }

    private String nodesOfGraphText(Graph graph, STGroup stGroup) {
        String modelsText = "";
        for (Node node : graph.getNodes().values()) {
            if (node instanceof Graph) {
                String subText = nodesOfGraphText(node.toGraph(), stGroup);

                ST subGraph = stGroup.getInstanceOf("subgraph");
                subGraph.add("graphId", node.getId());
                subGraph.add("label", node.getLabel());
                subGraph.add("objects", subText);
                String oneSubgraph = subGraph.render();
                modelsText += oneSubgraph;
                continue;
            }

            String attrText = node.getAttrText() != null ? node.getAttrText() : "";
            attrText = attrText.replaceAll("\n", "<br align='left'/>");
            ST ost = stGroup.getInstanceOf("simpleObject");
            ost.add("objectId", node.getId());
            ost.add("label", node.getLabel());
            ost.add("attrList", attrText);
            String oneObject = ost.render();
            modelsText += oneObject + "\n";
        }
        return modelsText;
    }

    public GraphDiagram addGraph(String graphId, String label) {
        Node newNode = root.getNodes().computeIfAbsent(graphId, (k) -> new Graph().setId(graphId));
        newNode.setLabel(label);
        currentGraph = newNode.toGraph();
        return this;
    }

    public GraphDiagram addNode(String nodeId, String label) {
        Node newNode = currentGraph.getNodes().computeIfAbsent(nodeId, (k) -> new Node().setId(k));
        newNode.setLabel(label);
        return this;
    }

    public GraphDiagram addNode(String nodeId, String label, String attrText) {
        addNode(nodeId, label);
        addAttrText(nodeId, attrText);
        return this;
    }

    public GraphDiagram addAttrText(String nodeId, String attrText) {
        Node newNode = currentGraph.getNodes().computeIfAbsent(nodeId, (k) -> new Node().setId(k));
        newNode.setAttrText(attrText);
        return this;
    }

    public GraphDiagram addEdge(String srcId, String srcLabel, String tgtId, String tgtLabel) {
        for (Edge edge : edges) {
            if (Objects.equals(srcId, edge.getSrcId())
                    && Objects.equals(srcLabel, edge.getSrcLabel())
                    && Objects.equals(tgtId, edge.getTgtId())
                    && Objects.equals(tgtLabel, edge.getTgtLabel())) {
                return this;
            } else if (Objects.equals(srcId, edge.getSrcId())
                    && Objects.equals(srcLabel, edge.getSrcLabel())
                    && Objects.equals(tgtId, edge.getTgtId())
                    && edge.getTgtLabel() == null) {
                edge.setTgtLabel(tgtLabel);
                return this;
            } else if (Objects.equals(srcId, edge.getTgtId())
                    && Objects.equals(srcLabel, edge.getTgtLabel())
                    && Objects.equals(tgtId, edge.getSrcId())
                    && Objects.equals(tgtLabel, edge.getSrcLabel())) {
                return this;
            } else if (Objects.equals(srcId, edge.getTgtId())
                    && Objects.equals(srcLabel, edge.getTgtLabel())
                    && Objects.equals(tgtId, edge.getSrcId())
                    && tgtLabel == null) {
                return this;
            }
        }

        Edge newEdge = new Edge()
                .setSrcId(srcId)
                .setSrcLabel(srcLabel)
                .setTgtId(tgtId)
                .setTgtLabel(tgtLabel);
        edges.add(newEdge);

        return this;
    }

    public Graph getRoot() {
        return root;
    }

    public GraphDiagram setRoot(Graph root) {
        this.root = root;
        return this;
    }

    public Graph getCurrentGraph() {
        return currentGraph;
    }

}
