package kn.valida.discourseModel;

public class Speaker {


    private String sid;
    private String name;
 //   private LinkedHashMap<String,List<DiscourseProposition>> commitments;

    @Override
    public String toString() {
        return name;
    }

    public Speaker(String name, String sid)
    {
        this.name = name;
        this.sid = sid;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /*
    public LinkedHashMap<String, List<DiscourseProposition>> getCommitments() {
        return commitments;
    }

    public void setCommitments(LinkedHashMap<String, List<DiscourseProposition>> commitments) {
        this.commitments = commitments;
    }
*/
}
