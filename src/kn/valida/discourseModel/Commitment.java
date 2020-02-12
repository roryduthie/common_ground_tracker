package kn.valida.discourseModel;

public class Commitment {


    private Speaker commitmentHolder;
    //This is not strictly necessary but may be useful for some other visualization tasks?
    String pid;
    //A rating r, 0 < r <= 1, expressing how strong the commitment
    private double commitmentRating;


    public Commitment(String pid, Speaker commitmentHolder, double commitmentRating)
    {
        this.pid = pid;
        this.commitmentHolder = commitmentHolder;
        this.commitmentRating = commitmentRating;
    }


    //Getter and Setter Methodes
    public Speaker getCommitmentHolder() {
        return commitmentHolder;
    }

    public void setCommitmentHolder(Speaker commitmentHolder) {
        this.commitmentHolder = commitmentHolder;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public double getCommitmentRating() {
        return commitmentRating;
    }

    public void setCommitmentRating(double commitmentRating) {
        this.commitmentRating = commitmentRating;
    }
}
