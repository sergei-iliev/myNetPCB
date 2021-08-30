package com.mynetpcb.pad.dialog.panel;


import com.mynetpcb.core.capi.event.ContainerEvent;
import com.mynetpcb.core.capi.event.ContainerListener;
import com.mynetpcb.core.capi.event.ShapeEvent;
import com.mynetpcb.core.capi.event.ShapeListener;
import com.mynetpcb.core.capi.event.UnitEvent;
import com.mynetpcb.core.capi.event.UnitListener;
import com.mynetpcb.core.capi.panel.AbstractPanelBuilder;
import com.mynetpcb.core.capi.panel.AbstractPanelBuilderFactory;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.pad.component.FootprintComponent;
import com.mynetpcb.pad.dialog.panel.inspector.FootprintBuilderFactory;
import com.mynetpcb.pad.unit.Footprint;

import java.awt.BorderLayout;

import javax.swing.JPanel;


public class PropertyInspectorPanel extends JPanel implements ShapeListener, UnitListener, ContainerListener {

    private final AbstractPanelBuilderFactory builderFactory;

    public PropertyInspectorPanel(FootprintComponent component) {
        builderFactory = new FootprintBuilderFactory(component);
        component.getModel().addUnitListener(this);
        component.getModel().addShapeListener(this);
        component.addContainerListener(this);
        this.setLayout(new BorderLayout());
    }

    @Override
    public void selectShapeEvent(ShapeEvent e) {
        Shape shape = e.getObject();
        //***this could be a click on a Pin tree node
        this.removeAll();
        this.add(builderFactory.getBuilder((shape==null?Footprint.class:shape.getClass())).getUI(shape),BorderLayout.NORTH);
        this.revalidate();
        this.repaint();
    }

    @Override
    public void deleteShapeEvent(ShapeEvent e) {
    }

    @Override
    public void renameShapeEvent(ShapeEvent e) {
    }

    @Override
    public void addShapeEvent(ShapeEvent e) {
    }

    @Override
    public void propertyChangeEvent(ShapeEvent e) {
        Shape shape = e.getObject();
        //***update fields only
        AbstractPanelBuilder builder = builderFactory.getBuilder((shape == null ? Footprint.class : shape.getClass()));
        //***property change if selected symbol is current
        if (builder != null && builder.getTarget() != null && builder.getTarget().equals(shape)) {
            builder.updateUI();
        }
    }

    @Override
    public void addUnitEvent(UnitEvent e) {
    }

    @Override
    public void deleteUnitEvent(UnitEvent e) {
    }

    @Override
    public void renameUnitEvent(UnitEvent e) {
    }

    @Override
    public void selectUnitEvent(UnitEvent e) {
        this.removeAll();
        this.add(builderFactory.getBuilder(Footprint.class).getUI(null), BorderLayout.NORTH);
        this.updateUI();
        this.revalidate();
    }

    @Override
    public void propertyChangeEvent(UnitEvent e) {
        AbstractPanelBuilder builder = builderFactory.getBuilder(Footprint.class);
        builder.updateUI();
    }


    @Override
    public void selectContainerEvent(ContainerEvent e) {
        this.removeAll();
        this.add(builderFactory.getBuilder(FootprintComponent.class).getUI(null), BorderLayout.NORTH);
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

