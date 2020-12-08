package com.mynetpcb.board.dialog.panel.inspector;

import com.mynetpcb.board.component.BoardComponent;
import com.mynetpcb.board.shape.PCBFootprint;
import com.mynetpcb.core.capi.layer.Layer;
import com.mynetpcb.core.capi.panel.AbstractPanelBuilder;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.text.Texture;
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

public class FootprintPanelBuilder  extends AbstractPanelBuilder<Shape> {

    private JTextField  referenceField, valueField;
    
    public FootprintPanelBuilder(BoardComponent component) {
        super(component,new GridLayout(5, 1));
        
        panel=new JPanel(); panel.setLayout(new BorderLayout()); 
        label=new JLabel("Side"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(100,label.getHeight())); panel.add(label,BorderLayout.WEST);
        layerCombo=new JComboBox(Layer.Side.values());layerCombo.addActionListener(this);  panel.add(layerCombo,BorderLayout.CENTER);                
        layoutPanel.add(panel);
        
        //***Name
        panel = new JPanel();
        panel.setLayout(new BorderLayout());
        label = new JLabel("Name");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setPreferredSize(new Dimension(100, label.getHeight()));
        panel.add(label, BorderLayout.WEST);
        nameField = new JTextField("");
        nameField.addKeyListener(this);
        panel.add(nameField, BorderLayout.CENTER);
        layoutPanel.add(panel);
        
        //***Reference
        panel = new JPanel();
        panel.setLayout(new BorderLayout());
        label = new JLabel("Reference");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setPreferredSize(new Dimension(100, label.getHeight()));
        panel.add(label, BorderLayout.WEST);
        referenceField = new JTextField("");
        referenceField.addKeyListener(this);
        panel.add(referenceField, BorderLayout.CENTER);
        layoutPanel.add(panel);


        
        //***Value
        panel = new JPanel();
        panel.setLayout(new BorderLayout());
        label = new JLabel("Value");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setPreferredSize(new Dimension(100, label.getHeight()));
        panel.add(label, BorderLayout.WEST);
        valueField = new JTextField();
        valueField.addKeyListener(this);
        panel.add(valueField, BorderLayout.CENTER);
        layoutPanel.add(panel);

        //rotate
        panel=new JPanel(); panel.setLayout(new BorderLayout());
        label=new JLabel("Rotate"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(100,label.getHeight())); panel.add(label,BorderLayout.WEST);
        rotateField=new JTextField(); rotateField.addKeyListener(this); panel.add(rotateField,BorderLayout.CENTER);
        layoutPanel.add(panel);
        

   
    }

    @Override
    public void updateUI() {
        PCBFootprint symbol = (PCBFootprint)getTarget();
            //fix empty labels
        referenceField.setText(symbol.getTextureByTag("reference").getText());
        valueField.setText(symbol.getTextureByTag("value").getText());
        rotateField.setText(String.valueOf(symbol.getRotate()));
        //packageNameField.setText(symbol.getPackaging()==null?"": symbol.getPackaging().getFootprintName());
        nameField.setText(symbol.getDisplayName()); 
        setSelectedItem(layerCombo, symbol.getSide());
        }

    public void actionPerformed(ActionEvent e) {
 
        if(e.getSource()==layerCombo){
            ((PCBFootprint)getTarget()).setSide((Layer.Side)layerCombo.getSelectedItem()); 
            getComponent().getModel().getUnit().getShapes().reorder();
            getComponent().getModel().getUnit().registerMemento( getTarget().getState(MementoType.MOVE_MEMENTO));
            getComponent().Repaint();
        }
    }
    
    @Override
    public void keyPressed(KeyEvent e) {    
        if (e.getKeyCode() != KeyEvent.VK_ENTER) {
            return;
        }
        PCBFootprint symbol=(PCBFootprint)getTarget();
        if(e.getSource()==this.rotateField){
           symbol.setRotation(Double.parseDouble(this.rotateField.getText()),symbol.getCenter()); 
        }
        if(e.getSource()==this.nameField){
           symbol.setDisplayName(nameField.getText()); 
        }        
        if (e.getSource() == valueField) {
             Texture texture=symbol.getTextureByTag("value");   
             boolean empty=texture.isEmpty();
             texture.setText(valueField.getText());  
            if(empty){
                texture.move(symbol.getCenter().x, symbol.getCenter().y);
            }
        }
        if (e.getSource() == referenceField) {
             Texture texture=symbol.getTextureByTag("reference");                       
             boolean empty=texture.isEmpty();
             texture.setText(referenceField.getText());  
             if(empty){
                 texture.move(symbol.getCenter().x, symbol.getCenter().y);
             }
        }
        getComponent().getModel().getUnit().registerMemento( getTarget().getState(MementoType.MOVE_MEMENTO));
        getComponent().Repaint();
    }
}
