package com.mynetpcb.symbol.dialog.panel.inspector;

import com.mynetpcb.core.capi.component.UnitComponent;
import com.mynetpcb.core.capi.panel.AbstractPanelBuilder;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.undo.MementoType;
import com.mynetpcb.core.utils.Utilities;
import com.mynetpcb.d2.shapes.Point;
import com.mynetpcb.symbol.shape.RoundRect;

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

public class RectPanelBuilder extends AbstractPanelBuilder<Shape>{

    private JTextField roundCornerField;
    
    public RectPanelBuilder(UnitComponent component) {
         super(component,new GridLayout(5,1));       
        //***Left        
                panel=new JPanel(); panel.setLayout(new BorderLayout());
                label=new JLabel("X"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(114,label.getHeight())); panel.add(label,BorderLayout.WEST);
                leftField=new JTextField("0");leftField.addKeyListener(this);  panel.add(leftField,BorderLayout.CENTER);
                layoutPanel.add(panel);
        
        //***Top        
                panel=new JPanel(); panel.setLayout(new BorderLayout());
                label=new JLabel("Y"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(114,label.getHeight())); panel.add(label,BorderLayout.WEST);
                topField=new JTextField("0"); topField.addKeyListener(this); panel.add(topField,BorderLayout.CENTER);
                layoutPanel.add(panel);              
        //***Thickness        
                panel=new JPanel(); panel.setLayout(new BorderLayout()); 
                label=new JLabel("Thickness"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(114,label.getHeight())); panel.add(label,BorderLayout.WEST);
                thicknessField=new JTextField("0"); thicknessField.addKeyListener(this); panel.add(thicknessField,BorderLayout.CENTER);
                layoutPanel.add(panel);   
        //***Fill
                panel=new JPanel(); panel.setLayout(new BorderLayout()); 
                label=new JLabel("Fill"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(114,label.getHeight())); panel.add(label,BorderLayout.WEST);
                fillCombo=new JComboBox(fillValues);fillCombo.addActionListener(this);  panel.add(fillCombo,BorderLayout.CENTER);
                layoutPanel.add(panel);
        //****Round Corner
                panel=new JPanel(); panel.setLayout(new BorderLayout()); 
                label=new JLabel("Rounding"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(114,label.getHeight())); panel.add(label,BorderLayout.WEST);
                roundCornerField=new JTextField("");  roundCornerField.addKeyListener(this); panel.add(roundCornerField,BorderLayout.CENTER);
                layoutPanel.add(panel);

    }

    @Override
    public void updateUI() {
        RoundRect rect=(RoundRect)getTarget();
        leftField.setEnabled(rect.getResizingPoint()==null?false:true);  
        topField.setEnabled(rect.getResizingPoint()==null?false:true);
        thicknessField.setText(String.valueOf((rect.getThickness())));    
        leftField.setText(toUnitX(rect.getResizingPoint()==null?0:Utilities.roundDouble(rect.getResizingPoint().x)));
        topField.setText(toUnitY(rect.getResizingPoint()==null?0:Utilities.roundDouble(rect.getResizingPoint().y))); 
        roundCornerField.setText(String.valueOf((rect.getRounding()))); 
        setSelectedIndex(fillCombo,(rect.getFill()==Shape.Fill.EMPTY?0:1));    
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource()==fillCombo){
            getTarget().setFill(Shape.Fill.values()[fillCombo.getSelectedIndex()]);
           this.getComponent().Repaint();
        }
    }
    
    @Override
    public void keyReleased(KeyEvent e){
        if(e.getKeyCode()!=KeyEvent.VK_ENTER) return;
        RoundRect rect=(RoundRect)getTarget(); 
        if(e.getSource()==this.thicknessField){
        getTarget().setThickness((int)(Double.parseDouble(thicknessField.getText())));  
        }
        if(e.getSource()==this.roundCornerField){
          rect.setRounding((int)(Double.parseDouble(roundCornerField.getText())));  
        }
        if(e.getSource()==this.leftField){
           Point p=rect.getResizingPoint();
           double x=fromUnitX(leftField.getText()); 
           rect.resize((int)(x-p.x), 0, p);
        }
        
        if(e.getSource()==this.topField){
            Point p=rect.getResizingPoint();
            double y=fromUnitY(topField.getText());  
            rect.resize(0, (int)(y-p.y), p);
        }
    
        getComponent().getModel().getUnit().registerMemento( getTarget().getState(MementoType.MOVE_MEMENTO));
        getComponent().Repaint();  
    }
    
}

