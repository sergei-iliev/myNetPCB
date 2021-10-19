package com.mynetpcb.symbol.dialog.panel.inspector;

import com.mynetpcb.core.capi.panel.AbstractPanelBuilder;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.undo.MementoType;
import com.mynetpcb.d2.shapes.Point;
import com.mynetpcb.symbol.component.SymbolComponent;
import com.mynetpcb.symbol.shape.ArrowLine;

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

public class ArrowPanelBuilder extends AbstractPanelBuilder<Shape> {
    
    private JTextField headField;
    
    public ArrowPanelBuilder(SymbolComponent component) {
        super(component, new GridLayout(5, 1));
        //***Left
        panel = new JPanel();
        panel.setLayout(new BorderLayout());
        label = new JLabel("X");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setPreferredSize(new Dimension(114, label.getHeight()));
        panel.add(label, BorderLayout.WEST);
        leftField = new JTextField("0");
        leftField.addKeyListener(this);
        panel.add(leftField, BorderLayout.CENTER);
        layoutPanel.add(panel);

        //***Top
        panel = new JPanel();
        panel.setLayout(new BorderLayout());
        label = new JLabel("Y");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setPreferredSize(new Dimension(114, label.getHeight()));
        panel.add(label, BorderLayout.WEST);
        topField = new JTextField("0");
        topField.addKeyListener(this);
        panel.add(topField, BorderLayout.CENTER);
        layoutPanel.add(panel);
        //***Thickness
        panel = new JPanel();
        panel.setLayout(new BorderLayout());
        label = new JLabel("Thickness");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setPreferredSize(new Dimension(114, label.getHeight()));
        panel.add(label, BorderLayout.WEST);
        thicknessField = new JTextField("0");
        thicknessField.addKeyListener(this);
        panel.add(thicknessField, BorderLayout.CENTER);
        layoutPanel.add(panel);        
        
        panel=new JPanel(); panel.setLayout(new BorderLayout()); 
        label=new JLabel("Fill"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(114,label.getHeight())); panel.add(label,BorderLayout.WEST);
        fillCombo=new JComboBox(fillValues);fillCombo.addActionListener(this);  panel.add(fillCombo,BorderLayout.CENTER);
        layoutPanel.add(panel);
        
        //head size
        panel = new JPanel();
        panel.setLayout(new BorderLayout());
        label = new JLabel("Head size");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setPreferredSize(new Dimension(114, label.getHeight()));
        panel.add(label, BorderLayout.WEST);
        headField = new JTextField("0");
        headField.addKeyListener(this);
        panel.add(headField, BorderLayout.CENTER);
        layoutPanel.add(panel);        
    }

    @Override
    public void updateUI() {
        ArrowLine arrow = (ArrowLine)getTarget();
        Point p = arrow.getResizingPoint();
        leftField.setEnabled(p == null ? false : true);
        topField.setEnabled(p == null ? false : true);
        leftField.setText(toUnitX(p == null ? 0 : p.x,1));
        topField.setText(toUnitY(p == null ? 0 : p.y,1));
        headField.setText(String.valueOf(arrow.getHeadSize())); 
        thicknessField.setText(String.valueOf((arrow.getThickness())));
        setSelectedItem(fillCombo,(arrow.getFill().toString()));  
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() != KeyEvent.VK_ENTER)
            return;
        ArrowLine arrow = (ArrowLine)getTarget();
        if (e.getSource() == this.leftField) {
            Point p = arrow.getResizingPoint();
            p.x = fromUnitX(leftField.getText());
        }

        if (e.getSource() == this.topField) {
            Point p = arrow.getResizingPoint();
            p.y = fromUnitY(topField.getText());
        }
        if(e.getSource()==headField){ 
          arrow.setHeadSize(Integer.parseInt(headField.getText())); 
       }  
        if (e.getSource() == this.thicknessField) {
            arrow.setThickness((Integer.parseInt(thicknessField.getText())));
        }
        getComponent().getModel().getUnit().registerMemento(getTarget().getState(MementoType.MOVE_MEMENTO));
        getComponent().Repaint();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource()==fillCombo){
            getTarget().setFill(Shape.Fill.valueOf((String)fillCombo.getSelectedItem()));
           this.getComponent().Repaint();
        }
    }
}
