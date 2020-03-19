package kn.valida.gui;

import kn.valida.discourseModel.*;
import kn.valida.iatReader.IATmap;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.List;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GUI {
    private JList propositionList;
    public JPanel mainPanel;
    private JList speakerList;
    private JButton showCommitmentsButton;
    private JButton showControversialButton;
    private JButton showUnresolvedButton;
    private JButton goBackwardButton;
    private JButton goForwardButton;
    private JLabel propositionIDLabel;
    private JScrollPane speakerScrollPane;
    private JScrollPane propositionScrollPane;
    private JButton loadIATmapButton;
    private JButton goBackwardFastButton;
    private JButton goForwardFastButton;
    private JButton mergeSpeakersButton;
    private JButton showRelevanceButton;
    private JButton showCommitmentRatingsButton;
    private JButton showContradictoryButton;
    private JButton writeToJSONButton;
    private JButton calculateAllComitmentsButton;

    private DiscourseModel dm;
    private DiscourseOperator operator;

    public JFrame guiFrame;

    public Integer locus = 0;

    public static HashMap<String, Color> speakerColor = new HashMap<>();


    private ListSelectionModel propositionSelection = new DefaultListSelectionModel() {
        boolean gestureStarted = false;

        @Override
        public void setSelectionInterval(int index0, int index1) {

            if (!gestureStarted) {
                if (isSelectedIndex(index0)) {
                    super.removeSelectionInterval(index0, index1);
                    ((SpeakerRenderer) speakerList.getCellRenderer()).setHighlightElements(new HashSet<>());
                    speakerList.repaint();

                } else {
                    super.clearSelection();
                    super.addSelectionInterval(index0, index1);


                    ((SpeakerRenderer) speakerList.getCellRenderer()).setHighlightElements(new HashSet<>());
                    speakerList.repaint();


                    String key = ((DiscourseProposition) propositionList.getModel().getElementAt(index0)).getPid();
                    //Adds belief holders of the proposition at the current time in discourse!

                    try {
                        for (Speaker s : ((DiscourseProposition) propositionList.getModel().getElementAt(locus)).getBeliefHolder().
                                get(key)) {
                            ((SpeakerRenderer) speakerList.getCellRenderer()).getHighlightElements().add(s.getSid());
                        }
                        speakerList.repaint();
                    } catch (Exception e) {
                        System.out.println("This element has no propositional content. If it is a discourse move this is intended.");
                    }

                }
            }
            gestureStarted = true;
        }

        @Override
        public void setValueIsAdjusting(boolean isAdjusting) {
            if (isAdjusting == false) {
                gestureStarted = false;
            }
        }
    };


    private ListSelectionModel speakerSelectionModel = new DefaultListSelectionModel() {
        boolean gestureStarted = false;

        @Override
        public void setSelectionInterval(int index0, int index1) {
            if (!gestureStarted) {
                if (isSelectedIndex(index0)) {
                    super.removeSelectionInterval(index0, index1);
                    ((PropositionRenderer) propositionList.getCellRenderer()).setHighlightCommitments(new HashSet<>());
                    ((PropositionRenderer) propositionList.getCellRenderer()).setHighlightJointCommitments(new HashSet<>());
                    propositionList.repaint();

                } else {
                    super.addSelectionInterval(index0, index1);
                    ((PropositionRenderer) propositionList.getCellRenderer()).setHighlightCommitments(new HashSet<>());
                    ((PropositionRenderer) propositionList.getCellRenderer()).setHighlightJointCommitments(new HashSet<>());
                    propositionList.repaint();


                }
            }
            gestureStarted = true;
        }

        @Override
        public void setValueIsAdjusting(boolean isAdjusting) {
            if (isAdjusting == false) {
                gestureStarted = false;
            }
        }
    };


    ActionListener loadListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            final JFileChooser dir = new JFileChooser();
            dir.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int returnVal = dir.showOpenDialog(guiFrame);

            if (returnVal == JFileChooser.APPROVE_OPTION) {

                File inFile = dir.getSelectedFile();


                if (inFile.isDirectory()) {

                    File[] fileArray = inFile.listFiles(new FilenameFilter() {
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


                    //          String jsonIn = sb.toString();

                    //          JSONObject json = new JSONObject(jsonIn);

                    //            System.out.println(json.toString());


                    List<IATmap> maps = new ArrayList<>();


                    for (File file : fileArray) {
                        try {
                            maps.add(IATmap.analyzeIATmap(file));
                        } catch (Exception exc) {
                            System.out.println("Couldn't load file: " + file.toString());
                        }
                    }


                    dm = new DiscourseModel(maps);
                    operator = new DiscourseOperator(dm);

                    initializeGUI();

                }


            } else if (returnVal == JFileChooser.CANCEL_OPTION) {
                System.out.println("Directory selection cancelled.");
            } else if (returnVal == JFileChooser.ERROR_OPTION) {
                System.out.println("Some error occured while loading files from directory");
            } else {
                System.out.println("unknown...");

            }

        }
    };


    public GUI(JFrame frame, DiscourseModel dm) {


        this.dm = dm;
        this.operator = new DiscourseOperator(dm);
        this.guiFrame = frame;
        this.propositionIDLabel.setText(locus.toString());

        initializeGUI();

        /*
           if (goBackwardButton.getActionListeners().length == 0) {
        goForwardButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ((PropositionRenderer) propositionList.getCellRenderer()).resetLists();
                propositionList.repaint();
                if (locus < (dm.getDiscoursePropositions().size() - 1)) {
                    locus++;
                    propositionIDLabel.setText(locus.toString());
            //        DiscourseProposition[] propositions = new DiscourseProposition[locus + 1];



                    for (int i = 0; i < propositions.length; i++) {
                        propositions[i] = dm.getDiscoursePropositions().get(i);
                    }

                    propositionList = new JList(propositions);
                    propositionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                    propositionList.setCellRenderer(new PropositionRenderer());
                    propositionList.setSelectionModel(propositionSelection);
                    propositionScrollPane.setViewportView(propositionList);

        ((DefaultListModel) propositionList.getModel()).addElement(dm.getDiscoursePropositions().get(locus));
    }
}
        });
                }
                */



    }

    public GUI(JFrame frame) {
        this.guiFrame = frame;
        this.propositionIDLabel.setText(locus.toString());

        loadIATmapButton.addActionListener(loadListener);


    }


    public void initializeGUI() {
        //Rendering of the speaker list
        Speaker[] speakers = new Speaker[dm.getDiscourseParticipants().size()];
        for (int i = 0; i < dm.getDiscourseParticipants().size(); i++) {
            speakers[i] = dm.getDiscourseParticipants().get(i);
        }

        speakerList = new JList(speakers);
        speakerList.getSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        speakerList.setCellRenderer(new SpeakerRenderer());
        speakerList.setSelectionModel(speakerSelectionModel);
        speakerScrollPane.setViewportView(speakerList);

        //Initial rendering of the discourse proposition list
        //DiscourseProposition[] propositions = new DiscourseProposition[locus + 1];

        DefaultListModel<DiscourseProposition> propListModel = new DefaultListModel<>();
        for (int i = 0; i <= locus; i++) {
            propListModel.add(i, dm.getDiscoursePropositions().get(i));
        }


        propositionList = new JList(propListModel);
        propositionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        propositionList.setCellRenderer(new PropositionRenderer(dm.getDpReference()));
        propositionList.setSelectionModel(propositionSelection);


        propositionScrollPane.setViewportView(propositionList);


        //Intialize all the buttons

        //Navigating through discourse propositions

        if (goForwardButton.getActionListeners().length == 0) {
            goForwardButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    ((PropositionRenderer) propositionList.getCellRenderer()).resetLists();
                    propositionList.repaint();
                    if (locus < (dm.getDiscoursePropositions().size() - 1)) {
                        locus++;
                        propositionIDLabel.setText(locus.toString());
                        //        DiscourseProposition[] propositions = new DiscourseProposition[locus + 1];


                    /*
                    for (int i = 0; i < propositions.length; i++) {
                        propositions[i] = dm.getDiscoursePropositions().get(i);
                    }

                    propositionList = new JList(propositions);
                    propositionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                    propositionList.setCellRenderer(new PropositionRenderer());
                    propositionList.setSelectionModel(propositionSelection);
                    propositionScrollPane.setViewportView(propositionList);
*/
                        ((DefaultListModel) propositionList.getModel()).addElement(dm.getDiscoursePropositions().get(locus));

                    }

                }
            });
        }


        if (goBackwardButton.getActionListeners().length == 0) {
            goBackwardButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    ((PropositionRenderer) propositionList.getCellRenderer()).resetLists();
                    propositionList.repaint();
                    if (locus > 0) {
                        ((DefaultListModel) propositionList.getModel()).remove(locus);
                        locus = locus - 1;
                        propositionIDLabel.setText(locus.toString());


                    /*

                    DiscourseProposition[] propositions = new DiscourseProposition[locus + 1];

                    for (int i = 0; i < propositions.length; i++) {
                        propositions[i] = dm.getDiscoursePropositions().get(i);
                    }

  */
/*
                    propositionList = new JList(propositions);
                    propositionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                    propositionList.setCellRenderer(new PropositionRenderer());
                    propositionList.setSelectionModel(propositionSelection);
                    propositionScrollPane.setViewportView(propositionList);
*/

                    }
                }
            });
        }

        if (goBackwardFastButton.getActionListeners().length == 0) {
            goBackwardFastButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {

                    ((PropositionRenderer) propositionList.getCellRenderer()).resetLists();
                    propositionList.repaint();
                    if ((locus - 10) > 0) {

                        int i = 0;
                        while (i < 10) {
                            ((DefaultListModel) propositionList.getModel()).remove(locus - i);
                            i++;
                        }
                        locus = locus - 10;
                        propositionIDLabel.setText(locus.toString());

                    }
                }
            });
        }

        if (goForwardFastButton.getActionListeners().length == 0) {
            goForwardFastButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    ((PropositionRenderer) propositionList.getCellRenderer()).resetLists();
                    propositionList.repaint();
                    if (locus < (dm.getDiscoursePropositions().size() - 10)) {

                        //        DiscourseProposition[] propositions = new DiscourseProposition[locus + 1];

                        int i = 1;
                        while (i < 11) {
                            ((DefaultListModel) propositionList.getModel()).addElement(dm.getDiscoursePropositions().get(locus + i));
                            i++;
                        }
                        locus = locus + 10;
                        propositionIDLabel.setText(locus.toString());

                    } else {
                        ActionEvent event;
                        long when;

                        when = System.currentTimeMillis();
                        event = new ActionEvent(goForwardButton, ActionEvent.ACTION_PERFORMED, "Anything", when, 0);

                        for (ActionListener listener : goForwardButton.getActionListeners()) {
                            listener.actionPerformed(event);
                        }

                    }
                }
            });
        }

        if (showCommitmentsButton.getActionListeners().length == 0) {
            showCommitmentsButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    ((PropositionRenderer) propositionList.getCellRenderer()).resetLists();
                    propositionList.repaint();

                    if (speakerList.isSelectionEmpty()) {
                        JOptionPane.showMessageDialog(guiFrame, "Select at least one speaker to highlight commitments");
                    } else {

                        List<Speaker> speakers = new ArrayList<>();
                        for (int i : speakerList.getSelectedIndices())
                        {
                            speakers.add((Speaker) speakerList.getModel().getElementAt(i));
                        }

                        HashMap<String,Set> commitments = operator.calculateCommitments(speakers,locus);

                        ((PropositionRenderer) propositionList.getCellRenderer()).
                                setHighlightCommitments(commitments.get("commitments"));

                        ((PropositionRenderer) propositionList.getCellRenderer()).
                                setHighlightJointCommitments(commitments.get("jointCommitments"));


                        /*
                        DiscourseProposition current = (DiscourseProposition) propositionList.getModel().getElementAt(locus);

                        //List<String>


                        for (String key : current.getBeliefHolder().keySet()) {
                            Boolean jointCommitment = true;
                            Boolean commitment = false;
                            for (int i : speakerList.getSelectedIndices()) {
                                if (current.getBeliefHolder().get(key).
                                        contains(speakerList.getModel().getElementAt(i))) {
                                    commitment = true;
                                } else {
                                    jointCommitment = false;
                                }
                            }

                            if (jointCommitment) {
                                ((PropositionRenderer) propositionList.getCellRenderer()).getHighlightJointCommitments().add(key);
                            } else if (commitment) {
                                ((PropositionRenderer) propositionList.getCellRenderer()).getHighlightCommitments().add(key);
                            }


                        }

                        // speakerList.clearSelection();

                         */
                        propositionList.repaint();

                    }
                }
            });
        }

        //TODO If both deny belief it's also a joint commitment
        if (showCommitmentRatingsButton.getActionListeners().length == 0) {
            showCommitmentRatingsButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    ((PropositionRenderer) propositionList.getCellRenderer()).resetLists();
                    propositionList.repaint();

                    if (speakerList.isSelectionEmpty()) {
                        JOptionPane.showMessageDialog(guiFrame, "Select at least one speaker to highlight commitments");
                    } else {

                        DiscourseProposition current = (DiscourseProposition) propositionList.getModel().getElementAt(locus);

                        ((PropositionRenderer) propositionList.getCellRenderer()).setCurrentPid(
                                (current.getPid()));

                        for (String key : current.getPositiveCommitments().keySet()) {
                          //  Boolean jointCommitment = true;
                          //  Boolean commitment = false;
                            for (int i : speakerList.getSelectedIndices()) {

                                if (!((PropositionRenderer) propositionList.getCellRenderer()).getSelectedSpeakers().contains((Speaker) speakerList.getModel().getElementAt(i))) {
                                    ((PropositionRenderer) propositionList.getCellRenderer()).getSelectedSpeakers().
                                            add((Speaker) speakerList.getModel().getElementAt(i));
                                }

                                for (Commitment c : current.getPositiveCommitments().get(key)) {
                                    if (speakerList.getModel().getElementAt(i).equals(c.getCommitmentHolder())) {

                                        if (!((PropositionRenderer) propositionList.getCellRenderer()).getHighlightPositiveCommitments().contains(key))
                                        {
                                            ((PropositionRenderer) propositionList.getCellRenderer()).getHighlightPositiveCommitments().add(key);
                                        }
                                    }
                                    }
                                }
                            }


                                /*
                                if (current.getBeliefHolder().get(key).
                                        contains(speakerList.getModel().getElementAt(i))) {
                                    commitment = true;
                                } else {
                                    jointCommitment = false;
                                }
                            }

                            if (jointCommitment) {
                                ((PropositionRenderer) propositionList.getCellRenderer()).getHighlightJointCommitments().add(key);
                            } else if (commitment) {
                                ((PropositionRenderer) propositionList.getCellRenderer()).getHighlightCommitments().add(key);
                            }


                        }


                                 */
                        // speakerList.clearSelection();

                        propositionList.repaint();


                    }

                }
            });

        }


        if (loadIATmapButton.getActionListeners().length == 0) {
            loadIATmapButton.addActionListener(loadListener);
        }

        if (showUnresolvedButton.getActionListeners().length == 0) {
            showUnresolvedButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    ((PropositionRenderer) propositionList.getCellRenderer()).resetLists();
                    propositionList.repaint();

                    Set<String> unresolved = operator.calculateUnresolved(locus);

                    ((PropositionRenderer) propositionList.getCellRenderer()).setHighlightUnresolved(unresolved);


                    /*
                    DiscourseProposition current = (DiscourseProposition) propositionList.getModel().getElementAt(locus);

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
                                ((PropositionRenderer) propositionList.getCellRenderer()).getHighlightUnresolved().add(key);
                            }
                        }
                    }

                     */
                    propositionList.repaint();

                }
            });
        }
        if (showControversialButton.getActionListeners().length == 0) {

            showControversialButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    ((PropositionRenderer) propositionList.getCellRenderer()).resetLists();
                    propositionList.repaint();

                    ((PropositionRenderer) propositionList.getCellRenderer()).
                            setHighlightControversial(operator.calculateControversial(locus));

                    propositionList.repaint();
                    /*
                    for (String key : (current.getBeliefHolder().keySet())) {
                        if (!current.getBeliefHolder().get(key).isEmpty() && !current.getDeniesBelief().get(key).isEmpty()) {
                            getHighlightControversial().add(key);
                        }
                    }*/

                }
            });
        }
        if (showContradictoryButton.getActionListeners().length==0) {
            showContradictoryButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    ((PropositionRenderer) propositionList.getCellRenderer()).resetLists();
                    propositionList.repaint();

                    ((PropositionRenderer) propositionList.getCellRenderer()).
                            setContradictoryProposition(operator.calculateContradictory(locus).keySet());

                    propositionList.repaint();

                    /*
                    DiscourseProposition current = (DiscourseProposition) propositionList.getModel().getElementAt(locus);

                    if (speakerList.getSelectedIndices().length==0) {
                        JOptionPane.showMessageDialog(guiFrame, "Select at least one speaker to show contradictions");
                    } else {


                        for (String key : current.getBeliefHolder().keySet()) {
                            for (int i : speakerList.getSelectedIndices()) {
                                Speaker s = (Speaker) speakerList.getModel().getElementAt(i);

                                if (current.getBeliefHolder().get(key).contains(s) && current.getDeniesBelief().get(key).contains(s)) {
                                    ((PropositionRenderer) propositionList.getCellRenderer()).getContradictoryProposition().
                                            add(key);
                                }
                            }
                        }
                    }
                }
                     */
                }
            });
        }

