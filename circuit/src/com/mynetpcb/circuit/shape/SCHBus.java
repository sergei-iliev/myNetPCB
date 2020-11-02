package com.mynetpcb.circuit.shape;

import com.mynetpcb.circuit.unit.Circuit;
import com.mynetpcb.core.capi.undo.AbstractMemento;
import com.mynetpcb.core.capi.undo.MementoType;
import com.mynetpcb.core.capi.unit.Unit;
import com.mynetpcb.d2.shapes.Point;

import java.util.Arrays;

public class SCHBus extends SCHWire{
    public SCHBus() {
        this.setThickness(4);
        this.displayName="Bus";
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
    
//    @Override
//    public void fromXML(Node node){ 
//        Element element=(Element)node;
//        Node n=element.getElementsByTagName("wire").item(0);
//        super.fromXML(n);
//    }
//    
//    @Override
//    public String toXML() {
//        StringBuffer xml=new StringBuffer();
//        if(getLinePoints().size()==0) {
//            return "";
//        }
//        xml.append("<bus>\r\n");
//        xml.append(super.toXML());
//        xml.append("</bus>\r\n");
//        return xml.toString();
//    }
    
    public static class Memento extends AbstractMemento<Circuit, SCHBus> {

        private double Ax[];

        private double Ay[];
        
        public Memento(MementoType mementoType) {
            super(mementoType);

        }

        @Override
        public void loadStateTo(SCHBus shape) {
            super.loadStateTo(shape);
            shape.polyline.points.clear();
            for (int i = 0; i < Ax.length; i++) {
                shape.add(new Point(Ax[i], Ay[i]));
            }
            //***reset floating start point
            if (shape.polyline.points.size() > 0) {
                shape.floatingStartPoint.set((Point)shape.polyline.points.get(shape.polyline.points.size() - 1));
                shape.reset();
            }
        }

        @Override
        public void saveStateFrom(SCHBus shape) {
            super.saveStateFrom(shape);
            Ax = new double[shape.polyline.points.size()];
            Ay = new double[shape.polyline.points.size()];
            for (int i = 0; i < shape.polyline.points.size(); i++) {
                Ax[i] = ((Point)shape.polyline.points.get(i)).x;
                Ay[i] = ((Point)shape.polyline.points.get(i)).y;
            }
        }

        @Override
        public void clear() {
            super.clear();
            Ax = null;
            Ay = null;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof Memento)) {
                return false;
            }
            Memento other = (Memento) obj;
            return (super.equals(obj)&&
                    Arrays.equals(Ax, other.Ax) && Arrays.equals(Ay, other.Ay));

        }

        @Override
        public int hashCode() {
            int  hash = super.hashCode();
            hash += Arrays.hashCode(Ax);
            hash += Arrays.hashCode(Ay);
            return hash;
        }
        @Override
        public boolean isSameState(Unit unit) {
            SCHBus line = (SCHBus) unit.getShape(getUUID());
            return (line.getState(getMementoType()).equals(this));
        }
    }  
                
}
