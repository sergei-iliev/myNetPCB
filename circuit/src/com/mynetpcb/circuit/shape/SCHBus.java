package com.mynetpcb.circuit.shape;

import com.mynetpcb.core.capi.undo.AbstractMemento;
import com.mynetpcb.core.capi.undo.MementoType;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class SCHBus extends SCHWire{
    public SCHBus() {
        super(4);
    }
    
    public SCHBus clone()throws CloneNotSupportedException{
        SCHBus copy=(SCHBus)super.clone();
        return copy;
    }
    
    public AbstractMemento getState(MementoType operationType) {
        Memento memento = new Memento(operationType);
        memento.saveStateFrom(this);
        return memento;
    }

    public void setState(AbstractMemento memento) {
        ((Memento)memento).loadStateTo(this);
    }

    public String getDisplayName() {
        return "Bus";
    }
    
    @Override
    public void fromXML(Node node){ 
        Element element=(Element)node;
        Node n=element.getElementsByTagName("wire").item(0);
        super.fromXML(n);
    }
    
    @Override
    public String toXML() {
        StringBuffer xml=new StringBuffer();
        if(getLinePoints().size()==0) {
            return "";
        }
        xml.append("<bus>\r\n");
        xml.append(super.toXML());
        xml.append("</bus>\r\n");
        return xml.toString();
    }
    
    static class Memento extends SCHWire.Memento{
        public Memento(MementoType mementoType){
          super(mementoType);  
        } 
    }
}
