package com.mynetpcb.circuit.dialog.panel.inspector;

import com.mynetpcb.circuit.component.CircuitComponent;
import com.mynetpcb.circuit.shape.SCHConnector;
import com.mynetpcb.core.capi.panel.AbstractPanelBuilder;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.undo.MementoType;

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

public class ConnectorPanelBuilder extends AbstractPanelBuilder<Shape> {
    
    private JComboBox typeCombo, styleCombo;

    private JTextField nameField;

    public ConnectorPanelBuilder(CircuitComponent component) {
        super(component, new GridLayout(3, 1));
        //***Connector Label
        panel = new JPanel();
        panel.setLayout(new BorderLayout());
        label = new JLabel("Label");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setPreferredSize(new Dimension(100, label.getHeight()));
        panel.add(label, BorderLayout.WEST);
        nameField = new JTextField("");
        nameField.addKeyListener(this);
        panel.add(nameField, BorderLayout.CENTER);
        layoutPanel.add(panel);


        panel = new JPanel();
        panel.setLayout(new BorderLayout());
        label = new JLabel("Type");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setPreferredSize(new Dimension(100, label.getHeight()));
        panel.add(label, BorderLayout.WEST);
        typeCombo = new JComboBox(SCHConnector.Type.values());
        typeCombo.addActionListener(this);
        panel.add(typeCombo, BorderLayout.CENTER);
        layoutPanel.add(panel);

        //****Style
        panel = new JPanel();
        panel.setLayout(new BorderLayout());
        label = new JLabel("Style");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setPreferredSize(new Dimension(100, label.getHeight()));
        panel.add(label, BorderLayout.WEST);
        styleCombo = new JComboBox(SCHConnector.Style.values());
        styleCombo.addActionListener(this);
        panel.add(styleCombo, BorderLayout.CENTER);
        layoutPanel.add(panel);

    }

    public void updateUI() {
        SCHConnector connector = (SCHConnector)getTarget();
        nameField.setText(connector.getTextureByTag("name").getText());

        setSelectedItem(typeCombo,connector.getType());

        setSelectedItem(styleCombo, connector.getStyle());
                
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() != KeyEvent.VK_ENTER)
            return;
        SCHConnector connector = (SCHConnector)getTarget();
        if (e.getSource() == nameField) {
            connector.setText(nameField.getText());
            getComponent().getModel().getUnit().registerMemento(getTarget().getState(MementoType.MOVE_MEMENTO));
            getComponent().Repaint();
        }
       
    }

    public void actionPerformed(ActionEvent e) {
        SCHConnector connector = (SCHConnector)getTarget();

        if (e.getSource() == typeCombo) {
            connector.setType((SCHConnector.Type)typeCombo.getSelectedItem());
            getComponent().Repaint();
        }
        if (e.getSource() == styleCombo) {
            connector.setStyle((SCHConnector.Style)styleCombo.getSelectedItem());
            getComponent().Repaint();
        }

        getComponent().getModel().getUnit().registerMemento(getTarget().getState(MementoType.MOVE_MEMENTO));
    }
}

