package com.mynetpcb.pad.dialog.panel.inspector;


import com.mynetpcb.core.capi.Grid;
import com.mynetpcb.core.capi.event.UnitEvent;
import com.mynetpcb.core.capi.panel.AbstractPanelBuilder;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.text.Texture;
import com.mynetpcb.core.capi.tree.AttachedItem;
import com.mynetpcb.pad.component.FootprintComponent;
import com.mynetpcb.pad.shape.GlyphLabel;
import com.mynetpcb.pad.unit.Footprint;
import com.mynetpcb.pad.unit.FootprintMgr;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

import java.util.Collection;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;


public class FootprintPanelBuilder extends AbstractPanelBuilder<Shape>{
    
    private JTextField moduleNameField,widthField,heightField;
    
    private JComboBox unitsCombo,gridCombo,referenceCombo,valueCombo;
    
    
        
    public FootprintPanelBuilder(FootprintComponent component) {
        super(component,new GridLayout(9,1));  
        //***Module Name        
                panel=new JPanel(); panel.setLayout(new BorderLayout()); 
                label=new JLabel("Footprint name"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(114,label.getHeight())); panel.add(label,BorderLayout.WEST);
                moduleNameField=new JTextField(); moduleNameField.addKeyListener(this); panel.add(moduleNameField,BorderLayout.CENTER);
                layoutPanel.add(panel);                              
        //***Widht
                panel=new JPanel(); panel.setLayout(new BorderLayout()); 
                label=new JLabel("Width"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(114,label.getHeight())); panel.add(label,BorderLayout.WEST);
                widthField=new JTextField(""); widthField.addKeyListener(this); panel.add(widthField,BorderLayout.CENTER);
                layoutPanel.add(panel);        
         //***Height
                panel=new JPanel(); panel.setLayout(new BorderLayout()); 
                label=new JLabel("Height"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(114,label.getHeight())); panel.add(label,BorderLayout.WEST);
                heightField=new JTextField(""); heightField.addKeyListener(this); panel.add(heightField,BorderLayout.CENTER);
                layoutPanel.add(panel); 
        //***units
        panel=new JPanel(); panel.setLayout(new BorderLayout()); 
        label=new JLabel("Units"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(114,label.getHeight())); panel.add(label,BorderLayout.WEST);
        unitsCombo=new JComboBox(Grid.Units.values());unitsCombo.addActionListener(this);  panel.add(unitsCombo,BorderLayout.CENTER);                
        unitsCombo.setEnabled(false);
        layoutPanel.add(panel);
        //***grid
        panel=new JPanel(); panel.setLayout(new BorderLayout()); 
        label=new JLabel("Grid Raster"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(114,label.getHeight())); panel.add(label,BorderLayout.WEST);
        gridCombo=new JComboBox(new Double[]{2.54,1.27,0.635,0.508,0.254,0.127,0.0635,0.0508,0.0254,0.0127,5.0,2.5,1.0,0.5,0.25,0.8,0.2,0.1,0.05,0.025,0.01});gridCombo.addActionListener(this);  panel.add(gridCombo,BorderLayout.CENTER);                
        layoutPanel.add(panel);
        
        //reference
        panel=new JPanel(); panel.setLayout(new BorderLayout()); 
        label=new JLabel("Reference"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(114,label.getHeight())); panel.add(label,BorderLayout.WEST);
        referenceCombo=new JComboBox();referenceCombo.addActionListener(this);  panel.add(referenceCombo,BorderLayout.CENTER);                
        layoutPanel.add(panel);        
        //***value
        panel=new JPanel(); panel.setLayout(new BorderLayout()); 
        label=new JLabel("Value"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(114,label.getHeight())); panel.add(label,BorderLayout.WEST);
        valueCombo=new JComboBox();valueCombo.addActionListener(this);  panel.add(valueCombo,BorderLayout.CENTER);                
        layoutPanel.add(panel);        

        //***Coordinate X
                panel=new JPanel(); panel.setLayout(new BorderLayout()); 
                label=new JLabel("Origin X"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(114,label.getHeight())); panel.add(label,BorderLayout.WEST);
                originX=new JTextField(""); originX.addKeyListener(this); panel.add(originX,BorderLayout.CENTER);
                layoutPanel.add(panel);        
         //***Coordinate Y
                panel=new JPanel(); panel.setLayout(new BorderLayout()); 
                label=new JLabel("Origin Y"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(114,label.getHeight())); panel.add(label,BorderLayout.WEST);
                originY=new JTextField(""); originY.addKeyListener(this); panel.add(originY,BorderLayout.CENTER);
                layoutPanel.add(panel); 
    }

