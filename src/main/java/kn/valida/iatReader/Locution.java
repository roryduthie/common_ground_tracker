package kn.valida.iatReader;

public class Locution extends Node {
    private String speaker;

    private String timeStamp;

    public Locution(String speaker, String text, Integer id,String timeStamp) {
        this.speaker = speaker;
        this.setText(text);
        this.setType("L");
        this.setJsonID(id);
        this.setTimeStamp(timeStamp);
    }


    @Override
    public String toString() {
        return "Locution{" +
                "speaker='" + speaker + '\'' +
                ", text='" + this.getText() + '\'' +
                ", relatedElements=" + this.getRelatedElements() +
                '}';
    }


    public String getSpeaker() {
        return speaker;
    }

    public void setSpeaker(String speaker) {
        this.speaker = speaker;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }
}