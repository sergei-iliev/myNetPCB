package com.mynetpcb.circuit.dialog.panel.inspector;

import com.mynetpcb.circuit.component.CircuitComponent;
import com.mynetpcb.circuit.shape.SCHBusPin;
import com.mynetpcb.circuit.shape.SCHConnector;
import com.mynetpcb.circuit.shape.SCHLabel;
import com.mynetpcb.circuit.shape.SCHSymbol;
import com.mynetpcb.circuit.unit.Circuit;
import com.mynetpcb.core.capi.panel.AbstractPanelBuilderFactory;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.symbol.dialog.panel.inspector.LabelPanelBuilder;


public class CircuitBuilderFactory extends AbstractPanelBuilderFactory<Shape>{
        public CircuitBuilderFactory(CircuitComponent component) {
          panelsMap.put(SCHBusPin.class,new BusPinPanelBuilder(component));
          panelsMap.put(Circuit.class,new CircuitPanelBuilder(component));
          panelsMap.put(SCHConnector.class,new ConnectorPanelBuilder(component));
          panelsMap.put(SCHSymbol.class,new SymbolPanelBuilder(component));
//          panelsMap.put(SCHNetLabel.class,new NetLabelPanelBuilder(component));  
          panelsMap.put(SCHLabel.class,new LabelPanelBuilder(component)); 
          panelsMap.put(CircuitComponent.class,new ComponentPanelBuilder(component)); 
        }

    }

