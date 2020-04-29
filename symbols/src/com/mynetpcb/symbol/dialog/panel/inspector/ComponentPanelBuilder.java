package com.mynetpcb.symbol.dialog.panel.inspector;

import com.mynetpcb.core.capi.Typeable;
import com.mynetpcb.core.capi.event.ContainerEvent;
import com.mynetpcb.core.capi.panel.AbstractPanelBuilder;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.symbol.component.SymbolComponent;

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

public class ComponentPanelBuilder extends AbstractPanelBuilder<Shape>{
    
    private JComboBox symbolTypeCombo;
    
    public ComponentPanelBuilder(SymbolComponent component) {
        super(component,new GridLayout(2,1));
        //***component name
        panel=new JPanel(); panel.setLayout(new BorderLayout()); 
        label=new JLabel("Name"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(100,25)); panel.add(label,BorderLayout.WEST);
        thicknessField=new JTextField(""); thicknessField.addKeyListener(this); panel.add(thicknessField,BorderLayout.CENTER);
        layoutPanel.add(panel); 

        panel=new JPanel(); panel.setLayout(new BorderLayout()); 
        label=new JLabel("Type"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(100,label.getHeight())); panel.add(label,BorderLayout.WEST);
        symbolTypeCombo=new JComboBox(Typeable.Type.values());symbolTypeCombo.addActionListener(this);  panel.add(symbolTypeCombo,BorderLayout.CENTER);
        layoutPanel.add(panel);  
    }

    @Override
    public void updateUI() {
        thicknessField.setText(getComponent().getModel().getFormatedFileName());  
        setSelectedItem(symbolTypeCombo,((SymbolComponent)getComponent()).getModel().getType());
    }


    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode()!=KeyEvent.VK_ENTER) return;
        
        if(e.getSource()==this.thicknessField){
           getComponent().getModel().setFileName(thicknessField.getText());
           getComponent().fireContainerEvent(new ContainerEvent(null,ContainerEvent.RENAME_CONTAINER));
        }         
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource()==symbolTypeCombo){
            ((SymbolComponent)getComponent()).getModel().setType((Typeable.Type)symbolTypeCombo.getSelectedItem());  
        }

    }
}

