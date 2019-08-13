package com.mynetpcb.board.dialog.panel.inspector;

import com.mynetpcb.board.component.BoardComponent;
import com.mynetpcb.board.shape.PCBCopperArea;
import com.mynetpcb.core.capi.Grid;
import com.mynetpcb.core.capi.panel.AbstractPanelBuilder;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.undo.MementoType;
import com.mynetpcb.core.pad.Layer;

import com.mynetpcb.core.pad.shape.PadShape;

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

public class CopperAreaPanelBuilder extends AbstractPanelBuilder<Shape>{
    private JComboBox padConnnectionCombo;
    
    public CopperAreaPanelBuilder(BoardComponent component) {
       super(component,new GridLayout(7,1));
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
         //fill       
                panel=new JPanel(); panel.setLayout(new BorderLayout()); 
                label=new JLabel("Fill"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(90,label.getHeight())); panel.add(label,BorderLayout.WEST);
                fillCombo=new JComboBox(fillValues);fillCombo.addActionListener(this);  panel.add(fillCombo,BorderLayout.CENTER);
                layoutPanel.add(panel);                 
        //***Clearance       
                panel=new JPanel(); panel.setLayout(new BorderLayout());
                label=new JLabel("Clearance"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(90,24)); panel.add(label,BorderLayout.WEST);
                clearanceField=new JTextField("0"); clearanceField.addKeyListener(this); panel.add(clearanceField,BorderLayout.CENTER);
                layoutPanel.add(panel); 
        //***Pad connection       
                panel=new JPanel(); panel.setLayout(new BorderLayout()); 
                label=new JLabel("Pad Connection"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(90,label.getHeight())); panel.add(label,BorderLayout.WEST);
                padConnnectionCombo=new JComboBox(PadShape.PadConnection.values());padConnnectionCombo.addActionListener(this);  panel.add(padConnnectionCombo,BorderLayout.CENTER);
                layoutPanel.add(panel);                
                
        //***Net
                panel=new JPanel(); panel.setLayout(new BorderLayout());
                label=new JLabel("Net"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(90,24)); panel.add(label,BorderLayout.WEST);
                netField=new JTextField(""); netField.addKeyListener(this); panel.add(netField,BorderLayout.CENTER);
                layoutPanel.add(panel);   
                
    }

    @Override
    public void updateUI() {
        PCBCopperArea area=(PCBCopperArea)getTarget();
        Point p=area.getResizingPoint();
        leftField.setEnabled(p==null?false:true);  
        topField.setEnabled(p==null?false:true);
        leftField.setText(toUnitX(p==null?0:p.x));
        topField.setText(toUnitY(p==null?0:p.y));
        netField.setText(area.getNetName());
        
        setSelectedItem(padConnnectionCombo, area.getPadConnection());
        setSelectedItem(layerCombo, area.getCopper());
        setSelectedIndex(fillCombo,(area.getFill()==Shape.Fill.EMPTY?0:1)); 
        clearanceField.setText(String.valueOf(Grid.COORD_TO_MM(area.getClearance())));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        super.actionPerformed(e);
        PCBCopperArea area=(PCBCopperArea)getTarget();
        if(e.getSource()==fillCombo){
           getTarget().setFill(Shape.Fill.values()[fillCombo.getSelectedIndex()]);                
        }
        if(e.getSource()==padConnnectionCombo){
           area.setPadConnection((PadShape.PadConnection)padConnnectionCombo.getSelectedItem());                
        }
        
        getComponent().getModel().getUnit().registerMemento( getTarget().getState(MementoType.MOVE_MEMENTO));         
        getComponent().Repaint();
    }
    
    @Override
    public void keyReleased(KeyEvent e){
        if(e.getKeyCode()!=KeyEvent.VK_ENTER) return;
        PCBCopperArea area=(PCBCopperArea)getTarget();
        if(e.getSource()==this.clearanceField){
           area.setClearance(Grid.MM_TO_COORD(Double.parseDouble(clearanceField.getText())));
        }
        if(e.getSource()==this.leftField){
           Point p=area.getResizingPoint(); 
           p.x=fromUnitX(leftField.getText()); 
        }
        
        if(e.getSource()==this.topField){
           Point p=area.getResizingPoint();
           p.y= fromUnitY(topField.getText());  
        }
        if(e.getSource()==this.netField){
           area.setNetName(this.netField.getText());
        }
        getComponent().getModel().getUnit().registerMemento(getTarget().getState(MementoType.MOVE_MEMENTO));
        getComponent().Repaint(); 
    }

}
