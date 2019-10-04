package kn.valida.utilities;

import java.awt.*;
import java.util.HashMap;

public class VariableHandler {
    public enum variableType{
        SPEAKER,
        PROPOSITION,
        COLOR
    }

    private static Integer usedColor;
    private static HashMap<Integer,Color> availableColors = availableColors();
    private static Integer sids;
    private static Integer sid_size = 10000;
    private static Integer pids;
    private static Integer pid_size = 10000;




  //  private Map<variableType,List<String>> usedVariables = usedVars();


    public VariableHandler()
    {
    }

/*
    public Map<variableType,List<String >> usedVars(){
        Map<variableType,List<String>> usedVars = new HashMap();
        for (variableType type : variableType.values())
        {
            usedVars.put(type,new ArrayList<String>());
        }
        return usedVars;
    }
*/

    public static Object returnNewVar(variableType type){


        switch(type) {
            case SPEAKER:

                if (sids == null)
                {
                    sids = 0;
                }else{
                    if (sid_size >= sids) {
                        sids++;
                    }else
                    {
                        System.out.println("Exceeded maximum number of speaker variables");
                        return null;
                    }

                }
                return "s" + sids.toString();


            case PROPOSITION:
                if (pids == null)
                {
                    pids = 0;
                }else{
                    if (pid_size >= pids) {
                        pids++;
                    }else
                    {
                        System.out.println("Exceeded maximum number of proposition variables");
                        return null;
                    }
                }
                return "p" + pids.toString();

            case COLOR:
                if (usedColor == null)
                {
                    usedColor = 1;
                }else{
                    if (availableColors.keySet().size() >= usedColor) {
                        usedColor++;
                    }else
                    {
                        System.out.println("Exceeded maximum number of color variables");
                        return null;
                    }
                }
                return availableColors.get(usedColor);
        }
        return null;
    }

    public static HashMap<Integer,Color> availableColors()
    {
        HashMap<Integer,Color> availableColors = new HashMap<>();

        availableColors.put(1,new Color(255,128,0,20));
        availableColors.put(2,new Color(0,128,255,20));
        availableColors.put(3,new Color(255,0,255,20));
        availableColors.put(4,new Color(0,153,0,20));
        availableColors.put(5,new Color(0,255,255,20));
        availableColors.put(6,new Color(204,0,0,20));
        availableColors.put(7,new Color(153,0,153,20));
        availableColors.put(8,new Color(255,255,0,20));
        availableColors.put(9,new Color(0,76,153,20));
        //TODO more colors to be specified?
        availableColors.put(10,new Color(255,0,0,20));
        availableColors.put(11,new Color(255,0,0,20));
        availableColors.put(12,new Color(255,0,0,20));
        availableColors.put(13,new Color(255,0,0,20));
        availableColors.put(14,new Color(255,0,0,20));
        availableColors.put(15,new Color(255,0,0,20));
        availableColors.put(16,new Color(255,0,0,20));
        availableColors.put(17,new Color(255,0,0,20));
        availableColors.put(18,new Color(255,0,0,20));
        availableColors.put(19,new Color(255,0,0,20));
        availableColors.put(20,new Color(255,0,0,20));

        return availableColors;
    }


}

