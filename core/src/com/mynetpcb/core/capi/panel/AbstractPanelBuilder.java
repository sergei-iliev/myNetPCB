package com.mynetpcb.core.capi.panel;


import com.mynetpcb.core.capi.CoordinateSystem;
import com.mynetpcb.core.capi.Ownerable;
import com.mynetpcb.core.capi.SortedList;
import com.mynetpcb.core.capi.component.UnitComponent;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.text.Text;
import com.mynetpcb.core.capi.text.Texture;
import com.mynetpcb.core.capi.tree.AttachedItem;
import com.mynetpcb.core.pad.Layer;

import java.awt.Component;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import java.lang.ref.WeakReference;

import java.util.Collection;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;


/*
 * Use the builder pattern to construct,show dynamically target properties in XXXInspector
 */
public abstract class AbstractPanelBuilder<S extends Shape> extends KeyAdapter implements ActionListener{
  protected JTextField netField,topField,leftField,widthField,heightField,clearanceField,thicknessField;

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
  
  protected JComboBox textOrientationCombo,textAlignmentCombo,styleCombo;  
    
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
    
    protected void fillParentCombo(Class<?> clazz){
        //***disconnect from listener
        this.parentCombo.removeActionListener(this);
        parentCombo.removeAllItems();
        parentCombo.addItem(new AttachedItem.Builder("none").setUUID(null).build());
        
        Shape parent=((Ownerable)getTarget()).getOwner();
        Collection<S> shapes=getComponent().getModel().getUnit().getShapes(clazz);
        //***get could be Owners list      
        for(S shape:shapes){
            //if(symbol instanceof Chip){
             AttachedItem item=new AttachedItem.Builder(shape.getDisplayName()).setUUID(shape.getUUID()).build();  
             parentCombo.addItem(item);  
             if(parent!=null){
               if(parent.getUUID().equals(item.getUUID()))
                 parentCombo.setSelectedItem(item);  
             }    
            }
        //***reconnect
        this.parentCombo.addActionListener(this);       
    }
    
    /**
     *Convert internal units value to user unit
     * @param value
     * @return
     */
    protected String toUnit(int value){
       return String.valueOf(getComponent().getModel().getUnit().getGrid().COORD_TO_UNIT(value));  
    }
    
    /**
     *Convert user unit to internal one 
     * @param value
     * @return
     */
    protected int fromUnit(String value){
       return getComponent().getModel().getUnit().getGrid().UNIT_TO_COORD(Double.parseDouble(value)) ;     
    }    
    /**
     *Convert internal unit values in pixel to user unit,taking care of coordinate shift
     * @param X value to convert
     * @return display value in user units
     */
    protected String toUnitX(double value){        
        CoordinateSystem coordinateSystem =getComponent().getModel().getUnit().getCoordinateSystem();
        return String.valueOf(getComponent().getModel().getUnit().getGrid().COORD_TO_UNIT(value-coordinateSystem.getOrigin().x));      
    }
    /**
     *Convert internal unit values in pixel to user unit,taking care of coordinate shift
     * @param Y value to convert
     * @return display value in user units
     */
    protected String toUnitY(double value){
        CoordinateSystem coordinateSystem =getComponent().getModel().getUnit().getCoordinateSystem();
        return String.valueOf(getComponent().getModel().getUnit().getGrid().COORD_TO_UNIT(value-coordinateSystem.getOrigin().y));
    }
    /**
     *Convert from unit coordinate to internal one,taking care of coordinate shift
     * @param x value
     * @return internal value
     */
    protected int fromUnitX(String value){
        CoordinateSystem coordinateSystem =getComponent().getModel().getUnit().getCoordinateSystem();
        return getComponent().getModel().getUnit().getGrid().UNIT_TO_COORD(Double.parseDouble(value))+coordinateSystem.getOrigin().x;  
    }
    /**
     *Convert from unit coordinate to internal one,taking care of coordinate shift
     * @param y value
     * @return internal value
     */    
    protected int fromUnitY(String value){
        CoordinateSystem coordinateSystem =getComponent().getModel().getUnit().getCoordinateSystem();
        return getComponent().getModel().getUnit().getGrid().UNIT_TO_COORD(Double.parseDouble(value))+coordinateSystem.getOrigin().y;         
    }
    public abstract void updateUI();
    
    //***common text alignment code
    protected void validateAlignmentComboText(JComboBox combo,Texture text){
        if(text==null)
            return;
        combo.removeActionListener(this);
        if(text.getAlignment().getOrientation() == Text.Orientation.HORIZONTAL){
            combo.setModel(new DefaultComboBoxModel(textAlignmentHorizontal));            
            combo.setSelectedIndex(text.getAlignment() == Text.Alignment.LEFT?0:1);
        }else{
            combo.setModel(new DefaultComboBoxModel(textAlignmentVertical));
            combo.setSelectedIndex(text.getAlignment() == Text.Alignment.BOTTOM?0:1);        
        }        
        combo.addActionListener(this);          
    }
    
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
            ((SortedList)getComponent().getModel().getUnit().getShapes()).reorder();
        }
    }
}


