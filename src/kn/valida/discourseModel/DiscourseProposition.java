package kn.valida.discourseModel;

import java.util.LinkedHashMap;
import java.util.List;

public class DiscourseProposition {

    private String pid;
    private Speaker originalSpeaker;
    private LinkedHashMap<String,List<Speaker>> beliefHolder = new LinkedHashMap<>();
    private LinkedHashMap<String,List<Speaker>> deniesBelief = new LinkedHashMap<>();
    private String text;

    @Override
    public String toString() {
        return "DiscourseProposition{" +
                "pid='" + pid + '\'' +
                ", text='" + text + '\'' +
                '}';
    }

    public DiscourseProposition(String pid, String text)
    {
        this.pid = pid;
        this.text = text;

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


}
