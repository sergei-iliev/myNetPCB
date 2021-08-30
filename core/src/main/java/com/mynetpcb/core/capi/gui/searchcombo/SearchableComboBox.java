package com.mynetpcb.core.capi.gui.searchcombo;


import com.mynetpcb.core.utils.Utilities;

import java.awt.BorderLayout;

import java.awt.Component;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;


import javax.swing.ComboBoxEditor;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.plaf.metal.MetalComboBoxUI;

public class SearchableComboBox extends JComboBox{
   
    private boolean searchable;     
    
    private final MouseListener parentListener;
    
    public SearchableComboBox(MouseListener parentListener) {
      this.setEditable(true);  
      this.parentListener=parentListener;
      this.setEditor(new SearchableEditor());  
      this.setSearchable(false);
    }
    
    public void setSearchable(boolean searchable){
        this.searchable=searchable;
        ((SearchableEditor)this.getEditor()).setSearchable(searchable);
        if(searchable){
            this.setUI(new SearchableComboBoxUI());
        }else{ 
            this.setUI(new MetalComboBoxUI());    
        }
    }
    
    public boolean isSearchable(){
       return searchable; 
    }


    private class SearchableEditor implements ComboBoxEditor{
        private JPanel panel;
        
        private JLabel imageIconLabel;

        private JTextField textField;
        
        public SearchableEditor() {
          panel=new JPanel(new BorderLayout());  
          imageIconLabel = new JLabel();
          imageIconLabel.addMouseListener(parentListener);
          textField = new JTextField();
          panel.add(imageIconLabel, BorderLayout.WEST);
          panel.add(textField, BorderLayout.CENTER);
        }

        public Component getEditorComponent() {
            return panel;
        }

        public void setItem(Object anObject) {
            if (anObject != null&&!SearchableComboBox.this.searchable) {
              textField.setText(anObject.toString());
            }        
        }
        
        public void setSearchable(boolean searchable){
            if(searchable){
                textField.setEditable(true);
                textField.setText("");
                //imageIconLabel.setIcon(Utility.loadImageIcon(this,"../../../dialogs/images/selectcb_search.png"));  
                imageIconLabel.setIcon(Utilities.loadImageIcon(this,"/com/mynetpcb/core/images/selectcb_search.png")); 
            }else{                
                textField.setEditable(false);
                imageIconLabel.setIcon(Utilities.loadImageIcon(this,"/com/mynetpcb/core/images/selectcb.png"));                  
            }
        }
        
        public Object getItem() {
            return textField.getText();
        }

        public void selectAll() {
            textField.selectAll();
        }

        public void addActionListener(ActionListener l) {
            textField.addActionListener(l);
        }
        
        public void removeActionListener(ActionListener l) {
            textField.removeActionListener(l);
        }
    }
   /* 
    public static void main(String s[]) {
      JFrame frame = new JFrame("Combo Box Example");
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      JPanel panel=new JPanel(new BorderLayout());
      JComboBox scb=new SearchableComboBox();
      scb.addItem("Hello man");
      scb.addItem("Hello XMAN");
      panel.add(scb, BorderLayout.CENTER);
      frame.setPreferredSize(new Dimension(200,50));
      frame.setContentPane(panel);
      frame.pack();
      frame.setVisible(true);
    }
*/
}



