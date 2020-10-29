package com.mynetpcb.circuit.shape;

import com.mynetpcb.core.capi.shape.AbstractShapeFactory;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.undo.AbstractMemento;

import org.w3c.dom.Node;

public class CircuitShapeFactory implements AbstractShapeFactory{

    @Override
    public Shape createShape(Node node) {
return null;
    }

    @Override
    public Shape createShape(AbstractMemento memento) {
                if(memento instanceof SCHSymbol.Memento){
                   SCHSymbol symbol=new SCHSymbol();          
                   symbol.setState(memento);
                   return symbol;
                }
        //        if(memento instanceof SCHConnector.Memento){
        //          SCHConnector connector=new SCHConnector();  
        //          connector.setState(memento);
        //          return connector;
        //        }
        //        if(memento instanceof SCHBus.Memento){
        //           SCHBus bus=new SCHBus();
        //           bus.setState(memento);
        //           return bus;
        //        }
        //        if(memento instanceof SCHBusPin.Memento){
        //           SCHBusPin busPin=new SCHBusPin();
        //           busPin.setState(memento);
        //           return busPin;
        //        }        
        //        if(memento instanceof SCHWire.Memento){
        //           SCHWire wire=new SCHWire();
        //           wire.setState(memento);
        //           return wire;
        //        }
        //        if(memento instanceof SCHJunction.Memento){
        //           SCHJunction junction=new SCHJunction();
        //           junction.setState(memento);
        //           return junction;
        //        }
                if(memento instanceof SCHLabel.Memento){
                    SCHLabel label=new SCHLabel();
                    label.setState(memento);
                    return label;            
                }
        //        if(memento instanceof SCHNoConnector.Memento){
        //            SCHNoConnector noconnector=new SCHNoConnector();
        //            noconnector.setState(memento);
        //            return noconnector;                        
        //        }
        //        if(memento instanceof SCHNetLabel.Memento){
        //                SCHNetLabel netlabel=new SCHNetLabel();
        //                netlabel.setState(memento);
        //                return netlabel;                        
        //        }        
                throw new IllegalStateException("Unknown memento type: "+memento.getClass().getCanonicalName());        
            
    }
}
