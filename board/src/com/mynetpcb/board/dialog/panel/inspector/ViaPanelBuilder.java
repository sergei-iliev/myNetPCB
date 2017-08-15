package com.mynetpcb.board.dialog.panel.inspector;

import com.mynetpcb.board.component.BoardComponent;
import com.mynetpcb.board.shape.PCBVia;
import com.mynetpcb.core.capi.Grid;
import com.mynetpcb.core.capi.panel.AbstractPanelBuilder;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.undo.MementoType;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class ViaPanelBuilder extends AbstractPanelBuilder<Shape>{
    
    public ViaPanelBuilder(BoardComponent component) {
        super(component, new GridLayout(6, 1));
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
        //drill  size      
                panel=new JPanel(); panel.setLayout(new BorderLayout());
                label=new JLabel("Drill size"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(90,24)); panel.add(label,BorderLayout.WEST);
                thicknessField=new JTextField("0"); thicknessField.addKeyListener(this); panel.add(thicknessField,BorderLayout.CENTER);
                layoutPanel.add(panel);      
        //via  size      
                panel=new JPanel(); panel.setLayout(new BorderLayout());
                label=new JLabel("Via size"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(90,24)); panel.add(label,BorderLayout.WEST);
                widthField=new JTextField("0"); widthField.addKeyListener(this); panel.add(widthField,BorderLayout.CENTER);
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
        PCBVia via=(PCBVia)getTarget(); 
        leftField.setText(toUnitX(via.getX()));
        topField.setText(toUnitY(via.getY()));
        netField.setText(via.getNetName());
        thicknessField.setText(toUnit(via.getThickness()));
        widthField.setText(toUnit(via.getWidth()));
        clearanceField.setText(String.valueOf(Grid.COORD_TO_MM(via.getClearance())));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
       
    }
    
    @Override
    public void keyReleased(KeyEvent e){
        if(e.getKeyCode()!=KeyEvent.VK_ENTER) return;
        PCBVia via=(PCBVia)getTarget();
        if(e.getSource()==this.leftField){
           via.setX(fromUnit(this.leftField.getText())); 
        }
        
        if(e.getSource()==this.topField){
            via.setY(fromUnit(this.topField.getText())); 
        }
        if(e.getSource()==this.thicknessField){
           via.setThickness(fromUnit(this.thicknessField.getText()));
        }
        if(e.getSource()==this.widthField){
           via.setWidth(fromUnit(this.widthField.getText()));
        }
        if(e.getSource()==this.netField){
           via.setNetName(this.netField.getText());
        }
        if(e.getSource()==this.clearanceField){
           via.setClearance(Grid.MM_TO_COORD(Double.parseDouble(clearanceField.getText())));
        }
        
        getComponent().getModel().getUnit().registerMemento(getTarget().getState(MementoType.MOVE_MEMENTO));
        getComponent().Repaint(); 
    }
}
