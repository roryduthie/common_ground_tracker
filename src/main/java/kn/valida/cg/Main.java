package kn.valida.cg;


import kn.valida.discourseModel.DiscourseModel;
import kn.valida.gui.GUI;
import kn.valida.iatReader.IATmap;
import kn.valida.utilities.VariableHandler;

import javax.swing.*;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Main {

    public static GUI GUI;
    public static JFrame frame;
    public static VariableHandler vh = new VariableHandler();

    public static void main(String[] args) throws IOException {

 /*
        CSVReader reader = new CSVReaderHeaderAware(new FileReader("/Users/red_queen/Desktop/ADDF.csv"));
        //Text /DiscourseMoves.DiscourseMove /Proposition
        String[] nextLine;
        List<String[]> locutions = new ArrayList<>();
        while ((nextLine = reader.readNext()) != null) {
            // nextLine[] is an array of values from the line
            locutions.add(nextLine);

            }
            */

        File[] fileArray =  null;

        if (args.length > 0) {
            for (int i = 0; i < args.length; i++) {
                switch (args[i]) {
                    case ("-i"):
                        try {
                            File inFile = new File(args[i + 1]);
                            if (inFile.isDirectory()) {

                                 fileArray = inFile.listFiles(new FilenameFilter() {
                                    @Override
                                    public boolean accept(File dir, String name) {
                                        return name.endsWith(".json");
                                    }
                                });

                                Arrays.sort(fileArray, new Comparator<File>() {
                                    @Override
                                    public int compare(File o1, File o2) {
                                        Pattern mapID = Pattern.compile(".*-(\\d+)\\.json");

                                        Matcher o1Matcher = mapID.matcher(o1.getName());
                                        Matcher o2Matcher = mapID.matcher(o2.getName());

                                        try {
                                            if (o1Matcher.find() && o2Matcher.find()) {
                                                int i1 = Integer.parseInt(o1Matcher.group(1));
                                                int i2 = Integer.parseInt(o2Matcher.group(1));

                                                return i1 - i2;
                                            }
                                        } catch (IllegalStateException e) {
                                            throw new AssertionError(e);
                                        }
                                        return 0;

                                    }
                                });

                                //StringBuilder sb = new StringBuilder();



                                List<IATmap> maps = new ArrayList<>();


                                for (File file : fileArray) {
                                    maps.add(IATmap.analyzeIATmap(file));
                                }


                                DiscourseModel dm = new DiscourseModel(maps);

                                frame = new JFrame("Main");
                                GUI mainGUI = new GUI(frame,dm);
                                GUI = mainGUI;

                                frame.setContentPane(GUI.mainPanel);
                                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                                //For some reason this order centers the GUI properly
                                frame.pack();
                                frame.setSize(520,680);
                                frame.setLocationRelativeTo(null);
                                frame.setVisible(true);




                                //          String jsonIn = sb.toString();

                      //          JSONObject json = new JSONObject(jsonIn);

                    //            System.out.println(json.toString());


                            }
                        } catch (Exception e) {
                            System.out.println("Error while reading file.");
                        }


                        break;
                    case ("-o"):

                        break;
                }
            }
        } else if (args.length == 0)
        {

            frame = new JFrame("Main");
            GUI mainGUI = new GUI(frame);
            GUI = mainGUI;

            frame.setContentPane(GUI.mainPanel);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            //For some reason this order centers the GUI properly
            frame.pack();
            frame.setSize(520,680);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

           // System.out.println("Specify a directory where the argument maps are stored with the '-i' argument, e.g. '-i sample/directoy'");
        }


        }









  /*
        for (Locution locution : locutions)
        {
            System.out.println(locution);
       }

  */
}


