package com.mynetpcb.symbol.dialog.panel.inspector;


import com.mynetpcb.core.capi.Pinable;
import com.mynetpcb.core.capi.panel.AbstractPanelBuilder;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.text.Text;
import com.mynetpcb.core.capi.text.Texture;
import com.mynetpcb.core.capi.undo.MementoType;
import com.mynetpcb.symbol.component.SymbolComponent;
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


public class PinPanelBuilder extends AbstractPanelBuilder<Shape> {

    private JTextField pinNumberField,pinNameField;
    
    private JComboBox pinTypeCombo,orientationCombo,styleCombo,textPinNameOrientationCombo,textPinNameAlignmentCombo,textPinNumberOrientationCombo,textPinNumberAlignmentCombo; 
    
    public PinPanelBuilder(SymbolComponent component) {
        super(component,new GridLayout(9,1));    
        //*****CREATE PIN  PANEL                   
           panel=new JPanel();panel.setLayout(new BorderLayout()); 
           label=new JLabel("Pin Type"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(114,label.getHeight())); panel.add(label,BorderLayout.WEST);
           pinTypeCombo=new JComboBox(com.mynetpcb.symbol.shape.Pin.Type.values()); pinTypeCombo.addActionListener(this); panel.add(pinTypeCombo,BorderLayout.CENTER);
           layoutPanel.add(panel);
        
           panel=new JPanel();panel.setLayout(new BorderLayout()); 
           label=new JLabel("Orientation"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(114,label.getHeight())); panel.add(label,BorderLayout.WEST);
           orientationCombo=new JComboBox(Pinable.Orientation.values()); orientationCombo.addActionListener(this); panel.add(orientationCombo,BorderLayout.CENTER);
           layoutPanel.add(panel);

           panel=new JPanel();panel.setLayout(new BorderLayout()); ;
           label=new JLabel("Style"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(114,label.getHeight())); panel.add(label,BorderLayout.WEST);
           styleCombo=new JComboBox(Pin.Style.values());styleCombo.addActionListener(this); panel.add(styleCombo,BorderLayout.CENTER);
           layoutPanel.add(panel);          
        
           panel=new JPanel();panel.setLayout(new BorderLayout()); ;
           label=new JLabel("Pin name"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(114,label.getHeight())); panel.add(label,BorderLayout.WEST);
           pinNameField=new JTextField(""); pinNameField.addKeyListener(this); panel.add(pinNameField,BorderLayout.CENTER);
           layoutPanel.add(panel);

           panel=new JPanel(); panel.setLayout(new BorderLayout()); 
           label=new JLabel("Text Alignment"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(114,label.getHeight())); panel.add(label,BorderLayout.WEST);
           textPinNameAlignmentCombo=new JComboBox();textPinNameAlignmentCombo.addActionListener(this);  panel.add(textPinNameAlignmentCombo,BorderLayout.CENTER);
           layoutPanel.add(panel);
        
           panel=new JPanel(); panel.setLayout(new BorderLayout()); 
           label=new JLabel("Text Orientation"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(114,label.getHeight())); panel.add(label,BorderLayout.WEST);
           textPinNameOrientationCombo=new JComboBox(Text.Orientation.values());textPinNameOrientationCombo.addActionListener(this);  panel.add(textPinNameOrientationCombo,BorderLayout.CENTER);
           layoutPanel.add(panel);

           panel=new JPanel();panel.setLayout(new BorderLayout()); ;
           label=new JLabel("Pin number"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(114,label.getHeight())); panel.add(label,BorderLayout.WEST);
           pinNumberField=new JTextField(""); pinNumberField.addKeyListener(this); panel.add(pinNumberField,BorderLayout.CENTER);
           layoutPanel.add(panel);
           
           panel=new JPanel(); panel.setLayout(new BorderLayout()); 
           label=new JLabel("Text Alignment"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(114,label.getHeight())); panel.add(label,BorderLayout.WEST);
           textPinNumberAlignmentCombo=new JComboBox();textPinNumberAlignmentCombo.addActionListener(this);  panel.add(textPinNumberAlignmentCombo,BorderLayout.CENTER);
           layoutPanel.add(panel);
        
           panel=new JPanel(); panel.setLayout(new BorderLayout()); 
           label=new JLabel("Text Orientation"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(114,label.getHeight())); panel.add(label,BorderLayout.WEST);
           textPinNumberOrientationCombo=new JComboBox(Text.Orientation.values());textPinNumberOrientationCombo.addActionListener(this);  panel.add(textPinNumberOrientationCombo,BorderLayout.CENTER);
           layoutPanel.add(panel);
        
    }

