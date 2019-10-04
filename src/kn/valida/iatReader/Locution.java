package kn.valida.iatReader;

public class Locution extends Node {
    private String speaker;

    public Locution(String speaker, String text, Integer id) {
        this.speaker = speaker;
        this.setText(text);
        this.setType("L");
        this.setJsonID(id);
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
}
