package com.mynetpcb.symbol.dialog.panel.inspector;

import com.mynetpcb.core.capi.event.UnitEvent;
import com.mynetpcb.core.capi.panel.AbstractPanelBuilder;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.tree.AttachedItem;
import com.mynetpcb.symbol.component.SymbolComponent;

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

public class SymbolPanelBuilder extends AbstractPanelBuilder<Shape>{
    
    private JTextField moduleNameField,widthField,heightField;
    
    private JComboBox textLayoutCombo,referenceCombo,valueCombo;
    
    JTextField originX,originY;
        
    public SymbolPanelBuilder(SymbolComponent component) {
        super(component,new GridLayout(9,1));  
        //***Module Name        
                panel=new JPanel(); panel.setLayout(new BorderLayout()); 
                label=new JLabel("Module name"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(140,label.getHeight())); panel.add(label,BorderLayout.WEST);
                moduleNameField=new JTextField(); moduleNameField.addKeyListener(this); panel.add(moduleNameField,BorderLayout.CENTER);
                layoutPanel.add(panel);                              
        //***Widht
                panel=new JPanel(); panel.setLayout(new BorderLayout()); 
                label=new JLabel("Width"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(140,label.getHeight())); panel.add(label,BorderLayout.WEST);
                widthField=new JTextField(""); widthField.addKeyListener(this); panel.add(widthField,BorderLayout.CENTER);
                layoutPanel.add(panel);        
         //***Height
                panel=new JPanel(); panel.setLayout(new BorderLayout()); 
                label=new JLabel("Height"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(140,label.getHeight())); panel.add(label,BorderLayout.WEST);
                heightField=new JTextField(""); heightField.addKeyListener(this); panel.add(heightField,BorderLayout.CENTER);
                layoutPanel.add(panel); 
        
        //reference
                panel=new JPanel(); panel.setLayout(new BorderLayout()); 
                label=new JLabel("Reference"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(140,label.getHeight())); panel.add(label,BorderLayout.WEST);
                referenceCombo=new JComboBox();referenceCombo.addActionListener(this);  panel.add(referenceCombo,BorderLayout.CENTER);                
                layoutPanel.add(panel);        
        
        //***value
                panel=new JPanel(); panel.setLayout(new BorderLayout()); 
                label=new JLabel("Value"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(140,label.getHeight())); panel.add(label,BorderLayout.WEST);
                valueCombo=new JComboBox();valueCombo.addActionListener(this);  panel.add(valueCombo,BorderLayout.CENTER);                
                layoutPanel.add(panel);        
        //***Text Layout visibility        
                panel=new JPanel(); panel.setLayout(new BorderLayout()); 
                label=new JLabel("Text Layout Visible"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(140,label.getHeight())); panel.add(label,BorderLayout.WEST);
                textLayoutCombo=new JComboBox(trueFalse);textLayoutCombo.addActionListener(this);  panel.add(textLayoutCombo,BorderLayout.CENTER);                
                layoutPanel.add(panel); 
        //***Coordinate X
                panel=new JPanel(); panel.setLayout(new BorderLayout()); 
                label=new JLabel("Origin X"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(140,label.getHeight())); panel.add(label,BorderLayout.WEST);
                originX=new JTextField(""); originX.addKeyListener(this); panel.add(originX,BorderLayout.CENTER);
                layoutPanel.add(panel);        
         //***Coordinate Y
                panel=new JPanel(); panel.setLayout(new BorderLayout()); 
                label=new JLabel("Origin Y"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(140,label.getHeight())); panel.add(label,BorderLayout.WEST);
                originY=new JTextField(""); originY.addKeyListener(this); panel.add(originY,BorderLayout.CENTER);
                layoutPanel.add(panel); 
    }

