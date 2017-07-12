package com.mynetpcb.circuit.dialog.panel.inspector;

import com.mynetpcb.circuit.component.CircuitComponent;
import com.mynetpcb.circuit.shape.SCHNetLabel;
import com.mynetpcb.core.capi.panel.AbstractPanelBuilder;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.text.Text;
import com.mynetpcb.core.capi.undo.MementoType;

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

public class NetLabelPanelBuilder extends AbstractPanelBuilder<Shape>{
    
    private JTextField netLabelField;
    
    public NetLabelPanelBuilder(CircuitComponent component) {
       super(component,new GridLayout(2,1));  
        //***BusPin Name        
       panel=new JPanel(); panel.setLayout(new BorderLayout()); 
       label=new JLabel("Net Label Name"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(100,label.getHeight())); panel.add(label,BorderLayout.WEST);
       netLabelField=new JTextField("???"); netLabelField.addKeyListener(this); panel.add(netLabelField,BorderLayout.CENTER);
       layoutPanel.add(panel);
        //****BusPin text orientation        
       panel=new JPanel(); panel.setLayout(new BorderLayout());
       label=new JLabel("Text Orientation"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(100,label.getHeight())); panel.add(label,BorderLayout.WEST);
       textOrientationCombo=new JComboBox(Text.Orientation.values());textOrientationCombo.addActionListener(this);  panel.add(textOrientationCombo,BorderLayout.CENTER);
       layoutPanel.add(panel);                    
    }
    
    public void actionPerformed(ActionEvent e) {
        SCHNetLabel net=(SCHNetLabel)getTarget();
        
        if(e.getSource()==textOrientationCombo){               
                AffineTransform rotation=AffineTransform.getRotateInstance(Math.PI/2,net.getX(),net.getY());
                net.Rotate(rotation);                 
        } 
        
        getComponent().getModel().getUnit().registerMemento(getTarget().getState(MementoType.MOVE_MEMENTO));
        getComponent().Repaint();
    }
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode()!=KeyEvent.VK_ENTER) return;
        
        SCHNetLabel net=(SCHNetLabel)getTarget();     
        if(e.getSource()==netLabelField){
            if(netLabelField.getText().length()==0){                
                netLabelField.setText(net.getText());
            }else{
              net.setText(netLabelField.getText());          
            }
           //***notify jTree of the rename           
        } 
        getComponent().getModel().getUnit().registerMemento(getTarget().getState(MementoType.MOVE_MEMENTO));
        getComponent().Repaint();
        
    }
    
    @Override
    public void updateUI() {
        SCHNetLabel net=(SCHNetLabel)getTarget();
        netLabelField.setText(net.getText());                                                             
        
        setSelectedIndex(textOrientationCombo,(net.getOrientation().ordinal()));
        
    }
}
