package com.mynetpcb.symbol.dialog.panel.inspector;


import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.undo.MementoType;
import com.mynetpcb.symbol.component.SymbolComponent;
import com.mynetpcb.symbol.shape.Arc;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;


public class ArcPanelBuilder extends EllipsePanelBuilder{
  
  private JComboBox arcCombo;

  private JTextField startAngField,extAngField; 
        
    public ArcPanelBuilder(SymbolComponent component) {
         super(component,9);                
        //****Arc
                panel=new JPanel(); panel.setLayout(new BorderLayout());         
                label=new JLabel("Start Angle"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(114,label.getHeight())); panel.add(label,BorderLayout.WEST);
                startAngField=new JTextField("");  startAngField.addKeyListener(this); panel.add(startAngField,BorderLayout.CENTER);
                layoutPanel.add(panel);
                
                panel=new JPanel(); panel.setLayout(new BorderLayout());         
                label=new JLabel("Extend Angle"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(114,label.getHeight())); panel.add(label,BorderLayout.WEST);
                extAngField=new JTextField("");  extAngField.addKeyListener(this); panel.add(extAngField,BorderLayout.CENTER);
                layoutPanel.add(panel);                
        //***Arc Type        
                panel=new JPanel(); panel.setLayout(new BorderLayout()); 
                label=new JLabel("Type"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(114,label.getHeight())); panel.add(label,BorderLayout.WEST);
                arcCombo=new JComboBox(arcType); arcCombo.addActionListener(this); panel.add(arcCombo,BorderLayout.CENTER);
                layoutPanel.add(panel); 
                
    }

    @Override
    public void updateUI() {
        super.updateUI();  
        Arc arc=(Arc)getTarget();
        
        extAngField.setText(String.valueOf(arc.getExtendAngle()));
        startAngField.setText(String.valueOf(arc.getStartAngle()));
        
        this.arcCombo.removeActionListener(this);        
        arcCombo.setSelectedIndex(arc.getArcType());
        this.arcCombo.addActionListener(this); 
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource()==arcCombo){
          ((Arc) getTarget()).setArcType(arcCombo.getSelectedIndex());  
        }   
        if(e.getSource()==fillCombo){
          getTarget().setFill((Shape.Fill)fillCombo.getSelectedItem());  
        } 
        getComponent().getModel().getUnit().registerMemento( getTarget().getState(MementoType.MOVE_MEMENTO));
        getComponent().Repaint();         
    }
    
    @Override
    public void keyReleased(KeyEvent e){
        if(e.getKeyCode() !=KeyEvent.VK_ENTER) return;
        Arc arc=(Arc)getTarget();
      
        if(e.getSource()==startAngField){
            arc.setStartAngle((Integer.parseInt(startAngField.getText())));    
        }
        if(e.getSource()==extAngField){
            arc.setExtendAngle((Integer.parseInt(extAngField.getText())));    
        }
        getComponent().getModel().getUnit().registerMemento(getTarget().getState(MementoType.MOVE_MEMENTO));
        super.keyReleased(e);          
    }
}
