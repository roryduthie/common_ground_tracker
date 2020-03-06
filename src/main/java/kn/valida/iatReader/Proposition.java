package kn.valida.iatReader;

public class Proposition extends Node {
    private String speaker;


    public Proposition(String text, Integer id)
    {

        this.setText(text);
        this.setType("I");
        this.setJsonID(id);
    }
}
