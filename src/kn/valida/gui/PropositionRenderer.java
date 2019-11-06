package kn.valida.gui;

import kn.valida.discourseModel.DiscourseProposition;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.HashSet;
import java.util.Set;

public class PropositionRenderer extends DefaultListCellRenderer {

    private Border padBorder = new EmptyBorder(3,3,3,3);
    private Set<String> highlightCommitments = new HashSet<>();
    private Set<String> highlightJointCommitments = new HashSet<>();
    private Set<String> highlightUnresolved = new HashSet<>();
    private Set<String> highlightControversial = new HashSet<>();

    public PropositionRenderer()
    {

    }

    @Override
    public Component getListCellRendererComponent(
            JList list,
            Object value,
            int index,
            boolean isSelected,
            boolean cellHasFocus) {

        /*
        Component c = super.getListCellRendererComponent(
                list,value,index,isSelected,cellHasFocus);



        JLabel l = (JLabel)c;
        */


        JPanel j = new JPanel();
        j.setLayout(new FlowLayout(FlowLayout.LEFT));

        j.setBackground(Color.white);

        JLabel l = new JLabel();

        //Changed to toString() instead of casting so that different objects can be rendered by this
        String f =  ((DiscourseProposition) value).getOriginalSpeaker().getName() + ": " +  ((DiscourseProposition) value).getText();
        //  "{" + ((DiscourseProposition) value).writeExpressiveContent() + "}";
        l.setText(f);
        l.setIcon(null);
        l.setBorder(padBorder);
        l.setOpaque(true);
        l.setBackground(Color.white);

        JLabel m = new JLabel();
        m.setText(((DiscourseProposition)value).writeExpressiveContent());
        m.setIcon(null);
        m.setBorder(padBorder);
        m.setOpaque(true);
        m.setBackground(Color.white);


        j.add(l);
        j.add(m);


        if (isSelected)
        {
            j.setBorder(BorderFactory.createLineBorder(Color.blue));
        }

        if (!((DiscourseProposition)value).getExpressiveContent().isEmpty())
        {
            Boolean highlight = false;
            for (DiscourseProposition p : ((DiscourseProposition) value).getExpressiveContent())
            {
                if (highlightJointCommitments.contains(p.getPid()))
                {
                    highlight = true;
                }
            }

            if (highlight)
            {
                m.setBackground(new Color(0,255,0,20));
            }


        }

        if (highlightCommitments.contains(((DiscourseProposition) value).getPid()))
        {
            l.setBackground(new Color(0,0,255,20));
        }

        if (highlightJointCommitments.contains(((DiscourseProposition) value).getPid()))
        {
            l.setBackground(new Color(0,255,0,20));
        }

        if (highlightUnresolved.contains(((DiscourseProposition) value).getPid()))
        {
            l.setBackground(new Color(255,128,0,20));
        }

        if (highlightControversial.contains(((DiscourseProposition) value).getPid()))
        {
            l.setBackground(new Color(255,0,0,20));
        }

        /*
        if (highlightElements.contains (((Speaker) value).getSid()))
        {
            setBackground(new Color(0,0,255,20));
        }
        */

            /*
            if (searchResult.contains(index))
            {
                setBackground(new Color(0,0,255,20));

            }

            if (possibleEvents.contains(index))
            {
                setBackground(new Color(255,0,0,20));

            }

            if (ruleSource.contains(index))
            {
                setBackground(new Color(0,255,0,20));
            }
            */
        return j;
    }


    public void resetLists(){
        highlightJointCommitments = new HashSet<>();
        highlightCommitments = new HashSet<>();
        highlightUnresolved = new HashSet<>();
        highlightControversial = new HashSet<>();
    }



    public Set<String> getHighlightCommitments() {
        return highlightCommitments;
    }

    public void setHighlightCommitments(Set<String> highlightCommitments) {
        this.highlightCommitments = highlightCommitments;
    }

    public Set<String> getHighlightJointCommitments() {
        return highlightJointCommitments;
    }

    public void setHighlightJointCommitments(Set<String> highlightJointCommitments) {
        this.highlightJointCommitments = highlightJointCommitments;
    }

    public Set<String> getHighlightUnresolved() {
        return highlightUnresolved;
    }

    public void setHighlightUnresolved(Set<String> highlightUnresolved) {
        this.highlightUnresolved = highlightUnresolved;
    }


    public Set<String> getHighlightControversial() {
        return highlightControversial;
    }

    public void setHighlightControversial(Set<String> highlightControversial) {
        this.highlightControversial = highlightControversial;
    }


}



