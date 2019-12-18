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
        for (IATmap map : annotatedDebate)
        {
            for (String s : map.getSpeakers())
            {
                if (isParticipant(s,discourseParticipants)==null)
                {
                    discourseParticipants.add(new Speaker(s,
                            (String) VariableHandler.returnNewVar(VariableHandler.variableType.SPEAKER)));
                }

            }
        }

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
            System.out.println("Now processing: \"" + map.fileName +"\"");

           DiscourseProposition dp = null;

            for (Locution l : map.getLocutions())
            {

                System.out.println("Current locution(id): " + l.getJsonID());

                dp = null;

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

                            discoursePropositions.add(dp);

                            System.out.println("Generated proposition " + dp.getPid() + ": " + dp.getText());

                            //Direct moves (i.e. directly anchored to the locution; aside from assertion)
                            List<Node> conventionalImplicatures = Edge.findRelatedDaugtherContentNode(l,map.getNodes(),
                                                                    map.getEdges(),"CI Asserting");

                            //TODO A implicating = CI Asserting?
                            conventionalImplicatures.addAll(Edge.findRelatedDaugtherContentNode(l,map.getNodes(),
                                    map.getEdges(),"A Implicating"));

                            if (!conventionalImplicatures.isEmpty())
                            {
                                for (Node ci : conventionalImplicatures)
                                {
                                    if (ci instanceof Proposition)
                                    {
                                      DiscourseProposition expressiveProposition = initializeDP(ci,l,discoursePropositions);
                                      dp.getExpressiveContent().add(expressiveProposition);


                                      addPropositon(discourseParticipants,expressiveProposition,dp);

                                      //dp.getBeliefHolder().put(expressiveProposition.getPid(),
                                     //         expressiveProposition.getBeliefHolder().get(expressiveProposition.getPid()));


                                    //  dp.getDeniesBelief().put(expressiveProposition.getPid(),new ArrayList<>());
                                        System.out.println("Generated expressive proposition " + expressiveProposition.getPid() + ": " + expressiveProposition.getText());
                                    }
                                }

                            }


                            //Indirect moves (i.e. via transition)
                            List<Node> motherNodes = Edge.findMother(l, map.getEdges(), "Default Transition");

                            agreeMove(motherNodes,map,dp);

                            disagreeMove(motherNodes,map,dp);

                            argueMove(motherNodes,map,dp);


                            }


                        }
                    }
                    //Agree moves independent of assertion
                else {
                    try {
                        DiscourseProposition empty = initializeDP(l, discoursePropositions);

                        List<Node> motherNodes = Edge.findMother(l, map.getEdges(), "Default Transition");

                        agreeMove(motherNodes, map, empty);

                        disagreeMove(motherNodes, map, empty);

                        discoursePropositions.add(empty);


                    } catch (Exception e) {
                        System.out.println("Failed to introduce new discourse move");
                    }

                }

                    }



                }


        return discoursePropositions;

    }

    public static Speaker isParticipant(String name, List<Speaker> discourseParticipants)
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

        try {
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
        } catch(Exception e)
        {
            System.out.println("Couldn't copy beliefholders to new proposition.");
        }
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
        Speaker s = isParticipant(l.getSpeaker(),discourseParticipants);

        //TODO this is sort of a hack?
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

    public void addPropositon(List<Speaker> speakers,DiscourseProposition p,DiscourseProposition currentProposition)
    {
        LinkedHashMap<String,List<Speaker>> bh = currentProposition.getBeliefHolder();
        LinkedHashMap<String,List<Speaker>> db = currentProposition.getDeniesBelief();
        String newID = p.getPid();

        try{
            if (!bh.containsKey(newID))
            {
                bh.put(newID,new ArrayList<>());
            }
            if (!db.containsKey(newID))
            {
                db.put(newID,new ArrayList<>());
            }
            for (Speaker s : speakers)
            {
                if (db.get(p.getPid()).contains(s))
                {
                    bh.get(p.getPid()).remove(s);
                    System.out.println("Speaker " + s.toString() + "contradicts himself. Resolved controversial stance by retracting older conflicting belief" );
                }
                bh.get(p.getPid()).add(s);
            }
        }
        catch (Exception e) {
            System.out.println("Failed to add proposition " + p.toString());
        }
    }


    public DiscourseProposition initializeDP(Locution l, LinkedList<DiscourseProposition> intermediatePropositionList)
    {
        String pid = (String) VariableHandler.returnNewVar(VariableHandler.variableType.PROPOSITION);
        DiscourseProposition dp = new DiscourseProposition(pid,"discourse move(" + l.getText() +")");
        Speaker s = isParticipant(l.getSpeaker(),discourseParticipants);
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
        return dp;
    }

    public void agreeMove(List<Node> transitionNodes, IATmap map, DiscourseProposition dp) {
        //Indirect moves (i.e. via transition)

        for (Node transition : transitionNodes) {

            //Discourse move agree
            List<Node> agreePropositions = Edge.findRelatedDaugtherContentNode(transition, map.getNodes(), map.getEdges(), "Agreeing");

            if (!agreePropositions.isEmpty()) {

                for (Node p : agreePropositions) {
                    dp.getBeliefHolder().get(propToDiscProp.get(p).getPid()).add(dp.getOriginalSpeaker());
                    if (dp.getDeniesBelief().get(propToDiscProp.get(p).getPid()).contains(dp.getOriginalSpeaker()))
                    {
                        dp.getDeniesBelief().get(propToDiscProp.get(p).getPid()).remove(dp.getOriginalSpeaker());
                    }
                }
            }
        }
    }

    public void disagreeMove(List<Node> transitionNodes, IATmap map, DiscourseProposition dp) {

        for (Node transition : transitionNodes) {

            List<Node> disagreePropositions = Edge.findRelatedDaugtherContentNode(transition, map.getNodes(), map.getEdges(), "Disagreeing");
            List<Node> cidisagreePropositions = Edge.findRelatedDaugtherContentNode(transition, map.getNodes(), map.getEdges(), "CI Disagreeing");

            if (!cidisagreePropositions.isEmpty()) {
                disagreePropositions.addAll(cidisagreePropositions);
            }

            if (!disagreePropositions.isEmpty()) {

                for (Node p : disagreePropositions) {

                    List<Node> conflictDaugther = Edge.findDaughter(p, map.getEdges(), "I");
                    //                List<Node> conflictMother = Edge.findMother(p, map.getEdges(), "I");


                    for (Node q : conflictDaugther) {
                        if (q instanceof Proposition) {
                            if (propToDiscProp.keySet().contains(q)) {

                                //Retract commitment if it is in conflict with previous agreement
                                if (dp.getOriginalSpeaker().equals(propToDiscProp.get(q).getOriginalSpeaker())) {
                                    dp.getBeliefHolder().get(propToDiscProp.get(q).getPid()).remove(propToDiscProp.get(q).getOriginalSpeaker());
                                } else {
                                    dp.getDeniesBelief().get(propToDiscProp.get((q)).getPid()).add(dp.getOriginalSpeaker());
                                }
                            } else {
                                System.out.println("Proposition " + p + "has no corresponding element in the discourse model.");
                                System.out.println("Proposition has not been introduced via a locution.");
                            }

                        }
                    }
                }
            }
        }
    }

    public void argueMove(List<Node> transitionNodes,IATmap map, DiscourseProposition dp)
    {
        for (Node transition : transitionNodes)
        {
            List<Node> argueNodes = Edge.findRelatedDaugtherContentNode(transition,map.getNodes(),map.getEdges(),"Arguing");


            if (!argueNodes.isEmpty()) {
                for (Node p : argueNodes) {
                    List<Node> arguePropositions = Edge.findDaughter(p,map.getEdges(),"I");

                    for (Node q : arguePropositions)
                    {
                        if (q instanceof Proposition)
                        {
                            if (propToDiscProp.keySet().contains(q))
                            {
                                //Retract commitment if it is in conflict with previous agreement
                                if (!dp.getOriginalSpeaker().equals(propToDiscProp.get(q).getOriginalSpeaker())) {
                                    dp.getBeliefHolder().get(propToDiscProp.get((q)).getPid()).add(dp.getOriginalSpeaker());
                                }

                            }
                        }
                    }

                }

            }
        }

    }
}


