package kn.valida.iatReader;

import kn.valida.utilities.Utilities;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IATmap {
    private List<Node> nodes;
    private List<Locution> locutions;
    private List<Proposition> propositions;
    private List<Edge> edges;
    private List<String> speakers;
    public String fileName;


    public IATmap(String fileName, List<Node> nodes, List<Locution> locutions, List<Proposition> propositions, List<Edge> edges,
    List<String> speakers)
    {
        this.fileName = fileName;
        this.nodes = nodes;
        this.locutions = locutions;
        this.propositions = propositions;
        this.edges = edges;
        this.speakers = speakers;
    }

    /**
     Translate JSON input step by step into JAVA native objects.
     @param file This is a json file containing an IAT annotation
     */
    public static IATmap  analyzeIATmap(File file) throws IOException {

        //Collect all nodes and edges into Java-native lists
        List<JSONObject> nodes = new ArrayList<>();
        List<JSONObject> edges = new ArrayList<>();

        String content =  Utilities.readFile(file.toString(), Charset.defaultCharset());
        //sb.append(content);
        JSONObject json = new JSONObject(content);
        JSONArray jsonnodes = (JSONArray) json.get("nodes");
        JSONArray jsonedges = (JSONArray) json.get("edges");

        //Collect nodes into list
        if (jsonnodes != null) {
            int len = jsonnodes.length();
            for (int j = 0; j < len; j++) {
                nodes.add((JSONObject) jsonnodes.get(j));
            }
        }

        //Collect edges into list
        if (jsonedges != null) {
            int len = jsonedges.length();
            for (int k = 0; k < len; k++) {
                edges.add((JSONObject) jsonedges.get(k));
            }
        }

        List<Locution> locutionsInMap = new ArrayList<>();
        List<Proposition> propositionsInMap = new ArrayList<>();
        List<Node> nodesInMap = new ArrayList<>();
        List<Edge> edgesInMap = new ArrayList<>();
        List<String> speakers = new ArrayList<>();

        //decompose locutions into speaker and text
        Pattern annotaterPattern = Pattern.compile("^.*: .* : .*");
        Pattern textPattern = Pattern.compile("^(.*) : (.*)");

        for (JSONObject j : nodes){
            //TODO node feature varies across representations (aifdb vs intern)
            //aifdb: "nodeID"
            //intern: "id"
            switch (j.get("type").toString()) {
                //Type L for locution
                case "L":
                    Matcher annotatorMatcher = annotaterPattern.matcher(j.get("text").toString());
                    if (annotatorMatcher.find()) {
                        continue;
                    } else {
                        Matcher textMatcher = textPattern.matcher(j.get("text").toString());
                        if (textMatcher.find()) {
                            Locution l = new Locution(textMatcher.group(1), textMatcher.group(2), Integer.parseInt(j.get("nodeID").toString()),
                                    j.get("timestamp").toString());
                            locutionsInMap.add(l);
                            //Experiment:

                            speakers.add(l.getSpeaker());

                            nodesInMap.add(l);

                        }
                        else{
                            Locution l = new Locution("unknown_speaker",j.get("text").toString(),Integer.parseInt(j.get("nodeID").toString()),
                                    j.get("timestamp").toString());
                            locutionsInMap.add(l);

                            speakers.add(l.getSpeaker());

                            nodesInMap.add(l);
                        }
                    }
                    break;
                //Type I for propositions (?)
                case "I":
                    String text = j.get("text").toString();
                    Proposition p = new Proposition(text, Integer.parseInt(j.get("nodeID").toString()));
                    propositionsInMap.add(p);
                    nodesInMap.add(p);
                    break;

                default:
                    String text1 = j.get("text").toString();
                    String type = j.get("type").toString();
                    Integer id = Integer.parseInt(j.get("nodeID").toString());
                    Node n = new Node(type, text1, id);
                    nodesInMap.add(n);
                    break;
            }

            //      locutions.addAll(locutionsInMap);
            //      propositions.addAll(propositionsInMap);
        }

        for (JSONObject e : edges)
        {
            /*
            TODO for testsuite
            JSONObject from = (JSONObject) e.get("from");
            Integer fromID = Integer.parseInt(from.get("nodeID").toString());
            JSONObject to = (JSONObject) e.get("to");
            Integer toID = Integer.parseInt(to.get("nodeID").toString());

             */

            //For aifdb

            Integer fromID = Integer.parseInt(e.get("fromID").toString());
            Integer toID = Integer.parseInt(e.get("toID").toString());

            Node mother = null;
            Node daughter = null;


            for (Node n : nodesInMap){
                if (fromID.equals(n.getJsonID()))
                {
                    mother = n;
                    break;
                }

            }

            for (Node n : nodesInMap) {
                if (toID.equals(n.getJsonID())) {
                    daughter = n;
                    break;
                }
            }


            if (daughter != null && mother != null) {
                Edge edge = new Edge(mother, daughter);
                edgesInMap.add(edge);
            } else{
                System.out.println("Invalid edge at " + e.toString() +"-- mother or daughter missing.");
            }


        }

        //Sort locutions by timestamp

        Collections.sort(locutionsInMap, new Comparator<Locution>() {
            @Override
            public int compare(Locution o1, Locution o2) {
                try {
                    DateFormat format = new SimpleDateFormat("MM-dd-yyyy hh:mm:ss");
                    return format.parse(o1.getTimeStamp()).compareTo(format.parse(o2.getTimeStamp()));

                } catch (Exception e) {
                    e.printStackTrace();
                    return 0;
                }
            }
        });


        IATmap map = new IATmap(file.toString(),nodesInMap,locutionsInMap,propositionsInMap,edgesInMap,speakers);



        return map;

    }


    //Getter and Setter
    public List<Node> getNodes() {
        return nodes;
    }

    public void setNodes(List<Node> nodes) {
        this.nodes = nodes;
    }

    public List<Locution> getLocutions() {
        return locutions;
    }

    public void setLocutions(List<Locution> locutions) {
        this.locutions = locutions;
    }

    public List<Proposition> getPropositions() {
        return propositions;
    }

    public void setPropositions(List<Proposition> propositions) {
        this.propositions = propositions;
    }

    public List<Edge> getEdges() {
        return edges;
    }

    public void setEdges(List<Edge> edges) {
        this.edges = edges;
    }

    public List<String> getSpeakers() {
        return speakers;
    }

    public void setSpeakers(List<String> speakers) {
        this.speakers = speakers;
    }
}
