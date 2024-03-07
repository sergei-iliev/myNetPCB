package com.mynetpcb.board.dialog.panel.inspector;

import com.mynetpcb.board.component.BoardComponent;
import com.mynetpcb.board.shape.PCBFootprint;
import com.mynetpcb.core.capi.Grid;
import com.mynetpcb.core.capi.layer.Layer;
import com.mynetpcb.core.capi.panel.AbstractPanelBuilder;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.text.Texture;
import com.mynetpcb.core.capi.undo.MementoType;
import com.mynetpcb.pad.shape.Pad;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.lang.ref.WeakReference;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;

public class FootprintPanelBuilder  extends AbstractPanelBuilder<Shape> {

    private JTextField  referenceField, valueField;
    private JPadPanel padPanel;
    
    public FootprintPanelBuilder(BoardComponent component) {
        super(component,new GridBagLayout());        
        GridBagConstraints constraints = new GridBagConstraints();
        
        panel=new JPanel(); panel.setLayout(new BorderLayout()); 
        label=new JLabel("Side"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(100,label.getHeight())); panel.add(label,BorderLayout.WEST);
        layerCombo=new JComboBox(Layer.Side.values());layerCombo.addActionListener(this);  panel.add(layerCombo,BorderLayout.CENTER);                
        constraints.gridx = 0;  
        constraints.gridy = 0; 
        constraints.weightx = 1;
        constraints.ipady=4;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridwidth = 1;        
        layoutPanel.add(panel,constraints);
        
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
        constraints.gridy = 1;   
        layoutPanel.add(panel,constraints);
        
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
        constraints.gridy = 2;           
        layoutPanel.add(panel,constraints);


        
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
        constraints.gridy = 3;           
        layoutPanel.add(panel,constraints);

        //rotate
        panel=new JPanel(); panel.setLayout(new BorderLayout());
        label=new JLabel("Rotate"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(100,label.getHeight())); panel.add(label,BorderLayout.WEST);
        rotateField=new JTextField(); rotateField.addKeyListener(this); panel.add(rotateField,BorderLayout.CENTER);
        constraints.gridy = 4;           
        layoutPanel.add(panel,constraints);
        
        padPanel=new JPadPanel(component);        
        constraints.gridy = 5;           
        layoutPanel.add(padPanel.getLayoutPanel(),constraints);
   
    }

