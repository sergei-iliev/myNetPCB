package com.mynetpcb.board.dialog.panel.inspector;

import com.mynetpcb.board.component.BoardComponent;
import com.mynetpcb.board.shape.PCBLine;
import com.mynetpcb.core.capi.Grid;
import com.mynetpcb.core.capi.panel.AbstractPanelBuilder;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.undo.MementoType;
import com.mynetpcb.core.pad.Layer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class LinePanelBuilder extends AbstractPanelBuilder<Shape>{
        
    public LinePanelBuilder(BoardComponent component) {
       super(component,new GridLayout(6,1));
        //layer
                panel=new JPanel(); panel.setLayout(new BorderLayout()); 
                label=new JLabel("Layer"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(90,label.getHeight())); panel.add(label,BorderLayout.WEST);
                layerCombo=new JComboBox(Layer.GRAPHICS_LAYERS);layerCombo.addActionListener(this);  panel.add(layerCombo,BorderLayout.CENTER);                
                layoutPanel.add(panel);       
        //***Left        
                panel=new JPanel(); panel.setLayout(new BorderLayout());
                label=new JLabel("X"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(90,24)); panel.add(label,BorderLayout.WEST);
                leftField=new JTextField("0");leftField.addKeyListener(this);  panel.add(leftField,BorderLayout.CENTER);
                layoutPanel.add(panel);
        
        //***Top        
                panel=new JPanel(); panel.setLayout(new BorderLayout());
                label=new JLabel("Y"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(90,24)); panel.add(label,BorderLayout.WEST);
                topField=new JTextField("0"); topField.addKeyListener(this); panel.add(topField,BorderLayout.CENTER);
                layoutPanel.add(panel);
        //***Thickness       
                panel=new JPanel(); panel.setLayout(new BorderLayout());
                label=new JLabel("Thickness"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(90,24)); panel.add(label,BorderLayout.WEST);
                thicknessField=new JTextField("0"); thicknessField.addKeyListener(this); panel.add(thicknessField,BorderLayout.CENTER);
                layoutPanel.add(panel);          
                
    }

    @Override
    public void updateUI() {
        PCBLine line=(PCBLine)getTarget();
        Point p=line.getResizingPoint();
        leftField.setEnabled(p==null?false:true);  
        topField.setEnabled(p==null?false:true);
        leftField.setText(toUnitX(p==null?0:p.x));
        topField.setText(toUnitY(p==null?0:p.y));
        
        setSelectedItem(layerCombo, line.getCopper());
        
        thicknessField.setText(String.valueOf(Grid.COORD_TO_MM(line.getThickness())));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        PCBLine line=(PCBLine)getTarget();
        if(e.getSource()==layerCombo){
            line.setCopper((Layer.Copper)layerCombo.getSelectedItem());
        }
        getComponent().Repaint();
    }
    
    @Override
    public void keyReleased(KeyEvent e){
        if(e.getKeyCode()!=KeyEvent.VK_ENTER) return;
        PCBLine line=(PCBLine)getTarget();
        if(e.getSource()==this.leftField){
           Point p=line.getResizingPoint(); 
           p.x=fromUnitX(leftField.getText()); 
        }
        
        if(e.getSource()==this.topField){
           Point p=line.getResizingPoint();
           p.y= fromUnitY(topField.getText());  
        }
        if(e.getSource()==this.thicknessField){
           line.setThickness(Grid.MM_TO_COORD(Double.parseDouble(thicknessField.getText())));
        }
        getComponent().getModel().getUnit().registerMemento(getTarget().getState(MementoType.MOVE_MEMENTO));
        getComponent().Repaint(); 
    }

}