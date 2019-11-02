package com.mynetpcb.pad.dialog.panel.inspector;

import com.mynetpcb.core.capi.layer.Layer;
import com.mynetpcb.core.capi.panel.AbstractPanelBuilder;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.undo.MementoType;
import com.mynetpcb.pad.component.FootprintComponent;
import com.mynetpcb.pad.shape.Pad;

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

public class PadPanelBuilder extends AbstractPanelBuilder<Shape>{
    private JTextField padNumber,padNetName,padRotate;
        
    private JComboBox padTypeCombo,padShapeCombo;
    
    private JTextField drillWidth,drillOffsetX,drillOffsetY,numberSize,netvalueSize,numberX,numberY,netvalueX,netvalueY;
    
    public PadPanelBuilder(FootprintComponent component) {
        super(component,new GridLayout(19,1));
        panel=new JPanel(); panel.setLayout(new BorderLayout()); 
        label=new JLabel("Layer"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(90,label.getHeight())); panel.add(label,BorderLayout.WEST);
        layerCombo=new JComboBox(Layer.PAD_LAYERS);layerCombo.addActionListener(this);  panel.add(layerCombo,BorderLayout.CENTER);                
        layoutPanel.add(panel);
        //***X        
        panel=new JPanel(); panel.setLayout(new BorderLayout());
        label=new JLabel("X"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(90,label.getHeight())); panel.add(label,BorderLayout.WEST);
        leftField=new JTextField("0");leftField.addKeyListener(this);  panel.add(leftField,BorderLayout.CENTER);
        layoutPanel.add(panel);
        
        //***Y        
        panel=new JPanel(); panel.setLayout(new BorderLayout());
        label=new JLabel("Y"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(90,label.getHeight())); panel.add(label,BorderLayout.WEST);
        topField=new JTextField("0"); topField.addKeyListener(this); panel.add(topField,BorderLayout.CENTER);
        layoutPanel.add(panel);        
        //****size x
        panel=new JPanel(); panel.setLayout(new BorderLayout()); 
        label=new JLabel("Width"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(90,label.getHeight())); panel.add(label,BorderLayout.WEST);
        widthField=new JTextField("");  widthField.addKeyListener(this);panel.add(widthField,BorderLayout.CENTER);
        layoutPanel.add(panel);  
        //****size y
        panel=new JPanel(); panel.setLayout(new BorderLayout()); 
        label=new JLabel("Height"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(90,label.getHeight())); panel.add(label,BorderLayout.WEST);
        heightField=new JTextField("");  heightField.addKeyListener(this); panel.add(heightField,BorderLayout.CENTER);
        layoutPanel.add(panel);
        //rotate
        panel=new JPanel(); panel.setLayout(new BorderLayout());
        label=new JLabel("Rotate"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(90,label.getHeight())); panel.add(label,BorderLayout.WEST);
        padRotate=new JTextField("0");padRotate.addKeyListener(this);  panel.add(padRotate,BorderLayout.CENTER);
        layoutPanel.add(panel);
        //pad type
        panel=new JPanel(); panel.setLayout(new BorderLayout()); 
        label=new JLabel("Pad Type"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(90,label.getHeight())); panel.add(label,BorderLayout.WEST);
        padTypeCombo=new JComboBox(com.mynetpcb.pad.shape.Pad.Type.values());padTypeCombo.setPreferredSize(new Dimension(90,padTypeCombo.getHeight())); padTypeCombo.addActionListener(this);  panel.add(padTypeCombo,BorderLayout.CENTER);                
        layoutPanel.add(panel);
        //pad shape
        panel=new JPanel(); panel.setLayout(new BorderLayout()); 
        label=new JLabel("Pad Shape"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(90,label.getHeight())); panel.add(label,BorderLayout.WEST);
        padShapeCombo=new JComboBox(com.mynetpcb.pad.shape.Pad.Shape.values());padShapeCombo.addActionListener(this);  panel.add(padShapeCombo,BorderLayout.CENTER);                
        layoutPanel.add(panel); 

        panel=new JPanel(); panel.setLayout(new BorderLayout()); 
        label=new JLabel("Drill Width"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(90,label.getHeight())); panel.add(label,BorderLayout.WEST);
        drillWidth=new JTextField("");  drillWidth.addKeyListener(this);panel.add(drillWidth,BorderLayout.CENTER);
        layoutPanel.add(panel);  
        
        //****offset x
        panel=new JPanel(); panel.setLayout(new BorderLayout()); 
        label=new JLabel("Offset X"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(90,label.getHeight())); panel.add(label,BorderLayout.WEST);
        drillOffsetX=new JTextField("");  drillOffsetX.addKeyListener(this); panel.add(drillOffsetX,BorderLayout.CENTER);
        layoutPanel.add(panel);
        
        //****offset y
        panel=new JPanel(); panel.setLayout(new BorderLayout()); 
        label=new JLabel("Offset Y"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(90,label.getHeight())); panel.add(label,BorderLayout.WEST);
        drillOffsetY=new JTextField("");  drillOffsetY.addKeyListener(this);panel.add(drillOffsetY,BorderLayout.CENTER);
        layoutPanel.add(panel);         
        //***layer MULTISELECT combo box
//        panel=new JPanel(); panel.setLayout(new BorderLayout()); 
//        label=new JLabel("Layer"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(90,label.getHeight())); panel.add(label,BorderLayout.WEST);
//        List<String> colors = Arrays.asList("Red","Green","Yellow");
//        layerMultiCombo = new CheckComboBox();  panel.add(layerMultiCombo,BorderLayout.CENTER);
//        ListCheckModel model = layerMultiCombo.getModel(); 
//        model.addListCheckListener(this);
//        layerMultiCombo.setTextFor(CheckComboBox.NONE, "no selection"); 
//        layerMultiCombo.setTextFor(CheckComboBox.MULTIPLE, "multiple selection"); 
//        layerMultiCombo.setTextFor(CheckComboBox.ALL, "all selected"); 
//        
//        for (String color : colors) { 
//          model.addElement(color); 
//        }   
//        model.setCheck("Red");
//        layoutPanel.add(panel);
        
        //pad number
        panel=new JPanel(); panel.setLayout(new BorderLayout()); 
        label=new JLabel("Pad Number"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(90,label.getHeight())); panel.add(label,BorderLayout.WEST);
        padNumber=new JTextField(); padNumber.addKeyListener(this);  panel.add(padNumber,BorderLayout.CENTER);
        layoutPanel.add(panel);    

        //****font size
        panel=new JPanel(); panel.setLayout(new BorderLayout()); 
        label=new JLabel("Size"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(90,label.getHeight())); panel.add(label,BorderLayout.WEST);
        numberSize=new JTextField("");  numberSize.addKeyListener(this);panel.add(numberSize,BorderLayout.CENTER);
        layoutPanel.add(panel); 
        
        //****x
        panel=new JPanel(); panel.setLayout(new BorderLayout()); 
        label=new JLabel("X"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(90,label.getHeight())); panel.add(label,BorderLayout.WEST);
        numberX=new JTextField("");  numberX.addKeyListener(this); panel.add(numberX,BorderLayout.CENTER);
        layoutPanel.add(panel);
        
        
        //****y
        panel=new JPanel(); panel.setLayout(new BorderLayout()); 
        label=new JLabel("Y"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(90,label.getHeight())); panel.add(label,BorderLayout.WEST);
        numberY=new JTextField("");  numberY.addKeyListener(this); panel.add(numberY,BorderLayout.CENTER);
        layoutPanel.add(panel);
        
        
        
        //pad net name
        panel=new JPanel(); panel.setLayout(new BorderLayout()); 
        label=new JLabel("Pad Net name"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(90,label.getHeight())); panel.add(label,BorderLayout.WEST);
        padNetName=new JTextField(); padNetName.addKeyListener(this); panel.add(padNetName,BorderLayout.CENTER);
        layoutPanel.add(panel); 
                
        //font size
        panel=new JPanel(); panel.setLayout(new BorderLayout()); 
        label=new JLabel("Size"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(90,label.getHeight())); panel.add(label,BorderLayout.WEST);
        netvalueSize=new JTextField("");  netvalueSize.addKeyListener(this); panel.add(netvalueSize,BorderLayout.CENTER);
        layoutPanel.add(panel);
        
        //****x
        panel=new JPanel(); panel.setLayout(new BorderLayout()); 
        label=new JLabel("X"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(90,label.getHeight())); panel.add(label,BorderLayout.WEST);
        netvalueX=new JTextField("");  netvalueX.addKeyListener(this); panel.add(netvalueX,BorderLayout.CENTER);
        layoutPanel.add(panel);
        
        
        //****y
        panel=new JPanel(); panel.setLayout(new BorderLayout()); 
        label=new JLabel("Y"); label.setHorizontalAlignment(SwingConstants.CENTER); label.setPreferredSize(new Dimension(90,label.getHeight())); panel.add(label,BorderLayout.WEST);
        netvalueY=new JTextField("");  netvalueY.addKeyListener(this); panel.add(netvalueY,BorderLayout.CENTER);
        layoutPanel.add(panel);
        
        
    }

