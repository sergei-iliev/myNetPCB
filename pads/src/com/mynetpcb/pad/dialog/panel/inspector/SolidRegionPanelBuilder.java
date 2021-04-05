package com.mynetpcb.pad.dialog.panel.inspector;

import com.mynetpcb.core.capi.component.UnitComponent;
import com.mynetpcb.core.capi.layer.Layer;
import com.mynetpcb.core.capi.panel.AbstractPanelBuilder;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.undo.MementoType;
import com.mynetpcb.d2.shapes.Point;
import com.mynetpcb.pad.shape.SolidRegion;

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

public class SolidRegionPanelBuilder extends AbstractPanelBuilder<Shape>{
    public SolidRegionPanelBuilder(UnitComponent component) {
         super(component,new GridLayout(3,1));
        //***layer        
         panel=new JPanel(); panel.setLayout(new BorderLayout()); 
         label=new JLabel("Layer"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(114,label.getHeight())); panel.add(label,BorderLayout.WEST);
         layerCombo=new JComboBox(Layer.PCB_SYMBOL_LAYERS);layerCombo.addActionListener(this);  panel.add(layerCombo,BorderLayout.CENTER);                
         layoutPanel.add(panel);  
        //***Left        
                panel=new JPanel(); panel.setLayout(new BorderLayout());
                label=new JLabel("X"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(114,24)); panel.add(label,BorderLayout.WEST);
                leftField=new JTextField("0");leftField.addKeyListener(this);  panel.add(leftField,BorderLayout.CENTER);
                layoutPanel.add(panel);
        
        //***Top        
                panel=new JPanel(); panel.setLayout(new BorderLayout());
                label=new JLabel("Y"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(114,24)); panel.add(label,BorderLayout.WEST);
                topField=new JTextField("0"); topField.addKeyListener(this); panel.add(topField,BorderLayout.CENTER);
                layoutPanel.add(panel);
    }


    @Override
    public void updateUI() {
        SolidRegion region=(SolidRegion)getTarget();
        Point p=region.getResizingPoint();
        leftField.setEnabled(p==null?false:true);  
        topField.setEnabled(p==null?false:true);
        leftField.setText(toUnitX(p==null?0:p.x));
        topField.setText(toUnitY(p==null?0:p.y));
        setSelectedItem(layerCombo, getTarget().getCopper());
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource()==layerCombo){
            getTarget().setCopper((Layer.Copper) layerCombo.getSelectedItem());
            getComponent().Repaint();
        }
    } 
    @Override
    public void keyReleased(KeyEvent e){
        if(e.getKeyCode()!=KeyEvent.VK_ENTER) return;
        SolidRegion area=(SolidRegion)getTarget();

        if(e.getSource()==this.leftField){
           Point p=area.getResizingPoint(); 
           p.x=fromUnitX(leftField.getText()); 
        }
        
        if(e.getSource()==this.topField){
           Point p=area.getResizingPoint();
           p.y= fromUnitY(topField.getText());  
        }
        getComponent().getModel().getUnit().registerMemento(getTarget().getState(MementoType.MOVE_MEMENTO));
        getComponent().Repaint(); 
    }    
}
