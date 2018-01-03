package com.mynetpcb.circuit.dialog.panel.inspector;


import com.mynetpcb.circuit.component.CircuitComponent;
import com.mynetpcb.circuit.shape.SCHLabel;
import com.mynetpcb.circuit.shape.SCHSymbol;
import com.mynetpcb.core.capi.Ownerable;
import com.mynetpcb.core.capi.gui.button.JColorButton;
import com.mynetpcb.core.capi.gui.button.JColorButton.ColorChangedListener;
import com.mynetpcb.core.capi.panel.AbstractPanelBuilder;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.text.Text;
import com.mynetpcb.core.capi.text.Texture;
import com.mynetpcb.core.capi.text.font.FontTexture;
import com.mynetpcb.core.capi.tree.AttachedItem;
import com.mynetpcb.core.capi.undo.MementoType;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;


public class LabelPanelBuilder extends AbstractPanelBuilder<Shape> implements ColorChangedListener{
    
    private JTextField textField;
    private JColorButton colorButton;
    
    public LabelPanelBuilder(CircuitComponent component) {
        super(component,new GridLayout(7,1));

        //***BusPin Name        
        panel=new JPanel(); panel.setLayout(new BorderLayout());
        label=new JLabel("Caption"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(100,label.getHeight())); panel.add(label,BorderLayout.WEST);
        textField=new JTextField("???"); textField.addKeyListener(this); panel.add(textField,BorderLayout.CENTER);
        layoutPanel.add(panel);
        //size
        panel=new JPanel(); panel.setLayout(new BorderLayout());
        label=new JLabel("Size"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(100,label.getHeight())); panel.add(label,BorderLayout.WEST);
        heightField=new JTextField(); heightField.addKeyListener(this); panel.add(heightField,BorderLayout.CENTER);
        layoutPanel.add(panel);
        
        panel=new JPanel(); panel.setLayout(new BorderLayout());
        label=new JLabel("Color"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(100,label.getHeight())); panel.add(label,BorderLayout.WEST);
        colorButton=new JColorButton(this);panel.add(colorButton,BorderLayout.CENTER);
        layoutPanel.add(panel);
        //style
        panel=new JPanel(); panel.setLayout(new BorderLayout()); 
        label=new JLabel("Font Style"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(100,label.getHeight())); panel.add(label,BorderLayout.WEST);
        styleCombo=new JComboBox(Text.Style.values());styleCombo.addActionListener(this);  panel.add(styleCombo,BorderLayout.CENTER);
        layoutPanel.add(panel); 
        //alignment
        panel=new JPanel(); panel.setLayout(new BorderLayout()); 
        label=new JLabel("Text Alignment"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(100,label.getHeight())); panel.add(label,BorderLayout.WEST);
        textAlignmentCombo=new JComboBox();textAlignmentCombo.addActionListener(this);  panel.add(textAlignmentCombo,BorderLayout.CENTER);
        layoutPanel.add(panel);  
        //****orientation        
        panel=new JPanel(); panel.setLayout(new BorderLayout());
        label=new JLabel("Text Orientation"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(100,label.getHeight())); panel.add(label,BorderLayout.WEST);
        textOrientationCombo=new JComboBox(Text.Orientation.values());textOrientationCombo.addActionListener(this);  panel.add(textOrientationCombo,BorderLayout.CENTER);
        layoutPanel.add(panel);      
        //****Owner        
        panel=new JPanel(); panel.setLayout(new BorderLayout());
        label=new JLabel("Owner"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(100,label.getHeight())); panel.add(label,BorderLayout.WEST);
        parentCombo=new JComboBox();parentCombo.addActionListener(this);  panel.add(parentCombo,BorderLayout.CENTER);
        layoutPanel.add(panel);
        
    }


    public void actionPerformed(ActionEvent e) {
        SCHLabel label=(SCHLabel)getTarget();
        if(e.getSource()==textOrientationCombo){
                Texture text=label.getTexture(); 
                AffineTransform rotation=AffineTransform.getRotateInstance(Math.PI/2,text.getBoundingShape().getCenterX(),text.getBoundingShape().getCenterY());
                text.Rotate(rotation); 
                validateAlignmentComboText(textAlignmentCombo,text);
        } 
        if(e.getSource()==textAlignmentCombo){
            Texture text=label.getTexture();  
            text.setAlignment(Text.Alignment.valueOf((String)textAlignmentCombo.getSelectedItem()));    
        }   
        if(e.getSource()==styleCombo){
            FontTexture text=(FontTexture)label.getTexture(); 
            text.setStyle((Text.Style)styleCombo.getSelectedItem());                                
        }      
        if(e.getSource()==parentCombo){
             Shape parent=getComponent().getModel().getUnit().getShape(((AttachedItem)parentCombo.getSelectedItem()).getUUID());
            ((Ownerable) getTarget()).setOwner(parent);  
             getComponent().getModel().getUnit().setSelected(false);
             if(parent!=null){
                parent.setSelected(true);
             }   
        }
        getComponent().getModel().getUnit().registerMemento(getTarget().getState(MementoType.MOVE_MEMENTO));
        getComponent().Repaint();
    }
    
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode()!=KeyEvent.VK_ENTER) return;
        SCHLabel label=(SCHLabel)getTarget();
             
        if(e.getSource()==textField&&textField.getText().length()>0){
            label.getTexture().setText(textField.getText());          
           //***notify jTree of the rename           
        } 
        if(e.getSource()==heightField&&heightField.getText().length()>0){
            label.getTexture().setSize(Integer.parseInt(heightField.getText()));          
           //***notify jTree of the rename           
        } 
        getComponent().getModel().getUnit().registerMemento(getTarget().getState(MementoType.MOVE_MEMENTO));
        getComponent().Repaint();
        
    }

    public void updateUI() {
        
        
        
        //System.out.println(("#"+Integer.toHexString(c.)));
        SCHLabel label=(SCHLabel)getTarget(); 
        colorButton.setSelectedColor(label.getTexture().getFillColor());
        textField.setText(label.getTexture().getText());                                                             
        heightField.setText(String.valueOf( label.getTexture().getSize()));
        setSelectedIndex(textOrientationCombo,(label.getTexture().getAlignment().getOrientation() == Text.Orientation.HORIZONTAL?0:1));
        
        setSelectedIndex(styleCombo,((FontTexture)label.getTexture()).getStyle().ordinal());
        validateAlignmentComboText(textAlignmentCombo,label.getTexture()); 

        this.fillParentCombo(SCHSymbol.class);     
    }

    @Override
    public void colorChanged(Color color) {
        SCHLabel label=(SCHLabel)getTarget(); 
        getComponent().getModel().getUnit().registerMemento(getTarget().getState(MementoType.MOVE_MEMENTO));
        label.getTexture().setFillColor(color);
    }
}