    @Override
    public void updateUI() {
        Pad pad=(Pad)getTarget();
        padRotate.setText(String.valueOf(pad.getRotation()));
        //numberSize.setText(String.valueOf(Grid.COORD_TO_MM(pad.getChipText().getTextureByTag("number").getSize())));
        
        //padNetName.setText(pad.getNetName());
        //netvalueSize.setText(String.valueOf(Grid.COORD_TO_MM(pad.getChipText().getTextureByTag("netvalue").getSize())));
        
//        widthField.setText(String.valueOf(Grid.COORD_TO_MM(pad.getWidth())));
//        if(pad.getShape() == Pad.Shape.CIRCULAR||pad.getShape()==Pad.Shape.POLYGON){
//            heightField.setEnabled(false);
//        }else{
//            heightField.setEnabled(true);
//            heightField.setText(String.valueOf(Grid.COORD_TO_MM(pad.getHeight())));  
//        }
//        
        leftField.setText(toUnitX(pad.getCenter().x));
        topField.setText(toUnitY(pad.getCenter().y));
        
        setSelectedItem(layerCombo, pad.getCopper());
        setSelectedItem(padTypeCombo, pad.getType());
        setSelectedItem(padShapeCombo, pad.getShape());
//        
//        drillWidth.setText(String.valueOf(Grid.COORD_TO_MM(pad.getDrill()==null?0:pad.getDrill().getWidth())));
//        drillOffsetX.setText(String.valueOf(Grid.COORD_TO_MM(pad.getOffset().x )));
//        drillOffsetY.setText(String.valueOf(Grid.COORD_TO_MM(pad.getOffset().y)));
     
        drillWidth.setEnabled(pad.getType() != com.mynetpcb.pad.shape.Pad.Type.SMD);
        drillOffsetX.setEnabled(pad.getType() != com.mynetpcb.pad.shape.Pad.Type.SMD);
        drillOffsetY.setEnabled(pad.getType() != com.mynetpcb.pad.shape.Pad.Type.SMD); 

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        super.actionPerformed(e);
        Pad pad=(Pad)getTarget();
//        if(e.getSource()==padShapeCombo){
//           pad.setShape((Pad.Shape)padShapeCombo.getSelectedItem());
//           updateUI();
//        }
        
        if(e.getSource()==padTypeCombo){
            pad.setType((Pad.Type)padTypeCombo.getSelectedItem());  
            updateUI();
        }
        
//        if(e.getSource()==layerCombo){
//            Layer.Copper copper=(Layer.Copper) layerCombo.getSelectedItem();
//            if(copper.isCopperLayer()){
//               pad.setCopper((Layer.Copper) layerCombo.getSelectedItem());            
//            }else{
//              setSelectedItem(layerCombo, pad.getCopper());   
//            }
//        }
        

        getComponent().getModel().getUnit().registerMemento( getTarget().getState(MementoType.MOVE_MEMENTO));
        getComponent().Repaint();
    }
    
