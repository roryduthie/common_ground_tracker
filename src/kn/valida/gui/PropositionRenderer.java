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
    private Set<String> highlightUnreolved = new HashSet<>();
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

        Component c = super.getListCellRendererComponent(
                list,value,index,isSelected,cellHasFocus);
        JLabel l = (JLabel)c;
        //Changed to toString() instead of casting so that different objects can be rendered by this
        String f =  ((DiscourseProposition) value).getOriginalSpeaker().getName() + ": " +  ((DiscourseProposition) value).getText();
        l.setText(f);
        l.setIcon(null);
        l.setBorder(padBorder);

        if (isSelected)
        {
            setBorder(BorderFactory.createLineBorder(Color.blue));
        }

        if (highlightCommitments.contains(((DiscourseProposition) value).getPid()))
        {
            setBackground(new Color(0,0,255,20));
        }

        if (highlightJointCommitments.contains(((DiscourseProposition) value).getPid()))
        {
            setBackground(new Color(0,255,0,20));
        }

        if (highlightUnreolved.contains(((DiscourseProposition) value).getPid()))
        {
            setBackground(new Color(255,128,0,20));
        }

        if (highlightControversial.contains(((DiscourseProposition) value).getPid()))
        {
            setBackground(new Color(255,0,0,20));
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
        return l;
    }


    public void resetLists(){
        highlightJointCommitments = new HashSet<>();
        highlightCommitments = new HashSet<>();
        highlightUnreolved = new HashSet<>();
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

    public Set<String> getHighlightUnreolved() {
        return highlightUnreolved;
    }

    public void setHighlightUnreolved(Set<String> highlightUnreolved) {
        this.highlightUnreolved = highlightUnreolved;
    }


    public Set<String> getHighlightControversial() {
        return highlightControversial;
    }

    public void setHighlightControversial(Set<String> highlightControversial) {
        this.highlightControversial = highlightControversial;
    }


}



