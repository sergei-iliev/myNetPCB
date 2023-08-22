package com.mynetpcb.symbol.dialog.panel.inspector;

import com.mynetpcb.core.capi.Grid;
import com.mynetpcb.core.capi.panel.AbstractPanelBuilder;
import com.mynetpcb.core.capi.pin.Pinable;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.text.Texture;
import com.mynetpcb.core.capi.text.font.SymbolFontTexture;
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

public class PinPanelBuilder  extends  AbstractPanelBuilder<Shape> { 
    private JComboBox pinTypeCombo,orientationCombo,nameOrientationCombo,numberOrientationCombo;
    private JTextField numberField;
    private JTextField numberX,numberY,nameX,nameY;
    public PinPanelBuilder(SymbolComponent component) {
        super(component,new GridLayout(13,1));
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
        
        panel=new JPanel();panel.setLayout(new BorderLayout()); 
        label=new JLabel("Pin Type"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(114,label.getHeight())); panel.add(label,BorderLayout.WEST);
        pinTypeCombo=new JComboBox(Pin.PinType.values()); pinTypeCombo.addActionListener(this); panel.add(pinTypeCombo,BorderLayout.CENTER);
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
        label=new JLabel("Name"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(114,label.getHeight())); panel.add(label,BorderLayout.WEST);
        nameField=new JTextField(""); nameField.addKeyListener(this); panel.add(nameField,BorderLayout.CENTER);
        layoutPanel.add(panel);
        
        panel=new JPanel(); panel.setLayout(new BorderLayout()); 
        label=new JLabel("Alignment"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(114,label.getHeight())); panel.add(label,BorderLayout.WEST);
        nameOrientationCombo=new JComboBox(Texture.Alignment.values());nameOrientationCombo.addActionListener(this);  panel.add(nameOrientationCombo,BorderLayout.CENTER);
        layoutPanel.add(panel);
        //***Left name
        panel = new JPanel();
        panel.setLayout(new BorderLayout());
        label = new JLabel("X");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setPreferredSize(new Dimension(114, label.getHeight()));
        panel.add(label, BorderLayout.WEST);
        nameX = new JTextField("0");
        nameX.addKeyListener(this);
        panel.add(nameX, BorderLayout.CENTER);
        layoutPanel.add(panel);

        //***Top name
        panel = new JPanel();
        panel.setLayout(new BorderLayout());
        label = new JLabel("Y");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setPreferredSize(new Dimension(114, label.getHeight()));
        panel.add(label, BorderLayout.WEST);
        nameY = new JTextField("0");
        nameY.addKeyListener(this);
        panel.add(nameY, BorderLayout.CENTER);
        layoutPanel.add(panel);        
        
        panel=new JPanel();panel.setLayout(new BorderLayout()); ;
        label=new JLabel("Number"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(114,label.getHeight())); panel.add(label,BorderLayout.WEST);
        numberField=new JTextField(""); numberField.addKeyListener(this); panel.add(numberField,BorderLayout.CENTER);
        layoutPanel.add(panel);
        
        panel=new JPanel(); panel.setLayout(new BorderLayout()); 
        label=new JLabel("Alignment"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(114,label.getHeight())); panel.add(label,BorderLayout.WEST);
        numberOrientationCombo=new JComboBox(Texture.Alignment.values());numberOrientationCombo.addActionListener(this);  panel.add(numberOrientationCombo,BorderLayout.CENTER);
        layoutPanel.add(panel);
        //***Left number
        panel = new JPanel();
        panel.setLayout(new BorderLayout());
        label = new JLabel("X");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setPreferredSize(new Dimension(114, label.getHeight()));
        panel.add(label, BorderLayout.WEST);
        numberX = new JTextField("0");
        numberX.addKeyListener(this);
        panel.add(numberX, BorderLayout.CENTER);
        layoutPanel.add(panel);

        //***Top number
        panel = new JPanel();
        panel.setLayout(new BorderLayout());
        label = new JLabel("Y");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setPreferredSize(new Dimension(114, label.getHeight()));
        panel.add(label, BorderLayout.WEST);
        numberY = new JTextField("0");
        numberY.addKeyListener(this);
        panel.add(numberY, BorderLayout.CENTER);
        layoutPanel.add(panel);  
    }