    @Override
    public void keyReleased(KeyEvent e){
        if(e.getKeyCode()!=KeyEvent.VK_ENTER) return;
        Pad pad=(Pad)getTarget();
        if(e.getSource()==this.padRotate){
           pad.setRotation(Double.parseDouble(this.padRotate.getText())); 
           
        }
//        if(e.getSource()==this.heightField){
//           pad.setHeight(Grid.MM_TO_COORD(Double.parseDouble(heightField.getText())));  
//        }
//        
//        if(e.getSource()==this.padNumber){
//           pad.getChipText().getTextureByTag("number").setText(padNumber.getText());
//        }
//        if(e.getSource()==this.numberSize){
//           pad.getChipText().getTextureByTag("number").setSize(Grid.MM_TO_COORD(Double.parseDouble(numberSize.getText())));
//        }
//        if(e.getSource()==this.numberX){
//           pad.getChipText().getTextureByTag("number").getAnchorPoint().x= fromUnitX(numberX.getText());
//        }
//        if(e.getSource()==this.numberY){
//           pad.getChipText().getTextureByTag("number").getAnchorPoint().y= fromUnitY(numberY.getText());
//        }
//        
//        if(e.getSource()==this.padNetName){
//           pad.setNetName(padNetName.getText());
//        }
//        if(e.getSource()==this.netvalueSize){
//           pad.getChipText().getTextureByTag("netvalue").setSize(Grid.MM_TO_COORD(Double.parseDouble(netvalueSize.getText())));
//        }        
//        if(e.getSource()==this.netvalueX){
//           pad.getChipText().getTextureByTag("netvalue").getAnchorPoint().x= fromUnitX(netvalueX.getText());
//        }
//        if(e.getSource()==this.netvalueY){
//           pad.getChipText().getTextureByTag("netvalue").getAnchorPoint().y= fromUnitY(netvalueY.getText());
//        }
//        
//        if(e.getSource()==this.drillOffsetX){
//           pad.getOffset().x=(Grid.MM_TO_COORD(Double.parseDouble(drillOffsetX.getText()))); 
//        }
//        
//        if(e.getSource()==this.drillOffsetY){
//           pad.getOffset().y=(Grid.MM_TO_COORD(Double.parseDouble(drillOffsetY.getText())));  
//        }
//        
//        if(e.getSource()==this.drillWidth){
//           pad.getDrill().setWidth(Grid.MM_TO_COORD(Double.parseDouble(drillWidth.getText())));  
//        }
        getComponent().getModel().getUnit().registerMemento( getTarget().getState(MementoType.MOVE_MEMENTO));
        getComponent().Repaint();  
        
    }

}