    @Override
    public void updateUI() {
        PCBFootprint symbol = (PCBFootprint)getTarget();
        padPanel.setTarget(symbol);
        padPanel._updateUI(symbol.getSelectedPad());
        
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
    
    private static class JPadPanel extends AbstractPanelBuilder<Shape>{
      private WeakReference<Pad> padRef;	
      private JTextField solderMaskExpansion,padNumber,padNetName,drillWidth;
      private JComboBox  padTypeCombo,padShapeCombo,platedCombo;
      
      public JPadPanel(BoardComponent component) {    	 
          super(component,new GridLayout(11,1));
    	  layoutPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
          
          
          var panel=new JPanel(); panel.setLayout(new BorderLayout()); 
          var label=new JLabel("Layer"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(100,label.getHeight())); panel.add(label,BorderLayout.WEST);
          layerCombo=new JComboBox(Layer.PAD_LAYERS);layerCombo.addActionListener(this); panel.add(layerCombo,BorderLayout.CENTER);                
          layoutPanel.add(panel);
               
          //****size x
          panel=new JPanel(); panel.setLayout(new BorderLayout()); 
          label=new JLabel("Width"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(100,label.getHeight())); panel.add(label,BorderLayout.WEST);
          widthField=new JTextField("");widthField.addKeyListener(this); panel.add(widthField,BorderLayout.CENTER);
          layoutPanel.add(panel);  
          //****size y
          panel=new JPanel(); panel.setLayout(new BorderLayout()); 
          label=new JLabel("Height"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(100,label.getHeight())); panel.add(label,BorderLayout.WEST);
          heightField=new JTextField(""); heightField.addKeyListener(this); panel.add(heightField,BorderLayout.CENTER);
          layoutPanel.add(panel);
          
          //rotate
          panel=new JPanel(); panel.setLayout(new BorderLayout());
          label=new JLabel("Rotate"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(100,label.getHeight())); panel.add(label,BorderLayout.WEST);
          rotateField=new JTextField("0");rotateField.addKeyListener(this);  panel.add(rotateField,BorderLayout.CENTER);
          layoutPanel.add(panel); 
          
          //pad type
          panel=new JPanel(); panel.setLayout(new BorderLayout()); 
          label=new JLabel("Pad Type"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(100,label.getHeight())); panel.add(label,BorderLayout.WEST);
          padTypeCombo=new JComboBox(com.mynetpcb.pad.shape.Pad.Type.values());padTypeCombo.addActionListener(this);padTypeCombo.setPreferredSize(new Dimension(90,padTypeCombo.getHeight())); ;  panel.add(padTypeCombo,BorderLayout.CENTER);                
          layoutPanel.add(panel);
          //pad shape
          panel=new JPanel(); panel.setLayout(new BorderLayout()); 
          label=new JLabel("Pad Shape"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(100,label.getHeight())); panel.add(label,BorderLayout.WEST);
          padShapeCombo=new JComboBox(com.mynetpcb.pad.shape.Pad.Shape.values());padShapeCombo.addActionListener(this);  panel.add(padShapeCombo,BorderLayout.CENTER);                
          layoutPanel.add(panel); 
          
          panel=new JPanel(); panel.setLayout(new BorderLayout()); 
          label=new JLabel("Plated"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(100,label.getHeight())); panel.add(label,BorderLayout.WEST);
          platedCombo=new JComboBox(new Boolean[] {Boolean.TRUE,Boolean.FALSE});platedCombo.addActionListener(this);  panel.add(platedCombo,BorderLayout.CENTER);                
          layoutPanel.add(panel); 

          panel=new JPanel(); panel.setLayout(new BorderLayout()); 
          label=new JLabel("Solder Mask"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(100,label.getHeight())); panel.add(label,BorderLayout.WEST);
          solderMaskExpansion=new JTextField("");  solderMaskExpansion.addKeyListener(this); panel.add(solderMaskExpansion,BorderLayout.CENTER);
          layoutPanel.add(panel);
          
          panel=new JPanel(); panel.setLayout(new BorderLayout()); 
          label=new JLabel("Drill Width"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(100,label.getHeight())); panel.add(label,BorderLayout.WEST);
          drillWidth=new JTextField(""); drillWidth.addKeyListener(this);panel.add(drillWidth,BorderLayout.CENTER);
          layoutPanel.add(panel); 
          
          panel=new JPanel(); panel.setLayout(new BorderLayout()); 
          label=new JLabel("Pad Number"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(100,label.getHeight())); panel.add(label,BorderLayout.WEST);
          padNumber=new JTextField();padNumber.addKeyListener(this);   panel.add(padNumber,BorderLayout.CENTER);
          padNumber.setEnabled(false);
          layoutPanel.add(panel); 
          
          panel=new JPanel(); panel.setLayout(new BorderLayout()); 
          label=new JLabel("Pad Net name"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(100,label.getHeight())); panel.add(label,BorderLayout.WEST);
          padNetName=new JTextField();padNetName.addKeyListener(this);  panel.add(padNetName,BorderLayout.CENTER);
          layoutPanel.add(panel); 
          
          
      }
      public JPanel getLayoutPanel() {
	    return layoutPanel;
	  }
      @Override
      public void updateUI() {
    	
    		
      }
      private void _updateUI(Pad pad) {
    	  layoutPanel.setVisible(pad!=null);
    	  if(padRef!=null){
    		 padRef.clear(); 
    	  }
    	  padRef=new WeakReference<Pad>(pad);  
    	  if(padRef.get()==null) {
    		  return;
    	  }
    	  rotateField.setText(String.valueOf(pad.getRotate()));
          padNumber.setText(pad.getTextureByTag("number").getText());
          //numberSize.setText(String.valueOf(Grid.COORD_TO_MM(pad.getTextureByTag("number").getSize())));
          
          padNetName.setText(pad.getTextureByTag("netvalue").getText());        
          //netvalueSize.setText(String.valueOf(Grid.COORD_TO_MM(pad.getTextureByTag("netvalue").getSize())));
          
          widthField.setText(String.valueOf(Grid.COORD_TO_MM(pad.getWidth())));
          if(pad.getShapeType() == Pad.Shape.CIRCULAR||pad.getShapeType()==Pad.Shape.POLYGON){
              heightField.setEnabled(false);
          }else{
              heightField.setEnabled(true);
              heightField.setText(String.valueOf(Grid.COORD_TO_MM(pad.getHeight())));  
          }
          
          //leftField.setText(toUnitX(pad.getCenter().x,5));
          //topField.setText(toUnitY(pad.getCenter().y,5));
          
          setSelectedItem(layerCombo, pad.getCopper());
          setSelectedItem(padTypeCombo, pad.getType());
          setSelectedItem(padShapeCombo, pad.getShapeType());
          setSelectedItem(platedCombo, pad.getPlated());
          
          drillWidth.setText(String.valueOf(Grid.COORD_TO_MM(pad.getDrill()==null?0:pad.getDrill().getWidth())));
       
          drillWidth.setEnabled(pad.getType() != com.mynetpcb.pad.shape.Pad.Type.SMD);
          platedCombo.setEnabled(pad.getType() != com.mynetpcb.pad.shape.Pad.Type.SMD);
          solderMaskExpansion.setText(String.valueOf(Grid.COORD_TO_MM(pad.getSolderMaskExpansion())));
    	  
      }

      public void actionPerformed(ActionEvent e) {
          Pad pad=padRef.get();
          if(e.getSource()==padShapeCombo){
             pad.setShape((Pad.Shape)padShapeCombo.getSelectedItem());
             updateUI();
          }
          
          if(e.getSource()==padTypeCombo){
              pad.setType((Pad.Type)padTypeCombo.getSelectedItem());  
              updateUI();
          }
          if(e.getSource()==platedCombo){
              pad.setPlated((Boolean)platedCombo.getSelectedItem());              
          }        
          if(e.getSource()==layerCombo){
             pad.setCopper((Layer.Copper) layerCombo.getSelectedItem());            
          }
         
          getComponent().getModel().getUnit().registerMemento( getTarget().getState(MementoType.MOVE_MEMENTO));
          getComponent().Repaint();
      }
      
      @Override
      public void keyPressed(KeyEvent e) {    
          if(e.getKeyCode()!=KeyEvent.VK_ENTER) return;
          Pad pad=padRef.get();
          if(e.getSource()==this.rotateField){
             pad.setRotation(Double.parseDouble(this.rotateField.getText()),pad.getCenter()); 
             
          }
          if(e.getSource()==this.widthField){
             pad.setWidth(Grid.MM_TO_COORD(Double.parseDouble(widthField.getText())));  
          }        
          if(e.getSource()==this.heightField){
             pad.setHeight(Grid.MM_TO_COORD(Double.parseDouble(heightField.getText())));  
          }
          if(e.getSource()==this.solderMaskExpansion&&Double.parseDouble(solderMaskExpansion.getText())>=0){
              pad.setSolderMaskExpansion(Grid.MM_TO_COORD(Double.parseDouble(solderMaskExpansion.getText())));  
          }
          
          if(e.getSource()==this.padNumber){
             pad.getTextureByTag("number").setText(padNumber.getText());
          }
          if(e.getSource()==this.padNetName){
             pad.getTextureByTag("netvalue").setText(padNetName.getText());
          }

          if(e.getSource()==this.drillWidth){
             pad.getDrill().setWidth(Grid.MM_TO_COORD(Double.parseDouble(drillWidth.getText())));  
          }
          getComponent().getModel().getUnit().registerMemento(getTarget().getState(MementoType.MOVE_MEMENTO));
          getComponent().Repaint(); 
    }
    }
}
