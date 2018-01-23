package com.mynetpcb.symbol.dialog.panel.inspector;


import com.mynetpcb.core.capi.gui.button.JColorButton;
import com.mynetpcb.core.capi.gui.button.JColorButton.ColorChangedListener;
import com.mynetpcb.core.capi.panel.AbstractPanelBuilder;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.text.Text;
import com.mynetpcb.core.capi.text.font.FontTexture;
import com.mynetpcb.core.capi.undo.MementoType;
import com.mynetpcb.symbol.component.SymbolComponent;
import com.mynetpcb.symbol.shape.FontLabel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;


public class LabelPanelBuilder extends AbstractPanelBuilder<Shape> implements ColorChangedListener{      
    
    private JTextField textField;
    private JColorButton colorButton;

    public LabelPanelBuilder(SymbolComponent component) {
         super(component,new GridLayout(8,1));
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
                label=new JLabel("Caption"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(114,label.getHeight())); panel.add(label,BorderLayout.WEST);
                textField=new JTextField("???"); textField.addKeyListener(this); panel.add(textField,BorderLayout.CENTER);
                layoutPanel.add(panel);
        
                panel=new JPanel(); panel.setLayout(new BorderLayout());
                label=new JLabel("Size"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(114,label.getHeight())); panel.add(label,BorderLayout.WEST);
                heightField=new JTextField("???"); heightField.addKeyListener(this); panel.add(heightField,BorderLayout.CENTER);
                layoutPanel.add(panel);

        
                panel=new JPanel(); panel.setLayout(new BorderLayout());
                label=new JLabel("Color"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(114,label.getHeight())); panel.add(label,BorderLayout.WEST);
                colorButton=new JColorButton(this);panel.add(colorButton,BorderLayout.CENTER);
                layoutPanel.add(panel);
        
                panel=new JPanel(); panel.setLayout(new BorderLayout()); 
                label=new JLabel("Font Style"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(114,label.getHeight())); panel.add(label,BorderLayout.WEST);
                styleCombo=new JComboBox(Text.Style.values());styleCombo.addActionListener(this);  panel.add(styleCombo,BorderLayout.CENTER);
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
          FontLabel label=(FontLabel)getTarget();
          
          setSelectedIndex(textOrientationCombo,(label.getTexture().getAlignment().getOrientation().ordinal()));    
          textField.setText(label.getTexture().getText());
          leftField.setText(toUnitX(label.getTexture().getAnchorPoint().x ));
          topField.setText(toUnitY(label.getTexture().getAnchorPoint().y));
          heightField.setText(String.valueOf((label.getTexture().getSize() )));
          colorButton.setSelectedColor(label.getTexture().getFillColor());
          setSelectedIndex(styleCombo,((FontTexture)label.getTexture()).getStyle().ordinal());
        
          validateAlignmentComboText(textAlignmentCombo,label.getTexture());  
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        FontLabel label=(FontLabel)getTarget();        
        if(e.getSource()==textOrientationCombo){ 
                label.getTexture().setOrientation((Text.Orientation)textOrientationCombo.getSelectedItem());
                validateAlignmentComboText(textAlignmentCombo, label.getTexture());
        } 
        if(e.getSource()==textAlignmentCombo){;  
             label.getTexture().setAlignment(Text.Alignment.valueOf((String)textAlignmentCombo.getSelectedItem()));
        }
        if(e.getSource()==styleCombo){
            FontTexture text=(FontTexture)label.getTexture(); 
            text.setStyle((Text.Style)styleCombo.getSelectedItem());                                
        }  
        getComponent().getModel().getUnit().registerMemento( getTarget().getState(MementoType.MOVE_MEMENTO));
        getComponent().Repaint();
    }
    
    @Override
    public void keyReleased(KeyEvent e){
        if(e.getKeyCode()!=KeyEvent.VK_ENTER) return;
        FontLabel label=(FontLabel)getTarget();
          if(e.getSource()==textField&&textField.getText().length()>0){
            label.getTexture().setText(textField.getText());
          }
          
        if(e.getSource()==this.heightField){
           label.getTexture().setSize((Integer.parseInt(heightField.getText())));  
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
    

    @Override
    public void colorChanged(Color color) {
        FontLabel label=(FontLabel)getTarget(); 
        getComponent().getModel().getUnit().registerMemento(getTarget().getState(MementoType.MOVE_MEMENTO));
        label.getTexture().setFillColor(color);
    }    
}

