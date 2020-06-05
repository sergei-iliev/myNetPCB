package com.mynetpcb.symbol.dialog.panel.inspector;

import com.mynetpcb.core.capi.panel.AbstractPanelBuilder;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.symbol.component.SymbolComponent;

import java.awt.GridLayout;

public class TrianglePanelBuilder extends AbstractPanelBuilder<Shape>{
    
    public TrianglePanelBuilder(SymbolComponent component) {
        super(component, new GridLayout(2, 1));    
    }

    @Override
    public void updateUI() {
        
    }
}
