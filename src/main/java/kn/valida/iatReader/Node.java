package kn.valida.iatReader;

import java.util.ArrayList;
import java.util.List;

public class Node {
    private String type;
    private String text;
    private List<String> relatedElements = new ArrayList<>();
    private Integer jsonID;

    /*

    private enum NodeType{
        LOCUTION,
        PROPOSITION
    }
    */


    public Node()
    {
        super();
    }

    public Node(String type, String text, Integer jsonID)
    {
        this.type = type;
        this.text = text;
        this.jsonID = jsonID;
    }

    @Override
    public String toString() {
        return "Node{" +
                "type='" + type + '\'' +
                ", text='" + text + '\'' +
                ", relatedElements=" + relatedElements +
                ", jsonID=" + jsonID +
                '}';
    }

    //Getter and Setter
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getRelatedElements() {
        return relatedElements;
    }

    public void setRelatedElements(List<String> relatedElements) {
        this.relatedElements = relatedElements;
    }

    public Integer getJsonID() {
        return jsonID;
    }

    public void setJsonID(Integer jsonID) {
        this.jsonID = jsonID;
    }


    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

}
