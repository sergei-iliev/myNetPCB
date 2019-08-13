package com.mynetpcb.core.capi.gui.searchcombo;

import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;
import javax.swing.plaf.metal.MetalComboBoxUI;


class SearchableComboBoxUI extends MetalComboBoxUI {


    protected ComboPopup createPopup()  
         {  
           BasicComboPopup popup = (BasicComboPopup)super.createPopup();  
           popup.setPreferredSize(new Dimension(0,0));  
           return popup;  
         }

    protected JButton createArrowButton() {
                return new JButton() {
                @Override
                public int getWidth() {
                        return 0;
                }
        };
    }
}
