package com.mynetpcb.symbol.dialog.panel.inspector;

import com.mynetpcb.core.capi.panel.AbstractPanelBuilder;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.symbol.component.SymbolComponent;
import com.mynetpcb.symbol.shape.Pin;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class PinPanelBuilder  extends  AbstractPanelBuilder<Shape> { 
    JComboBox pinTypeCombo;
    
    public PinPanelBuilder(SymbolComponent component) {
        super(component,new GridLayout(1,1));
        panel=new JPanel();panel.setLayout(new BorderLayout()); 
        label=new JLabel("Pin Type"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(114,label.getHeight())); panel.add(label,BorderLayout.WEST);
        pinTypeCombo=new JComboBox(Pin.PinType.values()); pinTypeCombo.addActionListener(this); panel.add(pinTypeCombo,BorderLayout.CENTER);
        layoutPanel.add(panel);
    }

    @Override
    public void updateUI() {
        
    }
}
