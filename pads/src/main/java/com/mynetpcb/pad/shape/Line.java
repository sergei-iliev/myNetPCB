package com.mynetpcb.pad.shape;

import com.mynetpcb.core.capi.Externalizable;
import com.mynetpcb.core.capi.layer.Layer;
import com.mynetpcb.core.capi.line.LinePoint;
import com.mynetpcb.core.capi.line.Trackable.ResumeState;
import com.mynetpcb.core.capi.print.PrintContext;
import com.mynetpcb.core.capi.shape.AbstractLine;
import com.mynetpcb.core.capi.undo.AbstractMemento;
import com.mynetpcb.core.capi.undo.MementoType;
import com.mynetpcb.core.capi.unit.Unit;
import com.mynetpcb.core.utils.Utilities;
import com.mynetpcb.d2.shapes.Point;
import com.mynetpcb.pad.unit.Footprint;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

import java.util.Arrays;
import java.util.Objects;
import java.util.StringTokenizer;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class Line extends AbstractLine implements Externalizable{
    public Line(int thickness,int layermaskId) {
        super(thickness, layermaskId);
        this.bendingPointDistance=Utilities.DISTANCE;
    }

    public Line clone() throws CloneNotSupportedException {
        Line copy = (Line) super.clone();
        copy.floatingStartPoint = new Point();
        copy.floatingMidPoint = new Point();
        copy.floatingEndPoint = new Point();
        copy.resizingPoint=null;
        copy.polyline=this.polyline.clone();
        return copy;
    }

    @Override
    public void setSide(Layer.Side side, com.mynetpcb.d2.shapes.Line line,double angle) {
        this.setCopper(Layer.Side.change(this.getCopper().getLayerMaskID()));
        this.mirror(line);
        this.rotate=angle;
    }

    @Override
    public void print(Graphics2D g2,PrintContext printContext,int layermask) {
        if((this.getCopper().getLayerMaskID()&layermask)==0){
            return;
        }

        g2.setStroke(new BasicStroke(thickness,1,1));    
        g2.setPaint(printContext.isBlackAndWhite()?Color.BLACK:copper.getColor());        
        
        this.polyline.paint(g2, false);      
        
    
    }
    @Override
    public String toXML() {
        StringBuffer sb = new StringBuffer();
        sb.append("<line copper=\"" + getCopper().getName() + "\" thickness=\"" + this.getThickness() + "\">");
        for (Point point : this.polyline.points) {
            sb.append(Utilities.roundDouble(point.x) + "," + Utilities.roundDouble(point.y) + ",");
        }
        sb.append("</line>\r\n");
        return sb.toString();
    }

    @Override
    public void fromXML(Node node) {
        Element element = (Element) node; 
        if(element.hasAttribute("copper")){
          this.setCopper(Layer.Copper.valueOf(element.getAttribute("copper")));    
        } 
       
        
        StringTokenizer st = new StringTokenizer(element.getTextContent(), ",");

        while (st.hasMoreTokens()) {
           this.add(new Point(Double.parseDouble(st.nextToken()), Double.parseDouble(st.nextToken())));
        }
        
        this.setThickness(Integer.parseInt(element.getAttribute("thickness")));

    }
    
    @Override
    public AbstractMemento getState(MementoType operationType) {
        AbstractMemento memento = new Memento(operationType);
        memento.saveStateFrom(this);
        return memento;
    }
    
    public static class Memento extends AbstractMemento<Footprint, Line> {

        private double Ax[];

        private double Ay[];
        private double rotate;
        private ResumeState resumeState;
        
        public Memento(MementoType mementoType) {
            super(mementoType);

        }

        @Override
        public void loadStateTo(Line shape) {
            super.loadStateTo(shape);
            shape.polyline.points.clear();
            for (int i = 0; i < Ax.length; i++) {
                shape.polyline.points.add(new LinePoint(Ax[i], Ay[i]));
            }
            shape.resumeState=resumeState;
            //***reset floating start point
            if (shape.polyline.points.size() > 0) {
                if(shape.getResumeState()==ResumeState.ADD_AT_END){
                  shape.floatingStartPoint.set(shape.polyline.points.get(shape.polyline.points.size() - 1));
                }else{
                  shape.floatingStartPoint.set(shape.polyline.points.get(0));  
                }
                shape.reset();
            }
        }

        @Override
        public void saveStateFrom(Line shape) {
            super.saveStateFrom(shape);
            Ax = new double[shape.polyline.points.size()];
            Ay = new double[shape.polyline.points.size()];
            for (int i = 0; i < shape.polyline.points.size(); i++) {
                Ax[i] = ((Point)shape.polyline.points.get(i)).x;
                Ay[i] = ((Point)shape.polyline.points.get(i)).y;
            }
            this.rotate=shape.rotate;
            this.resumeState=shape.resumeState; 
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
            return (super.equals(obj)&&Objects.equals(this.resumeState, other.resumeState)&&
                    Arrays.equals(Ax, other.Ax) && Arrays.equals(Ay, other.Ay));

        }

        @Override
        public int hashCode() {
            int  hash = super.hashCode()+Objects.hashCode(resumeState);
            hash += Arrays.hashCode(Ax);
            hash += Arrays.hashCode(Ay);
            return hash;
        }
        @Override
        public boolean isSameState(Unit unit) {
            Line line = (Line) unit.getShape(getUUID());
            return (line.getState(getMementoType()).equals(this));
        }
    }    
}
