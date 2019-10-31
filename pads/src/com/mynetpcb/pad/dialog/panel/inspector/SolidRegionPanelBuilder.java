package com.mynetpcb.pad.dialog.panel.inspector;

import com.mynetpcb.core.capi.component.UnitComponent;
import com.mynetpcb.core.capi.layer.Layer;
import com.mynetpcb.core.capi.panel.AbstractPanelBuilder;
import com.mynetpcb.core.capi.shape.Shape;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;

import java.awt.event.ActionEvent;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class SolidRegionPanelBuilder extends AbstractPanelBuilder<Shape>{
    public SolidRegionPanelBuilder(UnitComponent component) {
         super(component,new GridLayout(1,1));
        //***layer        
         panel=new JPanel(); panel.setLayout(new BorderLayout()); 
         label=new JLabel("Layer"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(114,label.getHeight())); panel.add(label,BorderLayout.WEST);
         layerCombo=new JComboBox(Layer.PCB_SYMBOL_LAYERS);layerCombo.addActionListener(this);  panel.add(layerCombo,BorderLayout.CENTER);                
         layoutPanel.add(panel);  
    }


    @Override
    public void updateUI() {
        setSelectedItem(layerCombo, getTarget().getCopper());
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource()==layerCombo){
            getTarget().setCopper((Layer.Copper) layerCombo.getSelectedItem());
            getComponent().Repaint();
        }
    }    
}
