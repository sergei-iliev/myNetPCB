package com.mynetpcb.pad.dialog.panel.inspector;

import com.mynetpcb.core.capi.Grid;
import com.mynetpcb.core.capi.component.UnitComponent;
import com.mynetpcb.core.capi.layer.Layer;
import com.mynetpcb.core.capi.panel.AbstractPanelBuilder;

import com.mynetpcb.core.capi.shape.Shape;

import com.mynetpcb.core.capi.undo.MementoType;
import com.mynetpcb.pad.shape.Circle;

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

public class CirclePanelBuilder extends AbstractPanelBuilder<Shape> {
    
    public CirclePanelBuilder(UnitComponent component) {
        this(component,7);
    }
    public CirclePanelBuilder(UnitComponent component,int rows) {
        super(component,new GridLayout(rows,1));
        //***layer        
                panel=new JPanel(); panel.setLayout(new BorderLayout()); 
                label=new JLabel("Layer"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(114,label.getHeight())); panel.add(label,BorderLayout.WEST);
                layerCombo=new JComboBox(Layer.PCB_SYMBOL_OUTLINE_LAYERS);layerCombo.addActionListener(this);  panel.add(layerCombo,BorderLayout.CENTER);                
                layoutPanel.add(panel);        
        //***Left        
                panel=new JPanel(); panel.setLayout(new BorderLayout());
                label=new JLabel("Center X"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(114,label.getHeight())); panel.add(label,BorderLayout.WEST);
                leftField=new JTextField("0");leftField.addKeyListener(this);  panel.add(leftField,BorderLayout.CENTER);
                layoutPanel.add(panel);
        //***Top        
                panel=new JPanel(); panel.setLayout(new BorderLayout());
                label=new JLabel("Center Y"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(114,label.getHeight())); panel.add(label,BorderLayout.WEST);
                topField=new JTextField("0"); topField.addKeyListener(this); panel.add(topField,BorderLayout.CENTER);
                layoutPanel.add(panel);
               
        //***Thickness        
                panel=new JPanel(); panel.setLayout(new BorderLayout()); 
                label=new JLabel("Thickness"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(114,label.getHeight())); panel.add(label,BorderLayout.WEST);
                thicknessField=new JTextField("0"); thicknessField.addKeyListener(this); panel.add(thicknessField,BorderLayout.CENTER);
                layoutPanel.add(panel); 
        //***Fill
                panel=new JPanel(); panel.setLayout(new BorderLayout()); 
                label=new JLabel("Fill"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(114,label.getHeight())); panel.add(label,BorderLayout.WEST);
                fillCombo=new JComboBox(fillValues);fillCombo.addActionListener(this);  panel.add(fillCombo,BorderLayout.CENTER);
                layoutPanel.add(panel);        
        //****Width
                panel=new JPanel(); panel.setLayout(new BorderLayout()); 
                label=new JLabel("Radius"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(114,label.getHeight())); panel.add(label,BorderLayout.WEST);
                widthField=new JTextField("");  widthField.addKeyListener(this);panel.add(widthField,BorderLayout.CENTER);
                layoutPanel.add(panel);   
    }

    @Override
    public void updateUI() {
        Circle circle=(Circle)getTarget();  
        
        leftField.setText(toUnitX(circle.getCenter().x,5));
        topField.setText(toUnitY(circle.getCenter().y,5)); 
        
        thicknessField.setText(String.valueOf(Grid.COORD_TO_MM(circle.getThickness())));    
        widthField.setText(String.valueOf(Grid.COORD_TO_MM(circle.getRadius())));
        
        setSelectedIndex(fillCombo,(circle.getFill()==Shape.Fill.EMPTY?0:1)); 
        
        setSelectedItem(layerCombo, (getTarget()).getCopper());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        super.actionPerformed(e);
    	if(e.getSource()==fillCombo){
           getTarget().setFill(Shape.Fill.values()[fillCombo.getSelectedIndex()]);                    
        }
    	getComponent().getModel().getUnit().registerMemento( getTarget().getState(MementoType.MOVE_MEMENTO));       
        this.getComponent().Repaint();
    }
    
    @Override
    public void keyReleased(KeyEvent e){
        if(e.getKeyCode() !=KeyEvent.VK_ENTER) return;

        if(e.getSource()==this.thicknessField){
            getTarget().setThickness((int)Grid.MM_TO_COORD(Double.parseDouble(thicknessField.getText())));  
        }
        
        if(e.getSource()==this.leftField){
           getTarget().getCenter().x=(Grid.MM_TO_COORD(Double.parseDouble(leftField.getText())));           
        }
        
        if(e.getSource()==this.topField){            
          getTarget().getCenter().y=(Grid.MM_TO_COORD(Double.parseDouble(topField.getText())));             
        }
        
        if(e.getSource()==this.widthField){
           ((Circle)getTarget()).setRadius(Grid.MM_TO_COORD(Double.parseDouble(widthField.getText()))); 
        }
        
        getComponent().getModel().getUnit().registerMemento( getTarget().getState(MementoType.MOVE_MEMENTO));
        getComponent().Repaint();          
    }
}
