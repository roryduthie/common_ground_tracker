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


    @Override
    public String toString() {
        return "DiscourseProposition{" +
                "pid='" + pid + '\'' +
                ", text='" + text + '\'' +
                '}';
    }


    public DiscourseProposition()
    {}

    public DiscourseProposition(String pid, String text)
    {
        this.pid = pid;
        this.text = text;

        }

    public DiscourseProposition(String pid, String text, Speaker originalSpeaker,
                                LinkedHashMap<String,List<Speaker>> beliefHolder,
                                LinkedHashMap<String,List<Speaker>> deniesBelief,
                                List<DiscourseProposition> expressiveContent)
    {
        this.pid = pid;
        this.text = text;
        this.originalSpeaker = originalSpeaker;
        this.beliefHolder = beliefHolder;
        this.deniesBelief = deniesBelief;
        this.expressiveContent = expressiveContent;
    }



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

    public String writeExpressiveContent()
    {
    return expressiveContent.stream().map(n -> n.text).collect(Collectors.joining(", "));
    }


}
