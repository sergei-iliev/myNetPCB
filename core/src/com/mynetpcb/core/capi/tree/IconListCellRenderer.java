package com.mynetpcb.core.capi.tree;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JList;


public class IconListCellRenderer extends DefaultListCellRenderer {

private Icon icon;    
    
    public IconListCellRenderer(Icon icon) {  
      this.icon=icon;
    }
    
    @Override     
      public Component getListCellRendererComponent( JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {     
          JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);      
          if(index==-1){
            label.setIcon(icon);
          }  
          return label;            
      } 
        
}


