package kn.valida.discourseModel;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Sets;
import kn.valida.iatReader.Proposition;
import kn.valida.utilities.VariableHandler;

import java.util.*;

public class DiscourseOperator {
    private DiscourseModel dm;

    /*
    private Set<String> commitments = new HashSet<>();
    private Set<String> jointCommitments = new HashSet<>();

    private Set<String> unresolved = new HashSet<>();
    private Set<String> controversial = new HashSet<>();


    private Set<String> positiveCommitments = new HashSet<>();
    private Set<String> negativeCommitments = new HashSet<>();

    //For contradiction
    private Set<String> inContradictionState = new HashSet<>();
    private Set<String> contradictoryProposition = new HashSet<>();

     */


    /**
     * Given a discourse model calculate various outputs, such as commitments, joint commitments, unresolved and
     * controversial.
     * Discourse propositions are referred to via their unique id of the form p1,p2,...pn.
     * Discourse propositions can be retrieved via dm.getDpReference().get(id).
     * Outputs are usually calculated for specific states of the discourse (the discourse transitions to a new state
     * whenever a new discourse propositions is added, thus the list dm.getDiscoursePropositions() contains a
     * list of states of the discourse.
     * . The state of a given discourse is referred to via an Integer (1,2,3...n) and must be within the range:
     * 0 -- dm.getDiscoursePropositions.length-1.
     * @param dm any discourse model
     */

    public DiscourseOperator(DiscourseModel dm)
    {
        this.dm = dm;
    }

    /**
     * Calculates commitments and joint commitments for a given speaker at a given state of the discourse.
     * Commitments are those propositions that are contained in some Speaker's beliefs.
     * Joint commitments are those propositions that are contained in all speakers beliefs.
     * @param speakers List of speakers for which commitments are to be calculated
     * @param index between 0 and length of discourse propositions (-1) in dm; corresponds to point in time in debate
     * @return List of commitments and joint commitments for the selected speakers
     */

    public HashMap<String,Set> calculateCommitments(List<Speaker> speakers, Integer index)
    {
        HashMap<String,Set> out = new HashMap<>();
        out.put("jointCommitments",new HashSet());
        out.put("commitments",new HashSet());

        DiscourseProposition current = dm.getDiscoursePropositions().get(index);

        for (String key : current.getBeliefHolder().keySet())
        {
            Boolean containsAll = true;
            Boolean contains = false;
            for (Speaker s : speakers) {
                if (current.getBeliefHolder().get(key).contains(s)) {
                    contains = true;
                } else {
                    containsAll = false;
                }
            }
                if (containsAll)
                {
                        out.get("jointCommitments").add(key);

                } else if (contains)
                {
                        out.get("commitments").add(key);
                }
        }
        return out;
    }

    /**
     * Calculates commitments and joint commitments for all possible speaker combinations
     * This gets big very fast with multiple speakers (since it calculates commitments and joint commitments for the
     * powerset of the set of speakers (minus the empty set).
     * WARNING: Only works for input set size <= 30
     * @param index state of the discourse
     * @return A Hashmap of the form "{Set<Speaker> :{commitments : {p1,p2,p3...},
     *                                          jointCommitments : {p4,p5,p6}},
     *                                 ...}
     */

    public HashMap<Set<Speaker>,HashMap<String,Set>> calculateAllCommitments(Integer index)
    {

        HashMap<Set<Speaker>,HashMap<String,Set>> out = new HashMap<>();

        Set<Speaker> speakerSet = new HashSet<>();

         for (Speaker s : dm.getDiscourseParticipants())
         {
             speakerSet.add(s);
         }

        Set<Set<Speaker>> speakerPowerSet = Sets.powerSet(speakerSet);


        DiscourseProposition current = dm.getDiscoursePropositions().get(index);

        Iterator powerSetIterator = speakerPowerSet.iterator();

        while(powerSetIterator.hasNext()) {
            Set<Speaker> powerSet = (Set<Speaker>) powerSetIterator.next();
            if (!powerSet.isEmpty()) {
                Iterator setIterator = powerSet.iterator();
                for (String key : current.getBeliefHolder().keySet()) {
                    boolean containsAll = true;
                    boolean contains = false;

                    while (setIterator.hasNext()) {
                        Speaker speaker = (Speaker) setIterator.next();
                        if (current.getBeliefHolder().get(key).contains(speaker)) {
                            contains = true;
                        } else {
                            containsAll = false;
                        }
                    }

                    if (containsAll) {
                        if (!out.keySet().contains(powerSet)) {
                            out.put(powerSet, new HashMap<>());
                        }

                        if (!out.get(powerSet).keySet().contains("jointCommitments")) {
                            out.get(powerSet).put("jointCommitments", new HashSet<String>());
                        }

                        out.get(powerSet).get("jointCommitments").add(key);
                    } else if (contains) {
                        if (!out.keySet().contains(powerSet)) {
                            out.put(powerSet, new HashMap<>());
                        }

                        if (!out.get(powerSet).keySet().contains("commitments")) {
                            out.get(powerSet).put("commitments", new HashSet<String>());
                        }
                        out.get(powerSet).get("commitments").add(key);
                    }

                }

            }
        }

        return out;

    }

