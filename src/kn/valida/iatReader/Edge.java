package kn.valida.iatReader;

import java.util.ArrayList;
import java.util.List;

public class Edge {

    private Node mother;
    private Node daugther;

    public Edge(Node mother, Node  daughter)
    {
        this.mother = mother;
        this.daugther = daughter;
    }

    /**Find a node that is related to the input node a via a specific relation from a  list of nodes.
     * Content nodes are propositions or locutions
    */
     public static List<Node> findRelatedMotherContentNode(Node a, List<Node> b, List<Edge> edges, String relationType)
    {

        List<Node> motherContentNodes = new ArrayList<>();

        List<Node> intermediateNodes = new ArrayList<>();

        for (Edge e : edges) {
            if (e.getMother() instanceof Node && e.getMother().getText().equals(relationType)) {
                if (a.getJsonID().equals(e.getDaugther().getJsonID())) {
                    intermediateNodes.add(e.getMother());
                }
            }
        }

        for (Node n : intermediateNodes)
        {
            for (Edge e : edges){
                if (n.getJsonID().equals(e.getDaugther().getJsonID()))
                {
                   motherContentNodes.add(e.getMother());
                }
            }
        }

        return motherContentNodes;
    }



    public static List<Node> findRelatedDaugtherContentNode(Node a, List<Node> b, List<Edge> edges, String relationType)
    {
        List<Node> intermediateNodes = new ArrayList<>();
        List<Node> daugtherContentNode = new ArrayList<>();

        for (Edge e : edges) {
            if (e.getDaugther() instanceof Node && e.getDaugther().getText().equals(relationType)) {
                if (a.getJsonID().equals(e.getMother().getJsonID()) && !intermediateNodes.contains(e.getDaugther())) {
                    intermediateNodes.add(e.getDaugther());
                }
            }
        }

        for (Node n : intermediateNodes)
        {
            for (Edge e : edges){
                if (n.getJsonID().equals(e.getMother().getJsonID()) && !daugtherContentNode.contains(e.getDaugther()))
                {
                    daugtherContentNode.add(e.getDaugther());
                }
            }
        }

        return daugtherContentNode;
    }

    /**
     * Find daugther node with specific text (For finding functional nodes such as default transition)
     * @param a
     * @param edges
     * @param type
     * @return
     */
    public static List<Node> findDaughter(Node a, List<Edge> edges, String type)
    {
        List<Node> nodeList = new ArrayList<>();

        for (Edge e : edges) {
            if (e.getDaugther().getType().equals(type)) {
                if (a.getJsonID().equals(e.getMother().getJsonID())) {
                    nodeList.add(e.getDaugther());
                }
            }
        }
        return nodeList;
    }


    //Find direct daughter disregarding of type. E.g. for finding propositions that are conflicted.



    /**
     * Find mother node with specific text (For finding functional nodes such as default transition)
     * @param a
     * @param edges
     * @param text
     * @return
     * TODO: find Mother based on text vs. find Mother based on type
     */
    public static List<Node> findMother(Node a, List<Edge> edges, String text)
    {

        List<Node> nodeList = new ArrayList<>();
        for (Edge e : edges) {
            if (e.getMother().getText().equals(text)) {
                if (a.getJsonID().equals(e.getDaugther().getJsonID())) {
                    nodeList.add(e.getMother());
                }
            }
        }
        return nodeList;
    }




    @Override
    public String toString() {
        return "Edge{" +
                "mother=" + mother +
                ", daugther=" + daugther +
                '}';
    }







    public Node getMother() {
        return mother;
    }

    public void setMother(Node mother) {
        this.mother = mother;
    }

    public Node getDaugther() {
        return daugther;
    }

    public void setDaugther(Node daugther) {
        this.daugther = daugther;
    }


}