/*
        if (cosineSimilarityButton.getActionListeners().length == 0) {
            cosineSimilarityButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    ((PropositionRenderer) propositionList.getCellRenderer()).resetLists();
                    propositionList.repaint();

                    DiscourseProposition current = (DiscourseProposition) propositionList.getModel().getElementAt(locus);

                    ((PropositionRenderer) propositionList.getCellRenderer()).setHighlightCosine(true);

                    ((PropositionRenderer) propositionList.getCellRenderer()).setCurrentPid(
                            (current.getPid()));


                    for (String key : current.getSemanticSimilarity().keySet()) {
                        ((PropositionRenderer) propositionList.getCellRenderer()).getHighlightCosineSimilarity().add(key);
                    }



                }
            });
        }
*/

        if (showRelevanceButton.getActionListeners().length == 0) {
            showRelevanceButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    ((PropositionRenderer) propositionList.getCellRenderer()).resetLists();
                    propositionList.repaint();

                    DiscourseProposition current = (DiscourseProposition) propositionList.getModel().getElementAt(locus);

                    ((PropositionRenderer) propositionList.getCellRenderer()).setHighlightRel(true);

                    ((PropositionRenderer) propositionList.getCellRenderer()).setCurrentPid(
                            (current.getPid()));

                    for (String key : current.getRelevance().keySet()) {
                        ((PropositionRenderer) propositionList.getCellRenderer()).getHighlightRelevance().add(key);
                    }
                }
            });

        }


        if (mergeSpeakersButton.getActionListeners().length == 0) {
            mergeSpeakersButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (speakerList.getSelectedIndices().length == 2) {

                        DiscourseModel dm2 = operator.mergeSpeakers((Speaker) speakerList.getModel().getElementAt(speakerList.getSelectedIndices()[0]),
                                (Speaker) speakerList.getModel().getElementAt(speakerList.getSelectedIndices()[1]));

                        JFrame frame = new JFrame("Main");
                        GUI mainGUI = new GUI(frame, dm2);

                        frame.setContentPane(mainGUI.mainPanel);
                        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                        //For some reason this order centers the GUI properly
                        frame.pack();
                        frame.setSize(520, 680);
                        frame.setLocationRelativeTo(null);
                        frame.setVisible(true);

                    } else {
                        System.out.println("Can only merge two speakers at one time");
                    }
                }
            });
        }

        if (writeToJSONButton.getActionListeners().length == 0) {
            writeToJSONButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {

                    final JFileChooser saveFile = new JFileChooser();

                    saveFile.addChoosableFileFilter(new FileNameExtensionFilter("json files", "json"));
                    //FileChooser.ExtensionFilter("treebank files (*.treebank)","*.treebank"))


                    // saveFile.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
                    //int returnVal = saveFile.showOpenDialog(guiFrame);

                    if (saveFile.showSaveDialog(guiFrame) == JFileChooser.APPROVE_OPTION) {
                        File dir = saveFile.getSelectedFile();

                        if (dir.toPath().toString().matches(".+\\.json")) {

                            try {
                                FileOutputStream fos = new FileOutputStream(dir.toPath().toString());
                                ObjectOutputStream oos = new ObjectOutputStream(fos);
                                oos.writeObject(operator.writeDiscourseModelToJson());
                                oos.close();
                            } catch (NotSerializableException nse) {
                                System.out.println("Serialization error!");
                            } catch (IOException ex) {
                                System.out.println("IO Error");
                            }

                        } else {
                            JOptionPane.showMessageDialog(guiFrame, "Invalid file name");
                        }


                    } else if (saveFile.showSaveDialog(guiFrame) == JFileChooser.CANCEL_OPTION) {
                        JOptionPane.showMessageDialog(guiFrame, "Cancelled");
                    } else if (saveFile.showSaveDialog(guiFrame) == JFileChooser.ERROR_OPTION) {
                        JOptionPane.showMessageDialog(guiFrame, "An error occured.");
                    } else {
                        JOptionPane.showMessageDialog(guiFrame, "Unknown operation");

                    }


                }
            });
        }

        if (calculateAllComitmentsButton.getActionListeners().length==0) {
            calculateAllComitmentsButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    operator.calculateAllCommitments(locus);

                }
            });
        }

    }


}