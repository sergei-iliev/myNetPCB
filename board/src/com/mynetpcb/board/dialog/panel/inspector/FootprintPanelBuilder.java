package com.mynetpcb.board.dialog.panel.inspector;

import com.mynetpcb.board.component.BoardComponent;
import com.mynetpcb.board.shape.PCBFootprint;
import com.mynetpcb.core.capi.event.ShapeEvent;
import com.mynetpcb.core.capi.panel.AbstractPanelBuilder;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.text.Text;
import com.mynetpcb.core.capi.text.Textable;
import com.mynetpcb.core.capi.text.Texture;
import com.mynetpcb.core.pad.Layer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class FootprintPanelBuilder  extends AbstractPanelBuilder<Shape> {
    private JComboBox chipUnitOrientationCombo, chipUnitAlignmentCombo, chipReferenceOrientationCombo, chipReferenceAlignmentCombo;

    private JTextField chipNameField, chipReferenceField, chipUnitField;
    
    public FootprintPanelBuilder(BoardComponent component) {
        super(component,new GridLayout(9, 1));
        
        panel=new JPanel(); panel.setLayout(new BorderLayout()); 
        label=new JLabel("Side"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(100,label.getHeight())); panel.add(label,BorderLayout.WEST);
        layerCombo=new JComboBox(Layer.Side.values());layerCombo.addActionListener(this);  panel.add(layerCombo,BorderLayout.CENTER);                
        layoutPanel.add(panel);
        
        //***Chip Name
        panel = new JPanel();
        panel.setLayout(new BorderLayout());
        label = new JLabel("Name");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setPreferredSize(new Dimension(100, label.getHeight()));
        panel.add(label, BorderLayout.WEST);
        chipNameField = new JTextField("");
        chipNameField.addKeyListener(this);
        panel.add(chipNameField, BorderLayout.CENTER);
        layoutPanel.add(panel);
        //***Reference Name
        panel = new JPanel();
        panel.setLayout(new BorderLayout());
        label = new JLabel("Reference");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setPreferredSize(new Dimension(100, label.getHeight()));
        panel.add(label, BorderLayout.WEST);
        chipReferenceField = new JTextField("");
        chipReferenceField.addKeyListener(this);
        panel.add(chipReferenceField, BorderLayout.CENTER);
        layoutPanel.add(panel);

        panel = new JPanel();
        panel.setLayout(new BorderLayout());
        label = new JLabel("Text Orientation");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setPreferredSize(new Dimension(100, label.getHeight()));
        panel.add(label, BorderLayout.WEST);
        chipReferenceOrientationCombo = new JComboBox(Text.Orientation.values());
        chipReferenceOrientationCombo.addActionListener(this);
        panel.add(chipReferenceOrientationCombo, BorderLayout.CENTER);
        layoutPanel.add(panel);
        //alignment
        panel = new JPanel();
        panel.setLayout(new BorderLayout());
        label = new JLabel("Text Alignment");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setPreferredSize(new Dimension(100, label.getHeight()));
        panel.add(label, BorderLayout.WEST);
        chipReferenceAlignmentCombo = new JComboBox();
        chipReferenceAlignmentCombo.addActionListener(this);
        panel.add(chipReferenceAlignmentCombo, BorderLayout.CENTER);
        layoutPanel.add(panel);
        
        //***Unit
        panel = new JPanel();
        panel.setLayout(new BorderLayout());
        label = new JLabel("Unit");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setPreferredSize(new Dimension(100, label.getHeight()));
        panel.add(label, BorderLayout.WEST);
        chipUnitField = new JTextField();
        chipUnitField.addKeyListener(this);
        panel.add(chipUnitField, BorderLayout.CENTER);
        layoutPanel.add(panel);

        panel = new JPanel();
        panel.setLayout(new BorderLayout());
        label = new JLabel("Text Orientation");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setPreferredSize(new Dimension(100, label.getHeight()));
        panel.add(label, BorderLayout.WEST);
        chipUnitOrientationCombo = new JComboBox(Text.Orientation.values());
        chipUnitOrientationCombo.addActionListener(this);
        panel.add(chipUnitOrientationCombo, BorderLayout.CENTER);
        layoutPanel.add(panel);
        
        //alignment
        panel = new JPanel();
        panel.setLayout(new BorderLayout());
        label = new JLabel("Text Alignment");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setPreferredSize(new Dimension(100, label.getHeight()));
        panel.add(label, BorderLayout.WEST);
        chipUnitAlignmentCombo = new JComboBox();
        chipUnitAlignmentCombo.addActionListener(this);
        panel.add(chipUnitAlignmentCombo, BorderLayout.CENTER);
        layoutPanel.add(panel);
   
    }

    @Override
    public void updateUI() {
        PCBFootprint symbol = (PCBFootprint)getTarget();
            //fix empty labels
        Rectangle r= symbol.calculateShape();
 
        if(symbol.getChipText().getTextureByTag("value").isEmpty()){
             symbol.getChipText().getTextureByTag("value"). getAnchorPoint().setLocation(r.getX()-10,r.getY()-10);
         }
         if(symbol.getChipText().getTextureByTag("reference").isEmpty()){
             symbol.getChipText().getTextureByTag("reference").getAnchorPoint().setLocation(r.getX(),r.getY());
         }
        chipUnitField.setText((symbol.getChipText().getTextureByTag("value").getText() ==
                               null ? "" :
                               symbol.getChipText().getTextureByTag("value").getText()));
        chipNameField.setText(symbol.getDisplayName());
        chipReferenceField.setText((symbol.getChipText().getTextureByTag("reference").getText() ==
                                    null ? "" :
                                    symbol.getChipText().getTextureByTag("reference").getText()));

        //packageNameField.setText(symbol.getPackaging()==null?"": symbol.getPackaging().getFootprintName());
        
        validateAlignmentComboText(chipUnitAlignmentCombo,symbol.getChipText().getTextureByTag("value")); 

        setSelectedIndex(chipUnitOrientationCombo,(symbol.getChipText().getTextureByTag("value").getAlignment().getOrientation().ordinal()));

        setSelectedIndex(chipReferenceOrientationCombo,(symbol.getChipText().getTextureByTag("reference").getAlignment().getOrientation().ordinal()));
        
        validateAlignmentComboText(chipReferenceAlignmentCombo,symbol.getChipText().getTextureByTag("reference")); 
        
        setSelectedItem(layerCombo, symbol.getSide());
        }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == chipReferenceOrientationCombo) {
            Texture text =
                ((Textable)getTarget()).getChipText().getTextureByTag("reference");
            text.setOrientation((text.getAlignment().getOrientation() == Text.Orientation. HORIZONTAL ? Text.Orientation.VERTICAL : Text.Orientation.HORIZONTAL));
            validateAlignmentComboText(chipReferenceAlignmentCombo,text);
            getComponent().Repaint();
        }
        if(e.getSource()==chipReferenceAlignmentCombo){
            Texture text=((Textable)getTarget()).getChipText().getTextureByTag("reference");  
            text.setAlignment(Text.Alignment.valueOf((String)chipReferenceAlignmentCombo.getSelectedItem()));
            getComponent().Repaint();
        }  
        if (e.getSource() == chipUnitOrientationCombo) {
            Texture text =
                ((Textable)getTarget()).getChipText().getTextureByTag("value");
            text.setOrientation((text.getAlignment().getOrientation() == Text.Orientation.HORIZONTAL ? Text.Orientation.VERTICAL : Text.Orientation.HORIZONTAL));
            validateAlignmentComboText(chipUnitAlignmentCombo,text);
            getComponent().Repaint();
        }
        if(e.getSource()==chipUnitAlignmentCombo){
            Texture text=((Textable)getTarget()).getChipText().getTextureByTag("value");  
            text.setAlignment(Text.Alignment.valueOf((String)chipUnitAlignmentCombo.getSelectedItem()));
            getComponent().Repaint();
        } 
        if(e.getSource()==layerCombo){
            ((PCBFootprint)getTarget()).setSide((Layer.Side)layerCombo.getSelectedItem()); 
            getComponent().Repaint();
        }
    }
    
    @Override
    public void keyPressed(KeyEvent e) {    
        if (e.getKeyCode() != KeyEvent.VK_ENTER) {
            return;
        }
        PCBFootprint symbol=(PCBFootprint)getTarget();
        //****chip handling
        if (e.getSource() == chipUnitField) {
            Texture texture=symbol.getChipText().getTextureByTag("value");                       
            texture.setText(chipUnitField.getText());            
            getComponent().Repaint();
        }
        if (e.getSource() == chipReferenceField) {
            Texture texture=symbol.getChipText().getTextureByTag("reference");                       
            texture.setText(chipReferenceField.getText());          
            getComponent().Repaint();
        }
        if (e.getSource() == chipNameField) {
            symbol.setDisplayName(chipNameField.getText());
            getComponent().getModel().getUnit().fireShapeEvent(new ShapeEvent(getTarget(), ShapeEvent.RENAME_SHAPE));
        }

    }
}