    /**
     * Calculates unresolved propositions which are all those propositions which are not joint commitments, i.e.
     * some but not all discourse participants are committed to these propositions.
     * This will be the majority of discourse propositions.
     * @param index Current state of discourse
     * @return A set of discourse proposition ids containing all those ids that refer to unresolved dps.
     */
    public Set<String> calculateUnresolved(Integer index)
    {
        DiscourseProposition current = dm.getDiscoursePropositions().get(index);
        HashSet<String> out = new HashSet<>();

        for (String key : current.getBeliefHolder().keySet()) {
            Boolean containsAll = true;
            if (!current.getBeliefHolder().get(key).isEmpty()) {
                for (Speaker s : dm.getDiscourseParticipants()) {
                    if (current.getBeliefHolder().get(key).contains(s)) {
                        continue;
                    } else {
                        containsAll = false;
                        break;
                    }
                }
                if (!containsAll) {
                    out.add(key);
                }
            }
        }
        return out;
    }


    /**
     * Calculates controversial discourse propositions.
     * A dp is controversial if it describes a belief held by some speaker which is rejected by some OTHER speaker
     * @param index Current state of discourse
     * @return Set containing those pids that refer to controversial discourse propositions
     */

    public Set<String> calculateControversial(Integer index)
    {
        Set<String> out = new HashSet<>();
        DiscourseProposition current = dm.getDiscoursePropositions().get(index);

        for (String key : current.getBeliefHolder().keySet())
        {
            if (!(current.getBeliefHolder().get(key).isEmpty() && current.getDeniesBelief().isEmpty()))
            {
                if (!current.getBeliefHolder().get(key).containsAll(current.getDeniesBelief().get(key)))
                {
                 out.add(key);
                }
            }
        }
        return out;
    }

    /**
     * Calculates those discourse propositions where some speaker(s) hold and deny a belief at the same time.
     * @param index State of the discourse
     * @return A hashmap, the keyset is the set of discourse proposition that are contradictory, the values are the
     * corresponding speakers
     */

    public HashMap<String,Set<Speaker>> calculateContradictory(Integer index)
    {
        HashMap<String,Set<Speaker>> out = new HashMap<>();
        DiscourseProposition current = dm.getDiscoursePropositions().get(index);

        for (String key : current.getBeliefHolder().keySet())
        {
            if (!(current.getBeliefHolder().get(key).isEmpty() && current.getDeniesBelief().isEmpty()))
            {
                Set<Speaker> speakers = new HashSet<>();
                for (Speaker s : current.getBeliefHolder().get(key))
                {
                    if (current.getDeniesBelief().containsKey(s))
                    {
                        speakers.add(s);
                    }
                }

                if (!speakers.isEmpty())
                {
                    out.put(key,speakers);
                }
            }
        }
        return out;
    }




    /**
     * Wrapper method for merging speakers. Takes as input two speakers from the discourse model (Uniquely identified
     * via their string. (TODO could be changed to speaker ID)
     * @param a First speaker
     * @param b Second speaker
     * @return A discourse model in which speaker a and b are merged. This process cannot be reversed. Thus this model
     * should be stored separately.
     */

    public DiscourseModel mergeSpeakers(Speaker a, Speaker b)
    {

        String mergedSpeaker = a.getName() + " & " + b.getName();
        Speaker combined = new Speaker(mergedSpeaker,
                (String) VariableHandler.returnNewVar(VariableHandler.variableType.SPEAKER));

        List<Speaker> mergedSpeakers = new ArrayList<>();

        for (Speaker s : dm.getDiscourseParticipants())
        {
            if (!(s == a || s == b))
            {
                mergedSpeakers.add(s);
            }
        }

        mergedSpeakers.add(combined);

        HashMap<Proposition,DiscourseProposition> mergedPropToDiscProp = new HashMap<>();

        LinkedList<DiscourseProposition> mergedDiscoursePropositions =
                mergeSpeakers2(dm.getDiscoursePropositions(),mergedPropToDiscProp,a,b,combined);

        DiscourseModel mergedDiscourseModel = new DiscourseModel(mergedDiscoursePropositions,mergedSpeakers,mergedPropToDiscProp);

        return mergedDiscourseModel;
    }

    /**
     * This method does the actual merging of speakers in the model
     * @param dps List of discourse propositions provided by discourse model
     * @param pstodps Maps propositions in IAT to propositions in the discourse model (will be changed)
     * @param a first speaker
     * @param b second speaker
     * @param combined a & b combined as done by the wrapper method
     * @return A list of discourse propositions (i.e. the discourse model) with merged speakers
     */