    @Override
    public void updateUI() {
        if(getComponent().getModel().getUnit()==null){
          return;
        }
        widthField.setText(String.valueOf(( getComponent().getModel().getUnit().getWidth())));
        
        heightField.setText(String.valueOf(( getComponent().getModel().getUnit().getHeight()))); 
        
        moduleNameField.setText(getComponent().getModel().getUnit().getUnitName()); 
        
        referenceCombo.removeActionListener(this);
        referenceCombo.removeAllItems();
        referenceCombo.addItem(new AttachedItem.Builder("").setUUID(null).build());
        
        //Texture atext = SymbolMgr.getInstance().getTextureByTag(getComponent().getModel().getUnit(),"reference");
        //***get could be Owners list 
//        for(Shape symbol:((SymbolComponent)getComponent()).getModel().getUnit().<Shape>getShapes(FontLabel.class)){
//             Texture text=((FontLabel)symbol).getTexture();     
//             AttachedItem item=new AttachedItem.Builder(text.getText()).setUUID(symbol.getUUID()).build();  
//             referenceCombo.addItem(item);                          
//             if(text.getTag().equals("reference"))
//                 referenceCombo.setSelectedItem(item);  
//        }   
        referenceCombo.addActionListener(this);
                      

        
        valueCombo.removeActionListener(this);
        valueCombo.removeAllItems();
        valueCombo.addItem(new AttachedItem.Builder("").setUUID(null).build());
        
        //atext = SymbolMgr.getInstance().getTextureByTag(getComponent().getModel().getUnit(),"unit");
        //***get could be Owners list        
//        for(FontLabel symbol:((SymbolComponent)getComponent()).getModel().getUnit().<FontLabel>getShapes(FontLabel.class)){
//             Texture text=symbol.getTexture();     
//             AttachedItem item=new AttachedItem.Builder(text.getText()).setUUID(symbol.getUUID()).build();   
//             valueCombo.addItem(item);  
//             if(text.getTag().equals("unit"))
//                 valueCombo.setSelectedItem(item);    
//        }
        
        //***reconnect
        valueCombo.addActionListener(this);
        
        textLayoutCombo.removeActionListener(this);
        textLayoutCombo.setSelectedIndex((((SymbolComponent)getComponent()).getModel().getUnit().getTextLayoutVisibility()?1:0));        
        textLayoutCombo.addActionListener(this); 
        
        if(getComponent().getModel().getUnit().getCoordinateSystem()!=null){
          originX.setText(String.valueOf((getComponent().getModel().getUnit().getCoordinateSystem().getOrigin().x)));
          originY.setText(String.valueOf((getComponent().getModel().getUnit().getCoordinateSystem().getOrigin().y)));
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
//        if(e.getSource()==referenceCombo){
//           Shape element=getComponent().getModel().getUnit().getShape(((AttachedItem)referenceCombo.getSelectedItem()).getUUID());
//           Label text = (Label)SymbolMgr.getInstance().getLabelByTag(((SymbolComponent)getComponent()).getModel().getUnit(),"reference");
//           //***demark the old one
//           if(text!=null)
//              text.getTexture().setTag("label");  
//           //***mark the new one 
//           if(element!=null){
//               text=((Label)element);
//               text.getTexture().setTag("reference");
//           }           
//        }
//        
//        if(e.getSource()==valueCombo){
//           Shape element=getComponent().getModel().getUnit().getShape(((AttachedItem)valueCombo.getSelectedItem()).getUUID());
//           Label text = (Label)SymbolMgr.getInstance().getLabelByTag(((SymbolComponent)getComponent()).getModel().getUnit(),"unit");
//           //***demark the old one
//           if(text!=null)
//              text.getTexture().setTag("label");  
//           //***mark the new one 
//           if(element!=null){
//               text=(Label)element;
//               text.getTexture().setTag("unit");
//           }           
//        }
        
        
        if(e.getSource()==textLayoutCombo){
           ((SymbolComponent)getComponent()).getModel().getUnit().setTextLayoutVisibility((textLayoutCombo.getSelectedIndex()==0?false:true));       
         
        }
        getComponent().Repaint();
    }
    
    public void keyReleased(KeyEvent e) {
        if(e.getKeyCode()!=KeyEvent.VK_ENTER) return;
        
        if(e.getSource()==this.moduleNameField){
           getComponent().getModel().getUnit().setUnitName(moduleNameField.getText());  
           getComponent().getModel().fireUnitEvent(new UnitEvent(getComponent().getModel().getUnit(), UnitEvent.RENAME_UNIT));
        }
        if(e.getSource()==this.heightField||e.getSource()==this.widthField){            
            getComponent().getModel().getUnit().setSize((Integer.parseInt(widthField.getText())),(Integer.parseInt(heightField.getText())));
            //***refresh scrollbars
            getComponent().componentResized(null);                
            getComponent().Repaint();
        }
        if(e.getSource()==originX||e.getSource()==originY){
            getComponent().getModel().getUnit().getCoordinateSystem().reset((Double.parseDouble(originX.getText())),(Double.parseDouble(originY.getText())));   
            getComponent().Repaint();
        }
        

    }
}

