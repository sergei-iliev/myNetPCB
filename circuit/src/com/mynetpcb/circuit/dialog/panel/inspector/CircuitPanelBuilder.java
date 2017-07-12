package com.mynetpcb.circuit.dialog.panel.inspector;


import com.mynetpcb.circuit.component.CircuitComponent;
import com.mynetpcb.core.capi.event.UnitEvent;
import com.mynetpcb.core.capi.panel.AbstractPanelBuilder;
import com.mynetpcb.core.capi.shape.Shape;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;


public class CircuitPanelBuilder extends AbstractPanelBuilder<Shape> {
    
    private JTextField circuitNameField,widthField,heightField;
    
    public CircuitPanelBuilder(CircuitComponent component) {
        super(component,new GridLayout(3,1));        
                //***Circuit Name        
                        panel=new JPanel(); panel.setLayout(new BorderLayout()); 
                        label=new JLabel("Circuit name"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(100,24)); panel.add(label,BorderLayout.WEST);
                        circuitNameField=new JTextField(""); circuitNameField.addKeyListener(this);panel.add(circuitNameField,BorderLayout.CENTER);
                        layoutPanel.add(panel);                                                      
             //***Widht
                        panel=new JPanel(); panel.setLayout(new BorderLayout()); 
                        label=new JLabel("Width"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(100,24)); panel.add(label,BorderLayout.WEST);
                        widthField=new JTextField(""); widthField.addKeyListener(this); panel.add(widthField,BorderLayout.CENTER);                                                                                    
                        layoutPanel.add(panel);        
              //***Height
                        panel=new JPanel(); panel.setLayout(new BorderLayout()); 
                        label=new JLabel("Height"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(100,24)); panel.add(label,BorderLayout.WEST);
                        heightField=new JTextField(""); heightField.addKeyListener(this); panel.add(heightField,BorderLayout.CENTER);
                        layoutPanel.add(panel);   
        
    }


    public void actionPerformed(ActionEvent e) {
            
    }


    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode()!=KeyEvent.VK_ENTER) return;
        
        if(e.getSource()==this.heightField||e.getSource()==this.widthField){
            getComponent().getModel().getUnit().setSize(Integer.parseInt(widthField.getText()), Integer.parseInt(heightField.getText()));
            //***refresh scrollbars
            getComponent().setSize(getComponent().getWidth(),getComponent().getHeight());                
            getComponent().componentResized(null);   
            getComponent().Repaint();
        }
        
        if(e.getSource()==this.circuitNameField){
           getComponent().getModel().getUnit().setUnitName(circuitNameField.getText());
           getComponent().getModel().fireUnitEvent(new UnitEvent(getComponent().getModel().getUnit(), UnitEvent.RENAME_UNIT));
        }   
        
        getComponent().Repaint();
        
    }
    
    @Override
    public void updateUI() {
        circuitNameField.setText(getComponent().getModel().getUnit()!=null?getComponent().getModel().getUnit().getUnitName():""); 
        widthField.setText(String.valueOf(getComponent().getModel().getUnit()!=null?getComponent().getModel().getUnit().getWidth():"0"));
        heightField.setText(String.valueOf(getComponent().getModel().getUnit()!=null?getComponent().getModel().getUnit().getHeight():"0"));       
    }
}