    public LinkedList<DiscourseProposition> mergeSpeakers2(List<DiscourseProposition> dps,
                                                           HashMap<Proposition,DiscourseProposition> pstodps,
                                                           Speaker a, Speaker b, Speaker combined)
    {
        LinkedList<DiscourseProposition> mergedDiscoursePropositions = new LinkedList<>();

        for (DiscourseProposition p : dps)
        {
            LinkedHashMap<String,List<Speaker>> newBeliefs = new LinkedHashMap<>();
            LinkedHashMap<String,List<Speaker>> newDenials = new LinkedHashMap<>();


            for (String pid : p.getBeliefHolder().keySet())
            {


                if (p.getBeliefHolder().get(pid).contains(a) || p.getBeliefHolder().get(pid).contains(b)) {

                    if (!newBeliefs.keySet().contains(pid)) {
                        newBeliefs.put(pid, new ArrayList<>());
                    }
                    newBeliefs.get(pid).add(combined);


                }

                for (Speaker s : p.getBeliefHolder().get(pid)) {
                    if (!(s == a && s == b) && p.getBeliefHolder().get(pid).contains(s)) {
                        if (!newBeliefs.keySet().contains(pid)) {
                            newBeliefs.put(pid, new ArrayList<>());
                        }
                        newBeliefs.get(pid).add(s);
                    }
                }


                if (!newDenials.keySet().contains(pid))
                {
                    newDenials.put(pid,new ArrayList<>());
                }
            }

            for (String pid : p.getDeniesBelief().keySet())
            {
                if (p.getDeniesBelief().get(pid).contains(a) || p.getDeniesBelief().get(pid).contains(b))
                {

                    if(!newDenials.keySet().contains(pid)) {
                        newDenials.put(pid, new ArrayList<>());
                    }
                    newDenials.get(pid).add(combined);


                }

                for (Speaker s : p.getDeniesBelief().get(pid))
                {
                    if (!(s == a && s == b) && p.getDeniesBelief().get(pid).contains(s))
                    {
                        if(!newDenials.keySet().contains(pid)) {
                            newDenials.put(pid, new ArrayList<>());
                        }
                        newDenials.get(pid).add(s);
                    }
                }

                if (!newBeliefs.keySet().contains(pid))
                {
                    newBeliefs.put(pid,new ArrayList<>());
                }
            }

            Speaker mergedOriginalSpeaker;

            if (p.getOriginalSpeaker().equals(a) || p.getOriginalSpeaker().equals(b))
            {
                mergedOriginalSpeaker = combined;
            } else
            {
                mergedOriginalSpeaker = p.getOriginalSpeaker();
            }

            DiscourseProposition mergedProposition = new DiscourseProposition(p.getPid(),p.getText(),mergedOriginalSpeaker,
                    newBeliefs,newDenials,p.getExpressiveContent());

            mergedProposition.setExpressiveContent(mergeSpeakers2(p.getExpressiveContent(),pstodps,a,b,combined));

            mergedProposition.setRelevance(p.getRelevance());
            // mergedProposition.setSemanticSimilarity(p.getSemanticSimilarity());


            for (Proposition key : dm.getPropToDiscProp().keySet()) {
                try {
                    if (dm.getPropToDiscProp().get(key).equals(p)) {
                        pstodps.put(key,mergedProposition);
                        break;
                    }
                }catch(Exception e)
                {System.out.println("Failed to align IAT annotation with discourse model");}
            }


            mergedDiscoursePropositions.add(mergedProposition);
        }

        return mergedDiscoursePropositions;
    }


    /**
     * Generates a .json file from the list of discourse propositions in the discourse model
     * @return A string that can be saved as a json file
     * @throws JsonProcessingException
     */

    public String writeDiscourseModelToJson() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        try {
            String jsonString = mapper.writeValueAsString(dm.getDiscoursePropositions());
            return jsonString;
        }catch(Exception e)
        {
            System.out.println("Failed to write discourse model to JSON");
            e.printStackTrace();
        }

        //TODO
        return null;

    }

    /**
     * Only writes the current state of the discourse to JSON
     * @param current Integer that refers to the current state of discourse
     *                (Full discourse is discoursePropositions.length()-1)
     * @return Json string describing a singleton list containing the current state of the discourse
     * @throws JsonProcessingException
     */

    public String writeCurrentToJson(Integer current) throws JsonProcessingException {
        DiscourseProposition currentProposition = dm.getDiscoursePropositions().get(current);
        List<DiscourseProposition> out = new ArrayList<>();
        out.add(currentProposition);
        ObjectMapper mapper = new ObjectMapper();
        try {
            String jsonString = mapper.writeValueAsString(out);
            return jsonString;
        }catch(Exception e)
        {
            System.out.println("Failed to write discourse model to JSON");
            e.printStackTrace();
        }

        //TODO
        return null;

    }

    /**
     * Use to write any Java object, e.g. the output of the methods above to JSON format
     * @param o Any Java object you want to write to JSON
     * @return JSON version of given object
     */

    public String writeObjectToJson(Object o)
    {
        ObjectMapper mapper = new ObjectMapper();
        try{
            String jsonString = mapper.writeValueAsString(o);
            return jsonString;
        }catch(Exception e)
        {
            System.out.println("Failed to write object" + o.toString() + "to JSON");
            e.printStackTrace();
        }
        return null;
    }


}
