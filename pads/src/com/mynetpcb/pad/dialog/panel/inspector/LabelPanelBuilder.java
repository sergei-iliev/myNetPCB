package com.mynetpcb.pad.dialog.panel.inspector;


import com.mynetpcb.core.capi.Grid;
import com.mynetpcb.core.capi.panel.AbstractPanelBuilder;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.text.Text;
import com.mynetpcb.core.capi.undo.MementoType;
import com.mynetpcb.core.pad.Layer;
import com.mynetpcb.pad.component.FootprintComponent;
import com.mynetpcb.pad.shape.GlyphLabel;

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


public class LabelPanelBuilder extends AbstractPanelBuilder<Shape>{     
    
    private JTextField textField;

    public LabelPanelBuilder(FootprintComponent component) {
         super(component,new GridLayout(8,1));
        //layer
                panel=new JPanel(); panel.setLayout(new BorderLayout()); 
                label=new JLabel("Layer"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(114,label.getHeight())); panel.add(label,BorderLayout.WEST);
                layerCombo=new JComboBox(Layer.PCB_SYMBOL_LAYERS);layerCombo.addActionListener(this);  panel.add(layerCombo,BorderLayout.CENTER);                
                layoutPanel.add(panel);
        //***X        
                panel=new JPanel(); panel.setLayout(new BorderLayout());
                label=new JLabel("X"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(114,label.getHeight())); panel.add(label,BorderLayout.WEST);
                leftField=new JTextField("0");leftField.addKeyListener(this);  panel.add(leftField,BorderLayout.CENTER);
                layoutPanel.add(panel);
        
        //***Y        
                panel=new JPanel(); panel.setLayout(new BorderLayout());
                label=new JLabel("Y"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(114,label.getHeight())); panel.add(label,BorderLayout.WEST);
                topField=new JTextField("0"); topField.addKeyListener(this); panel.add(topField,BorderLayout.CENTER);
                layoutPanel.add(panel);
                
                panel=new JPanel(); panel.setLayout(new BorderLayout());
                label=new JLabel("Text"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(114,label.getHeight())); panel.add(label,BorderLayout.WEST);
                textField=new JTextField("???"); textField.addKeyListener(this); panel.add(textField,BorderLayout.CENTER);
                layoutPanel.add(panel);
        
                panel=new JPanel(); panel.setLayout(new BorderLayout());
                label=new JLabel("Size"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(114,label.getHeight())); panel.add(label,BorderLayout.WEST);
                heightField=new JTextField(); heightField.addKeyListener(this); panel.add(heightField,BorderLayout.CENTER);
                layoutPanel.add(panel);
        
                panel=new JPanel(); panel.setLayout(new BorderLayout());
                label=new JLabel("Thickness"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(114,label.getHeight())); panel.add(label,BorderLayout.WEST);
                thicknessField=new JTextField(); thicknessField.addKeyListener(this); panel.add(thicknessField,BorderLayout.CENTER);
                layoutPanel.add(panel);
        
                panel=new JPanel(); panel.setLayout(new BorderLayout());
                label=new JLabel("Orientation"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(114,label.getHeight())); panel.add(label,BorderLayout.WEST);
                textOrientationCombo=new JComboBox(Text.Orientation.values());textOrientationCombo.addActionListener(this);  panel.add(textOrientationCombo,BorderLayout.CENTER);
                layoutPanel.add(panel);
                
                panel=new JPanel(); panel.setLayout(new BorderLayout()); 
                label=new JLabel("Text Alignment"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(114,label.getHeight())); panel.add(label,BorderLayout.WEST);
                textAlignmentCombo=new JComboBox();textAlignmentCombo.addActionListener(this);  panel.add(textAlignmentCombo,BorderLayout.CENTER);
                layoutPanel.add(panel);
        
            
    }

    @Override
    public void updateUI() {
        GlyphLabel label=(GlyphLabel)getTarget();        
        setSelectedIndex(textOrientationCombo,(label.getTexture().getAlignment().getOrientation().ordinal()));    
        textField.setText(label.getTexture().getText());
        //leftField.setText(toUnitX(label.getTexture().getAnchorPoint().x ));
        //topField.setText(toUnitY(label.getTexture().getAnchorPoint().y));
        heightField.setText(String.valueOf(Grid.COORD_TO_MM(label.getTexture().getSize())));
        thicknessField.setText(String.valueOf(Grid.COORD_TO_MM(label.getTexture().getThickness())));
        validateAlignmentComboText(textAlignmentCombo,label.getTexture());            
        setSelectedItem(layerCombo, label.getCopper());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        GlyphLabel label=(GlyphLabel)getTarget(); 
        if(e.getSource()==textOrientationCombo){ 
            label.getTexture().setOrientation((Text.Orientation)textOrientationCombo.getSelectedItem());
            validateAlignmentComboText(textAlignmentCombo,label.getTexture());
        } 
        if(e.getSource()==textAlignmentCombo){;  
            label.getTexture().setAlignment(Text.Alignment.valueOf((String)textAlignmentCombo.getSelectedItem()));
        }
        if(e.getSource()==layerCombo){
           getTarget().setCopper((Layer.Copper)layerCombo.getSelectedItem());
        }
        getComponent().getModel().getUnit().registerMemento( getTarget().getState(MementoType.MOVE_MEMENTO));
        getComponent().Repaint();
    }
    
    @Override
    public void keyReleased(KeyEvent e){
        if(e.getKeyCode()!=KeyEvent.VK_ENTER) return;
        GlyphLabel label=(GlyphLabel)getTarget();     
        
        if(e.getSource()==textField&&textField.getText().length()>0){
            label.getTexture().setText(textField.getText());
        }
          
        if(e.getSource()==this.heightField){
           label.getTexture().setSize(Grid.MM_TO_COORD(Double.parseDouble(heightField.getText())));  
        }
        if(e.getSource()==this.thicknessField){
           label.getTexture().setThickness(Grid.MM_TO_COORD(Double.parseDouble(thicknessField.getText())));  
        }
        if(e.getSource()==this.leftField){           
           label.getTexture().getAnchorPoint().x=fromUnitX(leftField.getText());
        }
        
        if(e.getSource()==this.topField){
           label.getTexture().getAnchorPoint().y=fromUnitY(topField.getText());
        }
        getComponent().getModel().getUnit().registerMemento( getTarget().getState(MementoType.MOVE_MEMENTO));
        getComponent().Repaint();         
        
    }
}
