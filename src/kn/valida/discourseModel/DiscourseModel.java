package kn.valida.discourseModel;

import kn.valida.iatReader.*;
import kn.valida.utilities.VariableHandler;

import java.util.*;

public class DiscourseModel {

    private List<IATmap> annotatedDebate;
    //Discourse propositions provide temporal ordering for the whole model, i.e. everything revolves around these
    private LinkedList<DiscourseProposition> discoursePropositions;
    private List<Speaker> discourseParticipants = new ArrayList<>();
    private HashMap<Proposition,DiscourseProposition> propToDiscProp = new HashMap();


    public DiscourseModel(List<IATmap> annotatedDebate)
    {
        this.annotatedDebate = annotatedDebate;
        this.discoursePropositions = returnDiscoursePropostions();
        System.out.println("Collected discourse propositions and speakers.");


    }

    /**
     * Make this method so that it carries all the important work. For every new proposition first copy the previous
     * commitments and believeHolders to the new key (the new discoursePropositionID); then
     * do all the computations i.e. implement agree, disagree etc.
     * @return
     */
    public LinkedList<DiscourseProposition> returnDiscoursePropostions(){
        LinkedList<DiscourseProposition> discoursePropositions = new LinkedList<>();
        LinkedList<Locution>  locutions = new LinkedList<>();

        //Map by Map generate discourse propositions
        for (IATmap map : annotatedDebate)
        {
            for (Locution l : map.getLocutions())
            {
                //Copy old information to current proposition
                locutions.add(l);

                String pid = (String) VariableHandler.returnNewVar(VariableHandler.variableType.PROPOSITION);
                List<Node> daugtherNodes = Edge.findRelatedDaugtherContentNode(l,map.getNodes(), map.getEdges(),"Asserting");
                DiscourseProposition dp = null;
                //First try to find locutions that initialize proposition

                if (!daugtherNodes.isEmpty()) {

                    for (Node daugtherNode : daugtherNodes) {

                        if (daugtherNode instanceof Proposition) {
                            dp = new DiscourseProposition(pid, daugtherNode.getText());
                            Speaker s = isParticipant(l.getSpeaker());
                            if (s == null) {
                                s = new Speaker(l.getSpeaker(),
                                        (String) VariableHandler.returnNewVar(VariableHandler.variableType.SPEAKER));
                                discourseParticipants.add(s);
                            }

                            if (!discoursePropositions.isEmpty()) {

                                LinkedHashMap<String, List<Speaker>> currentBeliefs = new LinkedHashMap<>();

                                //copy old information to current locution

                                for (String key : discoursePropositions.getLast().getBeliefHolder().keySet()) {
                                    List<Speaker> beliefHolders = new ArrayList<>();

                                    for (Speaker speaker : discoursePropositions.getLast().getBeliefHolder().get(key)) {
                                        beliefHolders.add(speaker);
                                    }
                                    currentBeliefs.put(key, beliefHolders);

                                }
                                dp.setBeliefHolder(currentBeliefs);

                                LinkedHashMap<String, List<Speaker>> currentDenials = new LinkedHashMap<>();

                                for (String key : discoursePropositions.getLast().getDeniesBelief().keySet()) {
                                    List<Speaker> deniesBelief = new ArrayList<>();

                                    for (Speaker speaker : discoursePropositions.getLast().getDeniesBelief().get(key)) {
                                        deniesBelief.add(speaker);
                                    }
                                    currentDenials.put(key, deniesBelief);
                                }
                                dp.setDeniesBelief(currentDenials);

                            }


                            dp.setOriginalSpeaker(s);
                            List<Speaker> believeHolders = new ArrayList<>();
                            believeHolders.add(s);
                            dp.getBeliefHolder().put(pid, believeHolders);
                            dp.getDeniesBelief().put(pid, new ArrayList<>());
                            propToDiscProp.put((Proposition) daugtherNode, dp);


                            //}else{
                            //    dp = new DiscourseProposition(pid, p.getText());
                        }


                        if (dp != null) {

                            List<Node> motherNodes = Edge.findMother(l, map.getEdges(), "Default Transition");

                            for (Node transition : motherNodes) {
                                List<Node> agreePropositions = Edge.findRelatedDaugtherContentNode(transition, map.getNodes(), map.getEdges(), "Agreeing");
                                if (!agreePropositions.isEmpty()) {
                                    for (Node p : agreePropositions) {
                                        dp.getBeliefHolder().get(propToDiscProp.get(p).getPid()).add(dp.getOriginalSpeaker());
                                    }
                                }


                                List<Node> disagreePropositions = Edge.findRelatedDaugtherContentNode(transition, map.getNodes(), map.getEdges(), "Disagreeing");

                                if (!disagreePropositions.isEmpty()) {
                                    for (Node p : disagreePropositions) {
                                        if (p instanceof Proposition) {
                                            dp.getDeniesBelief().get(propToDiscProp.get(p).getPid()).add(dp.getOriginalSpeaker());
                                        }
                                    }
                                }
                            }

                            discoursePropositions.add(dp);
                        }
                    }
                    }

                }

            }


        return discoursePropositions;

    }



    public Speaker isParticipant(String name)
    {
        for (Speaker s : discourseParticipants)
        {
            if (name.equals(s.getName()))
            {
                return s;
            }
        }
        return null;
    }

    public List<IATmap> getAnnotatedDebate() {
        return annotatedDebate;
    }

    public void setAnnotatedDebate(List<IATmap> annotatedDebate) {
        this.annotatedDebate = annotatedDebate;
    }

    public LinkedList<DiscourseProposition> getDiscoursePropositions() {
        return discoursePropositions;
    }

    public void setDiscoursePropositions(LinkedList<DiscourseProposition> discoursePropositions) {
        this.discoursePropositions = discoursePropositions;
    }

    public List<Speaker> getDiscourseParticipants() {
        return discourseParticipants;
    }

    public void setDiscourseParticipants(List<Speaker> discourseParticipants) {
        this.discourseParticipants = discourseParticipants;
    }

}