    @Override
    public void updateUI() {
        if(getComponent().getModel().getUnit()==null){
          return;
        }
        widthField.setText(String.valueOf(Grid.COORD_TO_MM( getComponent().getModel().getUnit().getWidth())));
        
        heightField.setText(String.valueOf(Grid.COORD_TO_MM( getComponent().getModel().getUnit().getHeight()))); 
        
        moduleNameField.setText(getComponent().getModel().getUnit().getUnitName()); 
        
        referenceCombo.removeActionListener(this);
        referenceCombo.removeAllItems();
        referenceCombo.addItem(new AttachedItem.Builder("").setUUID(null).build());
        
        //Texture atext = FootprintMgr.getInstance().getTextureByTag(getComponent().getModel().getUnit(),"reference");
        //***get could be Owners list 
        Collection<GlyphLabel> labels=getComponent().getModel().getUnit().getShapes(GlyphLabel.class);
        for(GlyphLabel symbol:labels){
             Texture text=(symbol).getTexture();     
             AttachedItem item=new AttachedItem.Builder(text.getText()).setUUID(symbol.getUUID()).build();  
             referenceCombo.addItem(item);                          
             if(text.getTag().equals("reference"))
                 referenceCombo.setSelectedItem(item);  
        }   
        referenceCombo.addActionListener(this);
        
        valueCombo.removeActionListener(this);
        valueCombo.removeAllItems();
        valueCombo.addItem(new AttachedItem.Builder("").setUUID(null).build());
        //atext = FootprintMgr.getInstance().getTextureByTag(getComponent().getModel().getUnit(),"value");
        //***get could be Owners list        
        for(GlyphLabel symbol:labels){
             Texture text=symbol.getTexture();   
             AttachedItem item=new AttachedItem.Builder(text.getText()).setUUID(symbol.getUUID()).build();   
             valueCombo.addItem(item);  
             if(text.getTag().equals("value"))
                 valueCombo.setSelectedItem(item);    
        }
        
        //***reconnect
        valueCombo.addActionListener(this);

        setSelectedItem(gridCombo,getComponent().getModel().getUnit().getGrid().getGridValue());
        if(getComponent().getModel().getUnit().getCoordinateSystem()!=null){
          originX.setText(String.valueOf(Grid.COORD_TO_MM(getComponent().getModel().getUnit().getCoordinateSystem().getOrigin().x)));
          originY.setText(String.valueOf(Grid.COORD_TO_MM(getComponent().getModel().getUnit().getCoordinateSystem().getOrigin().y)));
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource()==gridCombo){
            getComponent().getModel().getUnit().getGrid().setGridValue((Double)gridCombo.getSelectedItem());
        }
        if(e.getSource()==referenceCombo){
           Shape element=getComponent().getModel().getUnit().getShape(((AttachedItem)referenceCombo.getSelectedItem()).getUUID());
           GlyphLabel text = (GlyphLabel)FootprintMgr.getInstance().getLabelByTag((Footprint)getComponent().getModel().getUnit(),"reference");
           //***demark the old one
           if(text!=null)
              text.getTexture().setTag("label");  
           //***mark the new one 
           if(element!=null){
               text=((GlyphLabel)element);
               text.getTexture().setTag("reference");
           }           
        }
        
        if(e.getSource()==valueCombo){
           Shape element=getComponent().getModel().getUnit().getShape(((AttachedItem)valueCombo.getSelectedItem()).getUUID());
           GlyphLabel text = (GlyphLabel)FootprintMgr.getInstance().getLabelByTag((Footprint)getComponent().getModel().getUnit(),"value");
           //***demark the old one
           if(text!=null)
              text.getTexture().setTag("label");  
           //***mark the new one 
           if(element!=null){
               text=((GlyphLabel)element);
               text.getTexture().setTag("value");
           }           
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
            getComponent().getModel().getUnit().setSize((int)Grid.MM_TO_COORD(Double.parseDouble(widthField.getText())),(int)Grid.MM_TO_COORD(Double.parseDouble(heightField.getText())));
            //***refresh scrollbars
            getComponent().componentResized(null);                
            getComponent().Repaint();
        }
        if(e.getSource()==originX||e.getSource()==originY){
            getComponent().getModel().getUnit().getCoordinateSystem().reset(Grid.MM_TO_COORD(Double.parseDouble(originX.getText())),Grid.MM_TO_COORD(Double.parseDouble(originY.getText())));   
            getComponent().Repaint();
        }
        

    }
}
