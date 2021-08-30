package com.mynetpcb.circuit.dialog.panel.inspector;

import com.mynetpcb.circuit.component.CircuitComponent;
import com.mynetpcb.circuit.shape.SCHBusPin;
import com.mynetpcb.core.capi.panel.AbstractPanelBuilder;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.text.Textable;
import com.mynetpcb.core.capi.text.Texture;
import com.mynetpcb.core.capi.text.font.SymbolFontTexture;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class BusPinPanelBuilder extends AbstractPanelBuilder<Shape> {   
    
    private JTextField busPinField;
    private JComboBox  alignmentCombo;
        
    public BusPinPanelBuilder(CircuitComponent component) {
       super(component,new GridLayout(2,1));  
        //***BusPin Name        
       panel=new JPanel(); panel.setLayout(new BorderLayout()); 
       label=new JLabel("Bus Pin Name"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(100,label.getHeight())); panel.add(label,BorderLayout.WEST);
       busPinField=new JTextField("???"); busPinField.addKeyListener(this); panel.add(busPinField,BorderLayout.CENTER);
       layoutPanel.add(panel);
        //****BusPin text alignment        
        panel=new JPanel(); panel.setLayout(new BorderLayout());
        label=new JLabel("Text Alignment"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(100,label.getHeight())); panel.add(label,BorderLayout.WEST);
        alignmentCombo=new JComboBox(Texture.Alignment.values());alignmentCombo.addActionListener(this);  panel.add(alignmentCombo,BorderLayout.CENTER);
        layoutPanel.add(panel);
        
    }

    public void actionPerformed(ActionEvent e) {
        SCHBusPin buspin=(SCHBusPin)getTarget();   
        if(e.getSource()==alignmentCombo){
            ((SymbolFontTexture)buspin.getTextureByTag("name")).setAlignment((Texture.Alignment)alignmentCombo.getSelectedItem());                
        } 

        getComponent().Repaint();        
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode()!=KeyEvent.VK_ENTER) return;
        
        SCHBusPin buspin=(SCHBusPin)getTarget();     
        if(e.getSource()==busPinField){
            Texture text=((Textable) getTarget()).getTextureByTag("name");
            if(text.getText()==null||text.getText().length()==0){
               text.move(buspin.getLinePoints().get(1).x,buspin.getLinePoints().get(1).y);
               text.setText(busPinField.getText());
            }else{
              text.setText(busPinField.getText());          
            }
           //***notify jTree of the rename           
        } 
        getComponent().Repaint();
        
    }

    public void updateUI() {
        SCHBusPin buspin=(SCHBusPin)getTarget();  
        busPinField.setText(((Textable)getTarget()).getTextureByTag("name").getText());                                                               
        setSelectedItem(alignmentCombo,((SymbolFontTexture)buspin.getTextureByTag("name")).getAlignment()); 
        
              
    }
}
