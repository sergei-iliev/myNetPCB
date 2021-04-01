package com.mynetpcb.circuit.dialog.panel.inspector;

import com.mynetpcb.circuit.component.CircuitComponent;
import com.mynetpcb.circuit.shape.SCHNetLabel;
import com.mynetpcb.core.capi.panel.AbstractPanelBuilder;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.text.Texture;
import com.mynetpcb.core.capi.text.font.SymbolFontTexture;
import com.mynetpcb.core.capi.undo.MementoType;

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

public class NetLabelPanelBuilder extends AbstractPanelBuilder<Shape>{
    
    private JTextField netLabelField;
    
    public NetLabelPanelBuilder(CircuitComponent component) {
       super(component,new GridLayout(2,1));  
        //***BusPin Name        
       panel=new JPanel(); panel.setLayout(new BorderLayout()); 
       label=new JLabel("Name"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(100,label.getHeight())); panel.add(label,BorderLayout.WEST);
       netLabelField=new JTextField("???"); netLabelField.addKeyListener(this); panel.add(netLabelField,BorderLayout.CENTER);
       layoutPanel.add(panel);
        //****BusPin text orientation        
       panel=new JPanel(); panel.setLayout(new BorderLayout());
       label=new JLabel("Alignment"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(100,label.getHeight())); panel.add(label,BorderLayout.WEST);
       styleCombo=new JComboBox(Texture.Alignment.values());styleCombo.addActionListener(this);  panel.add(styleCombo,BorderLayout.CENTER);
       layoutPanel.add(panel);                    
    }
    
    @Override
    public void updateUI() {
        SCHNetLabel net=(SCHNetLabel)getTarget();
        netLabelField.setText(net.getTexture().getText());                                                             
        setSelectedItem(styleCombo,((SymbolFontTexture)net.getTexture()).getAlignment());    
        
        
    }
    public void actionPerformed(ActionEvent e) {
        SCHNetLabel net=(SCHNetLabel)getTarget();
        
        if(e.getSource()==styleCombo){ 
           net.setAlignment((Texture.Alignment)styleCombo.getSelectedItem());            
        }
        getComponent().getModel().getUnit().registerMemento(getTarget().getState(MementoType.MOVE_MEMENTO));
        getComponent().Repaint();
    }
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode()!=KeyEvent.VK_ENTER) return;
        
        SCHNetLabel net=(SCHNetLabel)getTarget();     
        if(e.getSource()==netLabelField){
            if(netLabelField.getText().length()==0){                
                netLabelField.setText(net.getTexture().getText());
            }else{
              net.getTexture().setText(netLabelField.getText());          
            }
           //***notify jTree of the rename           
        } 
        getComponent().getModel().getUnit().registerMemento(getTarget().getState(MementoType.MOVE_MEMENTO));
        getComponent().Repaint();
        
    }
    

}

