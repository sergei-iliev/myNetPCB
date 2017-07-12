package com.mynetpcb.circuit.dialog.panel.inspector;


import com.mynetpcb.circuit.component.CircuitComponent;
import com.mynetpcb.circuit.shape.SCHConnector;
import com.mynetpcb.circuit.shape.SCHSymbol;
import com.mynetpcb.core.capi.Ownerable;
import com.mynetpcb.core.capi.Pinable;
import com.mynetpcb.core.capi.panel.AbstractPanelBuilder;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.tree.AttachedItem;
import com.mynetpcb.core.capi.undo.MementoType;
import com.mynetpcb.symbol.shape.Pin;

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
    
    private JComboBox connectorOrientationCombo, connectorTypeCombo, styleCombo;

    private JTextField connectorNameField;

    public ConnectorPanelBuilder(CircuitComponent component) {
        super(component, new GridLayout(5, 1));
        //***Connector Label
        panel = new JPanel();
        panel.setLayout(new BorderLayout());
        label = new JLabel("Label");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setPreferredSize(new Dimension(100, label.getHeight()));
        panel.add(label, BorderLayout.WEST);
        connectorNameField = new JTextField("");
        connectorNameField.addKeyListener(this);
        panel.add(connectorNameField, BorderLayout.CENTER);
        layoutPanel.add(panel);

        panel = new JPanel();
        panel.setLayout(new BorderLayout());
        label = new JLabel("Orientation");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setPreferredSize(new Dimension(100, label.getHeight()));
        panel.add(label, BorderLayout.WEST);
        connectorOrientationCombo = new JComboBox(Pinable.Orientation.values());
        connectorOrientationCombo.addActionListener(this);
        panel.add(connectorOrientationCombo, BorderLayout.CENTER);
        layoutPanel.add(panel);

        panel = new JPanel();
        panel.setLayout(new BorderLayout());
        label = new JLabel("Type");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setPreferredSize(new Dimension(100, label.getHeight()));
        panel.add(label, BorderLayout.WEST);
        connectorTypeCombo = new JComboBox(SCHConnector.Type.values());
        connectorTypeCombo.addActionListener(this);
        panel.add(connectorTypeCombo, BorderLayout.CENTER);
        layoutPanel.add(panel);
        //****Owner
        panel = new JPanel();
        panel.setLayout(new BorderLayout());
        label = new JLabel("Owner");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setPreferredSize(new Dimension(100, label.getHeight()));
        panel.add(label, BorderLayout.WEST);
        parentCombo = new JComboBox();
        parentCombo.addActionListener(this);
        panel.add(parentCombo, BorderLayout.CENTER);
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
        connectorNameField.setText(connector.getName());

        setSelectedItem(connectorOrientationCombo, connector.getOrientation());

        setSelectedItem(connectorTypeCombo,connector.getType());

        setSelectedItem(styleCombo, connector.getStyle());
        
        this.fillParentCombo(SCHSymbol.class); 
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() != KeyEvent.VK_ENTER)
            return;
        SCHConnector connector = (SCHConnector)getTarget();
        if (e.getSource() == connectorNameField) {
            connector.setName(connectorNameField.getText());
            getComponent().getModel().getUnit().registerMemento(getTarget().getState(MementoType.MOVE_MEMENTO));
            getComponent().Repaint();
        }
       
    }

    public void actionPerformed(ActionEvent e) {
        SCHConnector connector = (SCHConnector)getTarget();
        if (e.getSource() == connectorOrientationCombo) {
            connector.setOrientation((Pin.Orientation)connectorOrientationCombo.getSelectedItem());
            getComponent().Repaint();
        }
        if (e.getSource() == connectorTypeCombo) {
            connector.setType((SCHConnector.Type)connectorTypeCombo.getSelectedItem());
            getComponent().Repaint();
        }
        if (e.getSource() == styleCombo) {
            connector.setStyle((SCHConnector.Style)styleCombo.getSelectedItem());
            getComponent().Repaint();
        }
        if (e.getSource() == parentCombo) {
            Shape parent =
                getComponent().getModel().getUnit().getShape(((AttachedItem)parentCombo.getSelectedItem()).getUUID());
            ((Ownerable)getTarget()).setOwner(parent);
            getComponent().getModel().getUnit().setSelected(false);
            if (parent != null) {
                parent.setSelected(true);
                getTarget().setSelected(true);
            }
            getComponent().Repaint();
        }
        getComponent().getModel().getUnit().registerMemento(getTarget().getState(MementoType.MOVE_MEMENTO));
    }
}

