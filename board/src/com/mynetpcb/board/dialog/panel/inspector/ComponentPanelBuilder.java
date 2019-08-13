package com.mynetpcb.board.dialog.panel.inspector;

import com.mynetpcb.board.component.BoardComponent;
import com.mynetpcb.core.capi.event.ContainerEvent;
import com.mynetpcb.core.capi.panel.AbstractPanelBuilder;
import com.mynetpcb.core.capi.shape.Shape;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;


public class ComponentPanelBuilder extends AbstractPanelBuilder<Shape>{
    
    public ComponentPanelBuilder(BoardComponent component) {
        super(component,new GridLayout(1,1));
        //***component name
        panel=new JPanel(); panel.setLayout(new BorderLayout()); 
        label=new JLabel("Name"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(100,25)); panel.add(label,BorderLayout.WEST);
        thicknessField=new JTextField(""); thicknessField.addKeyListener(this); panel.add(thicknessField,BorderLayout.CENTER);
        layoutPanel.add(panel);  
    }

    @Override
    public void updateUI() {
        thicknessField.setText(getComponent().getModel().getFormatedFileName());  
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

    }
}


