package kn.valida.discourseModel;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

public class DiscourseProposition {

    private String pid;
    private Speaker originalSpeaker;

    /*TODO maybe make this one static for all DiscoursePropositions then the map doesn't have to be copied again and
    again
    */
    private LinkedHashMap<String,List<Speaker>> beliefHolder = new LinkedHashMap<>();
    private LinkedHashMap<String,List<Speaker>> deniesBelief = new LinkedHashMap<>();
    private String text;
    private List<DiscourseProposition> expressiveContent = new ArrayList<>();


    //Experimental
    private LinkedHashMap<String,Double> relevance = new LinkedHashMap<>();

    private LinkedHashMap<String,Double> semanticSimilarity = new LinkedHashMap<>();
    private float[] embedding;

    //Even more experimental
    private LinkedHashMap<String,List<Commitment>> positiveCommitments = new LinkedHashMap<>();
    private LinkedHashMap<String,List<Commitment>> negativeCommitments  = new LinkedHashMap<>();


    @Override
    public String toString() {
        return "DiscourseProposition{" +
                "pid='" + pid + '\'' +
                ", text='" + text + '\'' +
                '}';
    }


    public DiscourseProposition()
    {}

    public DiscourseProposition(String pid, String text, float[] embedding)
    {
        this.pid = pid;
        this.text = text;
        this.embedding = embedding;

        }

    public DiscourseProposition(String pid, String text, Speaker originalSpeaker,
                                LinkedHashMap<String,List<Speaker>> beliefHolder,
                                LinkedHashMap<String,List<Speaker>> deniesBelief,
                                List<DiscourseProposition> expressiveContent,
                                float[] embedding)
    {
        this.pid = pid;
        this.text = text;
        this.originalSpeaker = originalSpeaker;
        this.beliefHolder = beliefHolder;
        this.deniesBelief = deniesBelief;
        this.expressiveContent = expressiveContent;
        this.embedding = embedding;
    }


    public String writeExpressiveContent()
    {
        return expressiveContent.stream().map(n -> n.text).collect(Collectors.joining(", "));
    }


    //Getter and Setter

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public LinkedHashMap<String,List<Speaker>> getBeliefHolder() {
        return beliefHolder;
    }

    public void setBeliefHolder(LinkedHashMap<String,List<Speaker>> beliefHolder) {
        this.beliefHolder = beliefHolder;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Speaker getOriginalSpeaker() {
        return originalSpeaker;
    }

    public void setOriginalSpeaker(Speaker originalSpeaker) {
        this.originalSpeaker = originalSpeaker;
    }

    public LinkedHashMap<String, List<Speaker>> getDeniesBelief() {
        return deniesBelief;
    }

    public void setDeniesBelief(LinkedHashMap<String, List<Speaker>> deniesBelief) {
        this.deniesBelief = deniesBelief;
    }

    public List<DiscourseProposition> getExpressiveContent() {
        return expressiveContent;
    }

    public void setExpressiveContent(List<DiscourseProposition> expressiveContent) {
        this.expressiveContent = expressiveContent;
    }

    public LinkedHashMap<String, Double> getRelevance() {
        return relevance;
    }

    public void setRelevance(LinkedHashMap<String, Double> relevance) {
        this.relevance = relevance;
    }

    public LinkedHashMap<String, Double> getSemanticSimilarity() {
        return semanticSimilarity;
    }

    public void setSemanticSimilarity(LinkedHashMap<String, Double> semanticSimilarity) {
        this.semanticSimilarity = semanticSimilarity;
    }

    public float[] getEmbedding() {
        return embedding;
    }

    public void setEmbedding(float[] embedding) {
        this.embedding = embedding;
    }

    public LinkedHashMap<String, List<Commitment>> getPositiveCommitments() {
        return positiveCommitments;
    }

    public void setPositiveCommitments(LinkedHashMap<String, List<Commitment>> positiveCommitments) {
        this.positiveCommitments = positiveCommitments;
    }

    public LinkedHashMap<String, List<Commitment>> getNegativeCommitments() {
        return negativeCommitments;
    }

    public void setNegativeCommitments(LinkedHashMap<String, List<Commitment>> negativeCommitments) {
        this.negativeCommitments = negativeCommitments;
    }

}
