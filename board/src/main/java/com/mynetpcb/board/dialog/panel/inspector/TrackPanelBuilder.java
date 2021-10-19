package com.mynetpcb.board.dialog.panel.inspector;

import com.mynetpcb.board.component.BoardComponent;
import com.mynetpcb.board.shape.PCBTrack;
import com.mynetpcb.core.capi.Grid;
import com.mynetpcb.core.capi.layer.Layer;
import com.mynetpcb.core.capi.panel.AbstractPanelBuilder;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.undo.MementoType;
import com.mynetpcb.d2.shapes.Point;

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

public class TrackPanelBuilder extends AbstractPanelBuilder<Shape>{
        
    public TrackPanelBuilder(BoardComponent component) {
       super(component,new GridLayout(6,1));
        //layer
                panel=new JPanel(); panel.setLayout(new BorderLayout()); 
                label=new JLabel("Layer"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(90,label.getHeight())); panel.add(label,BorderLayout.WEST);
                layerCombo=new JComboBox(new Layer.Copper[]{Layer.Copper.FCu,Layer.Copper.BCu});layerCombo.addActionListener(this);  panel.add(layerCombo,BorderLayout.CENTER);                
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
        //***Clearance       
                panel=new JPanel(); panel.setLayout(new BorderLayout());
                label=new JLabel("Clearance"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(90,24)); panel.add(label,BorderLayout.WEST);
                clearanceField=new JTextField(); clearanceField.addKeyListener(this); panel.add(clearanceField,BorderLayout.CENTER);
                layoutPanel.add(panel);                      
        //***Net
                panel=new JPanel(); panel.setLayout(new BorderLayout());
                label=new JLabel("Net"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(90,24)); panel.add(label,BorderLayout.WEST);
                netField=new JTextField(""); netField.addKeyListener(this); panel.add(netField,BorderLayout.CENTER);
                layoutPanel.add(panel);                
    }

    @Override
    public void updateUI() {
        PCBTrack line=(PCBTrack)getTarget();
        Point p=line.getResizingPoint();
        leftField.setEnabled(p==null?false:true);  
        topField.setEnabled(p==null?false:true);
        leftField.setText(toUnitX(p==null?0:p.x,5));
        topField.setText(toUnitY(p==null?0:p.y,5));
        netField.setText(line.getNetName());
        setSelectedItem(layerCombo, line.getCopper());
        
        thicknessField.setText(String.valueOf(Grid.COORD_TO_MM(line.getThickness())));
        clearanceField.setText(String.valueOf(Grid.COORD_TO_MM(line.getClearance())));

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        super.actionPerformed(e);
        getComponent().Repaint(); 
    }
    
    @Override
    public void keyReleased(KeyEvent e){
        if(e.getKeyCode()!=KeyEvent.VK_ENTER) return;
        PCBTrack line=(PCBTrack)getTarget();
        if(e.getSource()==this.leftField){
           Point p=line.getResizingPoint(); 
           p.x=fromUnitX(leftField.getText()); 
        }
        
        if(e.getSource()==this.topField){
           Point p=line.getResizingPoint();
           p.y= fromUnitY(topField.getText());  
        }
        if(e.getSource()==this.thicknessField){
           line.setThickness((int)Grid.MM_TO_COORD(Double.parseDouble(thicknessField.getText())));
        }
        if(e.getSource()==this.clearanceField){
           line.setClearance((int)Grid.MM_TO_COORD(Double.parseDouble(clearanceField.getText())));
        }
        if(e.getSource()==this.netField){
           line.setNetName(this.netField.getText());
        }
        getComponent().getModel().getUnit().registerMemento(getTarget().getState(MementoType.MOVE_MEMENTO));
        getComponent().Repaint(); 
    }

}