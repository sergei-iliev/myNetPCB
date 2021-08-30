package com.mynetpcb.circuit.dialog.panel;

import com.mynetpcb.circuit.component.CircuitComponent;
import com.mynetpcb.circuit.dialog.panel.inspector.CircuitBuilderFactory;
import com.mynetpcb.circuit.unit.Circuit;
import com.mynetpcb.core.capi.event.ContainerEvent;
import com.mynetpcb.core.capi.event.ContainerListener;
import com.mynetpcb.core.capi.event.ShapeEvent;
import com.mynetpcb.core.capi.event.ShapeListener;
import com.mynetpcb.core.capi.event.UnitEvent;
import com.mynetpcb.core.capi.event.UnitListener;
import com.mynetpcb.core.capi.panel.AbstractPanelBuilder;
import com.mynetpcb.core.capi.panel.AbstractPanelBuilderFactory;
import com.mynetpcb.core.capi.shape.Shape;

import java.awt.BorderLayout;

import javax.swing.JPanel;

public class PropertyInspectorPanel extends JPanel implements ShapeListener,UnitListener,ContainerListener{

    private final  AbstractPanelBuilderFactory builderFactory;
    
    public PropertyInspectorPanel(CircuitComponent component) { 
               builderFactory=new CircuitBuilderFactory(component);
               component.getModel().addUnitListener(this);
               component.getModel().addShapeListener(this);
               component.addContainerListener(this);
               this.setLayout(new BorderLayout());                 
               //***enable circuit panel = initial visible panel 
               //this.add(builderFactory.getBuilder(CircuitComponent.class).getUI(null),BorderLayout.NORTH);                             
    }

    public void selectShapeEvent(ShapeEvent e) {
        Shape shape=e.getObject();
        if(null==builderFactory.getBuilder((shape==null?Circuit.class:shape.getClass())))
           return;
        //***this could be a click on a Pin tree node
        this.removeAll();
        this.add(builderFactory.getBuilder((shape==null?Circuit.class:shape.getClass())).getUI(shape),BorderLayout.NORTH);
        this.updateUI();  
        this.revalidate();
    }

    public void deleteShapeEvent(ShapeEvent e) { 
        this.removeAll();
        this.add(builderFactory.getBuilder(Circuit.class).getUI(null),BorderLayout.NORTH);
        this.updateUI();         
    }

    public void renameShapeEvent(ShapeEvent e) {
    }

    public void addShapeEvent(ShapeEvent e) {
    }

    public void propertyChangeEvent(ShapeEvent e) {
        Shape shape=e.getObject();      
        //***update fields only
        AbstractPanelBuilder builder=builderFactory.getBuilder((shape==null?Circuit.class:shape.getClass()));
        //***property change if selected symbol is current
        if(builder!=null&&builder.getTarget().equals(shape)){
           builder.updateUI(); 
        }
    }

    public void addUnitEvent(UnitEvent e) {
    }

    public void deleteUnitEvent(UnitEvent e) {
        this.removeAll();
        this.add(builderFactory.getBuilder(CircuitComponent.class).getUI(null),BorderLayout.NORTH);     
        this.updateUI();    
        this.revalidate();
        }

    public void selectUnitEvent(UnitEvent e) {
        this.removeAll();
        this.add(builderFactory.getBuilder(Circuit.class).getUI(null),BorderLayout.NORTH);
        this.updateUI();
        this.revalidate();
    }
    
    public void selectContainerEvent(ContainerEvent e) {    
        this.removeAll();
        this.add(builderFactory.getBuilder(CircuitComponent.class).getUI(null),BorderLayout.NORTH);     
        this.updateUI();
        this.revalidate();
    }
    
    @Override
    public void renameContainerEvent(ContainerEvent e){
        
    }
    
    @Override
    public void deleteContainerEvent(ContainerEvent e){
        
    }
    
    public void renameUnitEvent(UnitEvent e) {
    
    }
    public void propertyChangeEvent(UnitEvent e) {
        builderFactory.getBuilder(Circuit.class).updateUI(); 
    }

}