    @Override
    public void updateUI() {
        Pin pin=(Pin) getTarget();
        
        setSelectedItem(pinTypeCombo,pin.getPinType());
        setSelectedItem(orientationCombo,pin.getOrientation());
        setSelectedItem(styleCombo,pin.getStyle());
        nameField.setText(pin.getTextureByTag("name").getText());
        numberField.setText(pin.getTextureByTag("number").getText());
        
        setSelectedItem(nameOrientationCombo,((SymbolFontTexture)pin.getTextureByTag("name")).getAlignment());
        setSelectedItem(numberOrientationCombo,((SymbolFontTexture)pin.getTextureByTag("number")).getAlignment());
        
        leftField.setText(toUnitX(pin.getPinDrawing().pe.x,1));
        topField.setText(toUnitY(pin.getPinDrawing().pe.y,1));
        
        numberX.setText(toUnitX(pin.getTextureByTag("number").getAnchorPoint().x,1));
        numberY.setText(toUnitY(pin.getTextureByTag("number").getAnchorPoint().y,1));
        
        nameX.setText(toUnitX(pin.getTextureByTag("name").getAnchorPoint().x,1));
        nameY.setText(toUnitY(pin.getTextureByTag("name").getAnchorPoint().y,1));
        
        enableControls(pin.getPinType() == Pin.PinType.COMPLEX);
        
    }
    @Override
    public void actionPerformed(ActionEvent e) {        
        Pin pin=(Pin) getTarget();
        if(e.getSource()==pinTypeCombo){    
            pin.setPinType(Pin.PinType.values()[pinTypeCombo.getSelectedIndex()]);   
            this.updateUI();
        }    
        if(e.getSource()==nameOrientationCombo){                                 
           ((SymbolFontTexture)pin.getTextureByTag("name")).setAlignment((Texture.Alignment)nameOrientationCombo.getSelectedItem());           
        }    
        if(e.getSource()==numberOrientationCombo){
            ((SymbolFontTexture)pin.getTextureByTag("number")).setAlignment((Texture.Alignment)numberOrientationCombo.getSelectedItem());
        }
     
        if(e.getSource()==orientationCombo){
          pin.setOrientation((Pinable.Orientation)orientationCombo.getSelectedItem());           
        }

        if(e.getSource()==styleCombo){
           pin.setStyle ((Pin.Style)styleCombo.getSelectedItem()); 
        }
        getComponent().getModel().getUnit().registerMemento( getTarget().getState(MementoType.MOVE_MEMENTO));
        getComponent().Repaint();    
          
    }
    
    

    @Override
    public void keyPressed(KeyEvent e) 
    {
        //***is this ENTER
        if(e.getKeyCode()!=KeyEvent.VK_ENTER) return;
        Pin pin=(Pin) getTarget();
        if(e.getSource()==this.leftField){           
           var x=fromUnitX(leftField.getText());
           pin.move(x-pin.getPinDrawing().pe.x, 0);
           pin.alignToGrid(true);
        }
         
        if(e.getSource()==this.topField){
           var y=fromUnitY(topField.getText());
           pin.move(0,y-pin.getPinDrawing().pe.y);
           pin.alignToGrid(true);
        }
        
        if (e.getSource() == this.nameField){
            pin.getTextureByTag("name").setText(nameField.getText());
        }
        if (e.getSource() == this.numberField) {
            pin.getTextureByTag("number").setText(numberField.getText());
        }
        
        
        if(e.getSource()==this.numberX){
            pin.getTextureByTag("number").getAnchorPoint().x= fromUnitX(numberX.getText());
         }
         if(e.getSource()==this.numberY){
            pin.getTextureByTag("number").getAnchorPoint().y= fromUnitY(numberY.getText());
         }
         
        
         if(e.getSource()==this.nameX){
            pin.getTextureByTag("name").getAnchorPoint().x= fromUnitX(nameX.getText());
         }
         if(e.getSource()==this.nameY){
            pin.getTextureByTag("name").getAnchorPoint().y= fromUnitY(nameY.getText());
         }        
        //***register with undo manager   
        getComponent().getModel().getUnit().registerMemento( getTarget().getState(MementoType.MOVE_MEMENTO));
           
        getComponent().Repaint();           
    }    
    private void enableControls(boolean enable){        
        this.nameOrientationCombo.setEnabled(enable);                
        this.numberOrientationCombo.setEnabled(enable);        
        this.styleCombo.setEnabled(enable);     

    }    
}
