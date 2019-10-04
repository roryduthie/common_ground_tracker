package kn.valida.gui;

import kn.valida.discourseModel.Speaker;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.HashSet;
import java.util.Set;


public class SpeakerRenderer extends DefaultListCellRenderer {

        private Border padBorder = new EmptyBorder(3,3,3,3);
        private Set<String> highlightElements = new HashSet<>();





        public SpeakerRenderer()
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
            String f = ((Speaker) value).getName();
            l.setText(f);
            l.setIcon(null);
            l.setBorder(padBorder);

            if (isSelected)
            {
             //   setBorder(BorderFactory.createLineBorder(Color.blue));
              //  setBackground(Color.blue);
            }


            if (highlightElements.contains (((Speaker) value).getSid()))
            {
                setBackground(new Color(0,0,255,20));
            } else
            {
               // setBackground(Color.white);
            }

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



    public Set<String> getHighlightElements() {
        return highlightElements;
    }

    public void setHighlightElements(Set<String> highlightElements) {
        this.highlightElements = highlightElements;
    }



    }


