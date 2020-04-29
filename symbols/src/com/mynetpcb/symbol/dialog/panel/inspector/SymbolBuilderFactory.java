package com.mynetpcb.symbol.dialog.panel.inspector;

import com.mynetpcb.core.capi.panel.AbstractPanelBuilderFactory;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.symbol.component.SymbolComponent;
import com.mynetpcb.symbol.unit.Symbol;

public class SymbolBuilderFactory extends AbstractPanelBuilderFactory<Shape>{
        public SymbolBuilderFactory(SymbolComponent component) {
//          panelsMap.put(Ellipse.class,new EllipsePanelBuilder(component));
          panelsMap.put(Symbol.class,new SymbolPanelBuilder(component));
//          panelsMap.put(RoundRect.class,new RectPanelBuilder(component));
//          panelsMap.put(Arc.class,new ArcPanelBuilder(component));
//          panelsMap.put(Triangle.class,new EllipsePanelBuilder(component));
//          panelsMap.put(Line.class,new LinePanelBuilder(component)); 
//          panelsMap.put(Arrow.class,new ArrowPanelBuilder(component)); 
//          panelsMap.put(Pin.class,new PinPanelBuilder(component));  
//          panelsMap.put(FontLabel.class,new LabelPanelBuilder(component)); 
          panelsMap.put(SymbolComponent.class,new ComponentPanelBuilder(component)); 
        }

    }
