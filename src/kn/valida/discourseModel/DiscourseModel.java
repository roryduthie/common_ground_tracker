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
           DiscourseProposition dp = null;
            for (Locution l : map.getLocutions())
            {
                dp = null;
                //Copy old information to current proposition
                locutions.add(l);


                List<Node> daugtherNodes = Edge.findRelatedDaugtherContentNode(l,map.getNodes(), map.getEdges(),"Asserting");
                //DiscourseProposition dp = null;
                //First try to find locutions that initialize proposition

                if (!daugtherNodes.isEmpty()) {

                    for (Node daugtherNode : daugtherNodes) {
                        dp = null;
                        if (daugtherNode instanceof Proposition) {
                            /*
                            String pid = (String) VariableHandler.returnNewVar(VariableHandler.variableType.PROPOSITION);
                            dp = new DiscourseProposition(pid, daugtherNode.getText());
                            Speaker s = isParticipant(l.getSpeaker());
                            if (s == null) {
                                s = new Speaker(l.getSpeaker(),
                                        (String) VariableHandler.returnNewVar(VariableHandler.variableType.SPEAKER));
                                discourseParticipants.add(s);
                            }

                            //Copies the previous common ground to the current proposition
                            if (!discoursePropositions.isEmpty()) {
                                updateDiscourseProposition(discoursePropositions,dp);
                            }


                            dp.setOriginalSpeaker(s);
                            List<Speaker> believeHolders = new ArrayList<>();
                            believeHolders.add(s);
                            dp.getBeliefHolder().put(pid, believeHolders);
                            dp.getDeniesBelief().put(pid, new ArrayList<>());

                             */

                           dp = initializeDP(daugtherNode,l,discoursePropositions);

                            //}else{
                            //    dp = new DiscourseProposition(pid, p.getText());
                        }

                        if (dp != null) {


                            //Direct moves (i.e. directly anchored to the locution; aside from assertion)

                            List<Node> conventionalImplicatures = Edge.findRelatedDaugtherContentNode(l,map.getNodes(),
                                                                    map.getEdges(),"CI Asserting");

                            if (!conventionalImplicatures.isEmpty())
                            {
                                for (Node ci : conventionalImplicatures)
                                {
                                    if (ci instanceof Proposition)
                                    {
                                      DiscourseProposition expressiveProposition = initializeDP(ci,l,discoursePropositions);
                                      dp.getExpressiveContent().add(expressiveProposition);

                                      dp.getBeliefHolder().put(expressiveProposition.getPid(),
                                              expressiveProposition.getBeliefHolder().get(expressiveProposition.getPid()));
                                      dp.getDeniesBelief().put(expressiveProposition.getPid(),new ArrayList<>());

                                    }
                                }

                            }



                            //Indirect moves (i.e. via transition)
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


                                                if (propToDiscProp.keySet().contains(p)) {
                                                    dp.getDeniesBelief().get(propToDiscProp.get(p).getPid()).add(dp.getOriginalSpeaker());
                                                }else
                                                {
                                                    System.out.println("Proposition " + p + "has no corresponding element in the discourse model.");
                                                    System.out.println("Proposition has not been introduced via a locution.");
                                                }

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


    /*
                                if (!discoursePropositions.isEmpty()) {



                            }


     */


    public void updateDiscourseProposition(LinkedList<DiscourseProposition> discoursePropositions,DiscourseProposition dp)
    {
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

    /*


     */

    public DiscourseProposition initializeDP(Node daugtherNode,Locution l, LinkedList<DiscourseProposition> intermediatePropositionList)
    {
        String pid = (String) VariableHandler.returnNewVar(VariableHandler.variableType.PROPOSITION);
        DiscourseProposition dp = new DiscourseProposition(pid, daugtherNode.getText());
        Speaker s = isParticipant(l.getSpeaker());
        if (s == null) {
            s = new Speaker(l.getSpeaker(),
                    (String) VariableHandler.returnNewVar(VariableHandler.variableType.SPEAKER));
            discourseParticipants.add(s);
        }

        //Copies the previous common ground to the current proposition
        if (!intermediatePropositionList.isEmpty()) {
            updateDiscourseProposition(intermediatePropositionList,dp);
        }


        dp.setOriginalSpeaker(s);
        List<Speaker> believeHolders = new ArrayList<>();
        believeHolders.add(s);
        dp.getBeliefHolder().put(pid, believeHolders);
        dp.getDeniesBelief().put(pid, new ArrayList<>());

        try {
            propToDiscProp.put((Proposition) daugtherNode, dp);
        }catch(Exception e)
        {
            System.out.println("Failed to align propositions with discourse model");
        }

        return dp;
    }




}

