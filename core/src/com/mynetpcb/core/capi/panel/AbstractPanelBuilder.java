package com.mynetpcb.core.capi.panel;


import com.mynetpcb.core.capi.component.UnitComponent;
import com.mynetpcb.core.capi.layer.Layer;
import com.mynetpcb.core.capi.shape.CoordinateSystem;
import com.mynetpcb.core.capi.shape.Shape;

import java.awt.Component;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import java.lang.ref.WeakReference;

import java.util.Objects;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;


/*
 * Use the builder pattern to construct,show dynamically target properties in XXXInspector
 */
public abstract class AbstractPanelBuilder<S extends Shape> extends KeyAdapter implements ActionListener{
  protected JTextField netField,topField,leftField,widthField,heightField,clearanceField,thicknessField,nameField,rotateField;

  protected JLabel label;
  
  protected String[] arcType={"OPEN","CHORD","PIE"};
    
  protected static final String[] textAlignmentVertical={"BOTTOM","TOP"};
    
  protected static final String[] textAlignmentHorizontal={"LEFT","RIGHT"};
  
  protected static String[] fillValues={"EMPTY","FILLED"};    
//    
  protected String[] trueFalse={"false","true"};
      
  private final WeakReference<? extends UnitComponent> component;   //circuit,module,package
  
  private WeakReference<S> target;  //moveable,newelement
  
  protected  final JPanel layoutPanel;
  
  protected JPanel panel;
    
  private JScrollPane scrollPane ;
  
  protected JComboBox fillCombo,parentCombo,layerCombo;
  
  protected JComboBox styleCombo;
  
  protected  JTextField originX,originY;
    
    public AbstractPanelBuilder(UnitComponent component,LayoutManager layoutManager) {
       layoutPanel=new JPanel(layoutManager);
       this.scrollPane = new JScrollPane(layoutPanel);
       this.component=new WeakReference<>(component);
    }
    
    protected UnitComponent getComponent(){
       return component.get(); 
    }
    
    public S getTarget(){ 
       return target.get();  
    }
    
    protected void setTarget(S _target){
        if(target!=null){
           target.clear(); 
        }

        target=new WeakReference<S>(_target);        
    }
    
    @Override
    public void keyTyped(KeyEvent e) {
      //***isolate special characters
      switch(e.getKeyChar()){
            case '<': 
                  e.consume(); break;
            case '>': 
                  e.consume(); break;
            case '\"': 
                  e.consume(); break;
            case '&': 
                  e.consume(); break;
            case '\'': 
                 e.consume(); break;
      }

    }
    public final Component getUI(S target){
      setTarget(target);
      updateUI();
      return layoutPanel;  
    }
    
    /**
     *Convert internal units value to user unit
     * @param value
     * @return
     */
    protected String toUnit(double value){
       return String.valueOf(getComponent().getModel().getUnit().getGrid().COORD_TO_UNIT(value));  
    }
    
    /**
     *Convert user unit to internal one 
     * @param value
     * @return
     */
    protected double fromUnit(String value){
       return getComponent().getModel().getUnit().getGrid().UNIT_TO_COORD(Double.parseDouble(value)) ;     
    }    
    /**
     *Convert internal unit values in pixel to user unit,taking care of coordinate shift
     * @param X value to convert
     * @return display value in user units
     */
    protected String toUnitX(double value){        
        CoordinateSystem coordinateSystem =getComponent().getModel().getUnit().getCoordinateSystem();
        if(Objects.isNull(coordinateSystem))
           return String.valueOf(getComponent().getModel().getUnit().getGrid().COORD_TO_UNIT(value));      
        else    
           return String.valueOf(getComponent().getModel().getUnit().getGrid().COORD_TO_UNIT(value-coordinateSystem.getOrigin().x));      

    }
    /**
     *Convert internal unit values in pixel to user unit,taking care of coordinate shift
     * @param Y value to convert
     * @return display value in user units
     */
    protected String toUnitY(double value){
        CoordinateSystem coordinateSystem =getComponent().getModel().getUnit().getCoordinateSystem();
        if(Objects.isNull(coordinateSystem))
            return String.valueOf(getComponent().getModel().getUnit().getGrid().COORD_TO_UNIT(value));
        else    
            return String.valueOf(getComponent().getModel().getUnit().getGrid().COORD_TO_UNIT(value-coordinateSystem.getOrigin().y));
    }
    /**
     *Convert from unit coordinate to internal one,taking care of coordinate shift
     * @param x value
     * @return internal value
     */
    protected double fromUnitX(String value){
        CoordinateSystem coordinateSystem =getComponent().getModel().getUnit().getCoordinateSystem();
        if(Objects.isNull(coordinateSystem))
           return getComponent().getModel().getUnit().getGrid().UNIT_TO_COORD(Double.parseDouble(value));  
        else    
           return getComponent().getModel().getUnit().getGrid().UNIT_TO_COORD(Double.parseDouble(value))+coordinateSystem.getOrigin().x;  
    }
    /**
     *Convert from unit coordinate to internal one,taking care of coordinate shift
     * @param y value
     * @return internal value
     */    
    protected double fromUnitY(String value){
        CoordinateSystem coordinateSystem =getComponent().getModel().getUnit().getCoordinateSystem();
        if(Objects.isNull(coordinateSystem))
            return getComponent().getModel().getUnit().getGrid().UNIT_TO_COORD(Double.parseDouble(value));
        else
            return getComponent().getModel().getUnit().getGrid().UNIT_TO_COORD(Double.parseDouble(value))+coordinateSystem.getOrigin().y;         
        
        
    }
    public abstract void updateUI();
    
    protected void setSelectedIndex(JComboBox combo,int index){
        //***disconnect from listener
        combo.removeActionListener(this);
        combo.setSelectedIndex(index);
        //***reconnect
        combo.addActionListener(this);        
    }
    
    protected void setSelectedItem(JComboBox combo,Object item){
        //***disconnect from listener
        combo.removeActionListener(this);
        combo.setSelectedItem(item);
        //***reconnect
        combo.addActionListener(this);        
    }   
    
    public void actionPerformed(ActionEvent e) {
        if(e.getSource()==layerCombo){
            getTarget().setCopper((Layer.Copper)layerCombo.getSelectedItem());
            getComponent().getModel().getUnit().getShapes().reorder();
        }
    }
}


