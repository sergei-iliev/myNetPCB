package com.mynetpcb.symbol.dialog.panel.inspector;

import com.mynetpcb.core.capi.panel.AbstractPanelBuilder;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.undo.MementoType;
import com.mynetpcb.d2.shapes.Point;
import com.mynetpcb.symbol.component.SymbolComponent;
import com.mynetpcb.symbol.shape.Ellipse;

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

public class EllipsePanelBuilder extends  AbstractPanelBuilder<Shape> { 


    public EllipsePanelBuilder(SymbolComponent component) {
                super(component,new GridLayout(6,1));
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
                fillCombo=new JComboBox(Shape.Fill.values());fillCombo.addActionListener(this);  panel.add(fillCombo,BorderLayout.CENTER);
                layoutPanel.add(panel);        
        //****Width
                panel=new JPanel(); panel.setLayout(new BorderLayout()); 
                label=new JLabel("Radius X"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(114,label.getHeight())); panel.add(label,BorderLayout.WEST);
                widthField=new JTextField("");  widthField.addKeyListener(this);panel.add(widthField,BorderLayout.CENTER);
                layoutPanel.add(panel);  
        //****Height
                panel=new JPanel(); panel.setLayout(new BorderLayout()); 
                label=new JLabel("Radius Y"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(114,label.getHeight())); panel.add(label,BorderLayout.WEST);
                heightField=new JTextField("");  heightField.addKeyListener(this); panel.add(heightField,BorderLayout.CENTER);
                layoutPanel.add(panel);       
    }
    public void updateUI() {   
        Ellipse ellipse=(Ellipse)getTarget();  
        leftField.setEnabled(ellipse.getResizingPoint()==null?false:true);  
        topField.setEnabled(ellipse.getResizingPoint()==null?false:true);
        
        leftField.setText(toUnitX(ellipse.getResizingPoint()==null?0:ellipse.getResizingPoint().x));
        topField.setText(toUnitY(ellipse.getResizingPoint()==null?0:ellipse.getResizingPoint().y)); 
        
        thicknessField.setText(String.valueOf((ellipse.getThickness())));    
        widthField.setText(String.valueOf((ellipse.getShape().width)));
        heightField.setText(String.valueOf(( ellipse.getShape().height))); 
        
        setSelectedItem(fillCombo,ellipse.getFill()); 
    }
    public void actionPerformed(ActionEvent e) {
        if(e.getSource()==fillCombo){
           getTarget().setFill((Shape.Fill)fillCombo.getSelectedItem());
           getComponent().getModel().getUnit().registerMemento(getTarget().getState(MementoType.MOVE_MEMENTO));
           this.getComponent().Repaint();
        }  
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if(e.getKeyCode() !=KeyEvent.VK_ENTER) return;
        Ellipse ellipse=(Ellipse)getTarget();
        if(e.getSource()==this.thicknessField){
           getTarget().setThickness((Integer.parseInt(thicknessField.getText())));  
        }
        
        if(e.getSource()==this.leftField){
           Point p=ellipse.getResizingPoint();
           double x=fromUnitX(leftField.getText()); 
           ellipse.resize((int)(x-p.x), 0, p);
        }
        
        if(e.getSource()==this.topField){
            Point p=ellipse.getResizingPoint();
            double y=fromUnitY(topField.getText());  
            ellipse.resize(0, (int)(y-p.y), p);
        }
        
        if(e.getSource()==this.widthField){
           ellipse.getShape().width=Double.parseDouble(widthField.getText()); 
        }
        
        if(e.getSource()==this.heightField){
           ellipse.getShape().height=Double.parseDouble(heightField.getText());  
        }
        getComponent().getModel().getUnit().registerMemento( getTarget().getState(MementoType.MOVE_MEMENTO));
        getComponent().Repaint(); 
     
    }


}

