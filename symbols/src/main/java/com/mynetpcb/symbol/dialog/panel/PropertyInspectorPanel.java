package com.mynetpcb.symbol.dialog.panel;

import com.mynetpcb.core.capi.event.ContainerEvent;
import com.mynetpcb.core.capi.event.ContainerListener;
import com.mynetpcb.core.capi.event.ShapeEvent;
import com.mynetpcb.core.capi.event.ShapeListener;
import com.mynetpcb.core.capi.event.UnitEvent;
import com.mynetpcb.core.capi.event.UnitListener;
import com.mynetpcb.core.capi.panel.AbstractPanelBuilder;
import com.mynetpcb.core.capi.panel.AbstractPanelBuilderFactory;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.symbol.component.SymbolComponent;
import com.mynetpcb.symbol.dialog.panel.inspector.SymbolBuilderFactory;
import com.mynetpcb.symbol.unit.Symbol;

import java.awt.BorderLayout;

import javax.swing.JPanel;

public class PropertyInspectorPanel extends JPanel implements ShapeListener,UnitListener,ContainerListener{
    
    private final  AbstractPanelBuilderFactory builderFactory;
    
    public PropertyInspectorPanel(SymbolComponent component) {
        builderFactory=new SymbolBuilderFactory(component);
        component.getModel().addUnitListener(this);
        component.getModel().addShapeListener(this);
        component.addContainerListener(this);
        this.setLayout(new BorderLayout());      
    }

    public void selectShapeEvent(ShapeEvent e) {
        Shape shape=e.getObject();   
        //***this could be a click on a Pin tree node
        this.removeAll();
        this.add(builderFactory.getBuilder((shape==null?Symbol.class:shape.getClass())).getUI(shape),BorderLayout.NORTH);
        this.revalidate();
        this.repaint();        
    }

    public void deleteShapeEvent(ShapeEvent e) {
    }

    public void renameShapeEvent(ShapeEvent e) {
    }

    public void addShapeEvent(ShapeEvent e) {
    }

    public void propertyChangeEvent(ShapeEvent e) {
        Shape shape=e.getObject();      
        //***update fields only
        AbstractPanelBuilder builder=builderFactory.getBuilder((shape==null?Symbol.class:shape.getClass()));        
        //***property change if selected symbol is current
        if(builder!=null&&builder.getTarget().equals(shape)){
           builder.updateUI(); 
        }
    }

    public void addUnitEvent(UnitEvent e) {
    }

    public void deleteUnitEvent(UnitEvent e) {  
    }

    public void selectUnitEvent(UnitEvent e) {
        this.removeAll();
        this.add(builderFactory.getBuilder(Symbol.class).getUI(null),BorderLayout.NORTH);
        this.updateUI();  
        this.revalidate();
    }

    public void renameUnitEvent(UnitEvent e) {
    }

    public void propertyChangeEvent(UnitEvent e) {
        builderFactory.getBuilder(Symbol.class).updateUI(); 
    }
    @Override
    public void selectContainerEvent(ContainerEvent e) {        
        this.removeAll();
        this.add(builderFactory.getBuilder(SymbolComponent.class).getUI(null),BorderLayout.NORTH);     
        this.updateUI(); 
        this.revalidate();
    }
    @Override
    public void renameContainerEvent(ContainerEvent e) {
    }

    @Override
    public void deleteContainerEvent(ContainerEvent e) {
    }
}