    public void actionPerformed(ActionEvent e) {
        Pin pin=(Pin) getTarget();
        if(e.getSource()==pinTypeCombo){    
            pin.setType(com.mynetpcb.symbol.shape.Pin.Type.values()[pinTypeCombo.getSelectedIndex()]);   
            this.updateUI();
        }    
        if(e.getSource()==textPinNameOrientationCombo){                                 
            pin.getChipText().getTextureByTag("pinname").setOrientation((pin.getChipText().getTextureByTag("pinname").getAlignment().getOrientation() == Text.Orientation.HORIZONTAL ? Text.Orientation.VERTICAL : Text.Orientation.HORIZONTAL));
            validateAlignmentComboText(textPinNameAlignmentCombo,pin.getChipText().getTextureByTag("pinname"));                   
        }    
        if(e.getSource()==textPinNumberOrientationCombo){
            pin.getChipText().getTextureByTag("pinnumber").setOrientation((pin.getChipText().getTextureByTag("pinnumber").getAlignment().getOrientation() == Text.Orientation.HORIZONTAL ? Text.Orientation.VERTICAL : Text.Orientation.HORIZONTAL));
            validateAlignmentComboText(textPinNumberAlignmentCombo,pin.getChipText().getTextureByTag("pinnumber"));
        }
        
        if(e.getSource()==textPinNameAlignmentCombo){
            Texture text=pin.getChipText().getTextureByTag("pinname");  
            text.setAlignment(Text.Alignment.valueOf((String)textPinNameAlignmentCombo.getSelectedItem()));
        }
        if(e.getSource()==textPinNumberAlignmentCombo){
            Texture text=pin.getChipText().getTextureByTag("pinnumber");  
            text.setAlignment(Text.Alignment.valueOf((String)textPinNumberAlignmentCombo.getSelectedItem()));  
        }        
        if(e.getSource()==orientationCombo){
          pin.setOrientation((Pinable.Orientation)orientationCombo.getSelectedItem()); 
          
        }

        if(e.getSource()==styleCombo){
           pin.setStyle((Pin.Style)styleCombo.getSelectedItem()); 
        }
        getComponent().getModel().getUnit().registerMemento( getTarget().getState(MementoType.MOVE_MEMENTO));

        //***is it comming from User interaction? or programm?
        //if(e.getModifiers()!=0){
           getComponent().Repaint();    
        //}   
    }
    
  

    @Override
    public void keyPressed(KeyEvent e) 
    {
        //***is this ENTER
        if(e.getKeyCode()!=KeyEvent.VK_ENTER) return;
        Pin pin=(Pin) getTarget();
        if (e.getSource() == this.pinNumberField)
            pin.getChipText().getTextureByTag("pinnumber").setText(pinNumberField.getText());
        
        if (e.getSource() == this.pinNameField)
            pin.getChipText().getTextureByTag("pinname").setText(pinNameField.getText());
        //***is it comming from User interaction? or programm?
           getComponent().Repaint();
        //***register with undo manager   
           getComponent().getModel().getUnit().registerMemento( getTarget().getState(MementoType.MOVE_MEMENTO));
    }

    public void updateUI() {
        Pin pin=(Pin) getTarget();

        //***disconnect from listener
        this.pinTypeCombo.removeActionListener(this);
        this.pinTypeCombo.setSelectedIndex(pin.getType().ordinal());
        //***reconnect
        this.pinTypeCombo.addActionListener(this);        

        setSelectedItem(orientationCombo,pin.getOrientation());
        
        this.pinNumberField.setText(pin.getChipText().getTextureByTag("pinnumber").getText());
        
        this.pinNameField.setText(pin.getChipText().getTextureByTag("pinname").getText());  
        
        setSelectedIndex(textPinNumberOrientationCombo,(pin.getChipText().getTextureByTag("pinnumber").getAlignment().getOrientation().ordinal()));
        
        validateAlignmentComboText(textPinNumberAlignmentCombo,pin.getChipText().getTextureByTag("pinnumber"));    

        if(pin.getType() == com.mynetpcb.symbol.shape.Pin.Type.COMPLEX){
            enableControls(true);

            setSelectedIndex(textPinNameOrientationCombo,(pin.getChipText().getTextureByTag("pinname").getAlignment().getOrientation().ordinal()));
        
            validateAlignmentComboText(textPinNameAlignmentCombo,pin.getChipText().getTextureByTag("pinname"));       
        
            setSelectedItem(styleCombo,pin.getStyle());
            
            //this.pinNameField.setText(pin.getChipText().getTextureByTag("pinname").getText());      
        }else{
          enableControls(false);  
        }
    }
    
    private void enableControls(boolean enable){
        //this.pinNameField.setEnabled(enable);  
        this.textPinNameOrientationCombo.setEnabled(enable);
        this.textPinNameAlignmentCombo.setEnabled(enable);  
        //this.pinNumberField.setEnabled(enable);  
        this.textPinNumberOrientationCombo.setEnabled(enable);
        this.textPinNumberAlignmentCombo.setEnabled(enable);  
        this.styleCombo.setEnabled(enable);     

    }
    
}

