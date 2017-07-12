package com.mynetpcb.circuit.dialog.panel.inspector;


import com.mynetpcb.circuit.component.CircuitComponent;
import com.mynetpcb.circuit.shape.SCHBusPin;
import com.mynetpcb.core.capi.panel.AbstractPanelBuilder;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.text.Text;
import com.mynetpcb.core.capi.text.Textable;
import com.mynetpcb.core.capi.text.Texture;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;


public class BusPinPanelBuilder extends AbstractPanelBuilder<Shape> {   
    
    private JTextField busPinField;
    
    public BusPinPanelBuilder(CircuitComponent component) {
       super(component,new GridLayout(3,1));  
        //***BusPin Name        
       panel=new JPanel(); panel.setLayout(new BorderLayout()); 
       label=new JLabel("Bus Pin Name"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(100,label.getHeight())); panel.add(label,BorderLayout.WEST);
       busPinField=new JTextField("???"); busPinField.addKeyListener(this); panel.add(busPinField,BorderLayout.CENTER);
       layoutPanel.add(panel);
        //****BusPin text alignment        
        panel=new JPanel(); panel.setLayout(new BorderLayout());
        label=new JLabel("Text Alignment"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(100,label.getHeight())); panel.add(label,BorderLayout.WEST);
        textAlignmentCombo=new JComboBox();textAlignmentCombo.addActionListener(this);  panel.add(textAlignmentCombo,BorderLayout.CENTER);
        layoutPanel.add(panel);
        //****BusPin text orientation        
       panel=new JPanel(); panel.setLayout(new BorderLayout()); 
       label=new JLabel("Text Orientation"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(100,label.getHeight())); panel.add(label,BorderLayout.WEST);
       textOrientationCombo=new JComboBox(Text.Orientation.values());textOrientationCombo.addActionListener(this);  panel.add(textOrientationCombo,BorderLayout.CENTER);
       layoutPanel.add(panel);                    
    }

    public void actionPerformed(ActionEvent e) {
        if(e.getSource()==textOrientationCombo){
                Texture text=((Textable) getTarget()).getChipText().getTextureByTag("name");  
                AffineTransform rotation=AffineTransform.getRotateInstance(Math.PI/2,text.getBoundingShape().getBounds().getCenterX(),text.getBoundingShape().getBounds().getCenterY());
                text.Rotate(rotation);                          
                validateAlignmentComboText(textAlignmentCombo,text);                
        } 
        if(e.getSource()==textAlignmentCombo){
            Texture text=((Textable)getTarget()).getChipText().getTextureByTag("name");  
            text.setAlignment(Text.Alignment.valueOf((String)textAlignmentCombo.getSelectedItem()));            
        }
        getComponent().Repaint();        
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode()!=KeyEvent.VK_ENTER) return;
        
        SCHBusPin buspin=(SCHBusPin)getTarget();     
        if(e.getSource()==busPinField){
            Texture text=((Textable) getTarget()).getChipText().getTextureByTag("name");
            if(text.getText()==null||text.getText().length()==0){
               text.Move(buspin.getLinePoints().get(1).x,buspin.getLinePoints().get(1).y);
               text.setText(busPinField.getText());
            }else{
              text.setText(busPinField.getText());          
            }
           //***notify jTree of the rename           
        } 
        getComponent().Repaint();
        
    }

    public void updateUI() {
        
        busPinField.setText(((Textable)getTarget()).getChipText().getTextureByTag("name").getText());                                                             
        
        setSelectedIndex(textOrientationCombo,(((Textable)getTarget()).getChipText().getTextureByTag("name").getAlignment().getOrientation().ordinal()));
        
        validateAlignmentComboText(textAlignmentCombo,((Textable)getTarget()).getChipText().getTextureByTag("name")); 
              
    }
}
