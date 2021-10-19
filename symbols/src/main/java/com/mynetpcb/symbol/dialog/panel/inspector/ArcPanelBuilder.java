package com.mynetpcb.symbol.dialog.panel.inspector;

import com.mynetpcb.core.capi.panel.AbstractPanelBuilder;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.undo.MementoType;
import com.mynetpcb.core.utils.Utilities;
import com.mynetpcb.d2.shapes.Point;
import com.mynetpcb.symbol.component.SymbolComponent;
import com.mynetpcb.symbol.shape.Arc;

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

public class ArcPanelBuilder extends  AbstractPanelBuilder<Shape> { 

    private JTextField startAngField,extAngField; 
    
    public ArcPanelBuilder(SymbolComponent component) {
                super(component,new GridLayout(8,1));
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
                
                panel=new JPanel(); panel.setLayout(new BorderLayout());         
                label=new JLabel("Start Angle"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(114,label.getHeight())); panel.add(label,BorderLayout.WEST);
                startAngField=new JTextField("");  startAngField.addKeyListener(this); panel.add(startAngField,BorderLayout.CENTER);
                layoutPanel.add(panel);
        
                panel=new JPanel(); panel.setLayout(new BorderLayout());         
                label=new JLabel("Extend Angle"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(114,label.getHeight())); panel.add(label,BorderLayout.WEST);
                extAngField=new JTextField("");  extAngField.addKeyListener(this); panel.add(extAngField,BorderLayout.CENTER);
                layoutPanel.add(panel);  
    }
    public void updateUI() {   
        Arc arc=(Arc)getTarget();  
        leftField.setEnabled(arc.getResizingPoint()==null?false:true);  
        topField.setEnabled(arc.getResizingPoint()==null?false:true);
        
        leftField.setText(toUnitX(arc.getResizingPoint()==null?0:arc.getResizingPoint().x,1));
        topField.setText(toUnitY(arc.getResizingPoint()==null?0:arc.getResizingPoint().y,1)); 
        
        thicknessField.setText(String.valueOf((arc.getThickness())));    
        widthField.setText(String.valueOf(Utilities.roundDouble(arc.getShape().width)));
        heightField.setText(String.valueOf(Utilities.roundDouble( arc.getShape().height))); 
        
        startAngField.setText(String.valueOf(Utilities.roundDouble(arc.getShape().startAngle,2)));
        extAngField.setText(String.valueOf(Utilities.roundDouble(arc.getShape().endAngle,2)));
        
        
        setSelectedItem(fillCombo,arc.getFill()); 
    }
    public void actionPerformed(ActionEvent e) {
        if(e.getSource()==fillCombo){
            getTarget().setFill((Shape.Fill)fillCombo.getSelectedItem());
           this.getComponent().Repaint();
        }  
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if(e.getKeyCode() !=KeyEvent.VK_ENTER) return;
        Arc arc=(Arc)getTarget();
        if(e.getSource()==this.thicknessField){
           getTarget().setThickness((Integer.parseInt(thicknessField.getText())));  
        }
        
        if(e.getSource()==this.leftField){
           Point p=arc.getResizingPoint();
           double x=fromUnitX(leftField.getText()); 
           arc.resize((int)(x-p.x), 0, p);
        }
        
        if(e.getSource()==this.topField){
            Point p=arc.getResizingPoint();
            double y=fromUnitY(topField.getText());  
            arc.resize(0, (int)(y-p.y), p);
        }
        
        if(e.getSource()==this.widthField){
           arc.getShape().width=Double.parseDouble(widthField.getText()); 
        }
        
        if(e.getSource()==this.heightField){
           arc.getShape().height=Double.parseDouble(heightField.getText());  
        }
        
        if(e.getSource()==this.startAngField){
           arc.getShape().startAngle=Math.abs(Double.parseDouble(startAngField.getText()));  
        }        
        if(e.getSource()==this.extAngField){
           arc.getShape().endAngle=Double.parseDouble(extAngField.getText());  
        } 
        
        getComponent().getModel().getUnit().registerMemento( getTarget().getState(MementoType.MOVE_MEMENTO));
        getComponent().Repaint(); 
     
    }


}


