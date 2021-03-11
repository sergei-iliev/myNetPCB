package com.mynetpcb.symbol.shape;

import com.mynetpcb.core.capi.Externalizable;
import com.mynetpcb.core.capi.ViewportWindow;
import com.mynetpcb.core.capi.layer.Layer;
import com.mynetpcb.core.capi.line.LinePoint;
import com.mynetpcb.core.capi.print.PrintContext;
import com.mynetpcb.core.capi.shape.AbstractLine;
import com.mynetpcb.core.capi.undo.AbstractMemento;
import com.mynetpcb.core.capi.undo.MementoType;
import com.mynetpcb.core.capi.unit.Unit;
import com.mynetpcb.core.utils.Utilities;
import com.mynetpcb.d2.shapes.Box;
import com.mynetpcb.d2.shapes.Point;
import com.mynetpcb.d2.shapes.Polyline;
import com.mynetpcb.symbol.unit.Symbol;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import java.util.Arrays;
import java.util.Objects;
import java.util.StringTokenizer;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class Line  extends AbstractLine implements Externalizable {

    public Line(int thickness) {
        super(thickness, Layer.LAYER_NONE);
        this.selectionRectWidth=2;
        this.fillColor=Color.BLACK;
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
    public Point alignToGrid(boolean isRequired) {
    if (isRequired) {
      this.polyline.points.forEach(wirePoint->{
          Point point = this.getOwningUnit().getGrid().positionOnGrid(wirePoint.x, wirePoint.y);
          wirePoint.set(point.x,point.y);
      });
    }
    return null;
    }
    @Override
    public void paint(Graphics2D g2, ViewportWindow viewportWindow, AffineTransform scale, int layermask) {

        
        Box rect = this.polyline.box();
        rect.scale(scale.getScaleX());           
        if (!this.isFloating()&& (!rect.intersects(viewportWindow))) {
                return;
        }
        g2.setColor(isSelected() ? Color.GRAY :fillColor);
        
        Polyline r=this.polyline.clone();   
        
        // draw floating point
        if (this.isFloating()) {                                                    
            if(this.getResumeState()==ResumeState.ADD_AT_FRONT){                
                Point p = this.floatingEndPoint.clone();
                r.points.add(0,p);                
            }else{
                            
                Point p = this.floatingEndPoint.clone();
                r.add(p);                                
            }            
        }
        
        r.scale(scale.getScaleX());
        r.move(-viewportWindow.getX(),- viewportWindow.getY());
        
        double wireWidth = thickness * scale.getScaleX();
        g2.setStroke(new BasicStroke((float) wireWidth, 1, 1));

        //transparent rect
        r.paint(g2, false);
        
        if (this.isSelected()&&isControlPointVisible) {
            Point pt=null;
            if(resizingPoint!=null){
                pt=resizingPoint.clone();
                pt.scale(scale.getScaleX());
                pt.move(-viewportWindow.getX(),- viewportWindow.getY());
            }
            for(Object p:r.points){
              Utilities.drawCrosshair(g2,  pt,(int)(selectionRectWidth*scale.getScaleX()),(Point)p); 
            }
        }        
        
    }
    @Override
    public void print(Graphics2D g2, PrintContext printContext, int layermask) {        
        g2.setStroke(new BasicStroke(thickness));
        g2.setColor(Color.BLACK);
        
        this.polyline.paint(g2,false);  
    }
    @Override
    public String toXML() {
        StringBuffer sb = new StringBuffer();
        sb.append("<line  thickness=\"" + this.getThickness() + "\">");
        for (Point point : this.polyline.points) {
            sb.append(Utilities.roundDouble(point.x,1) + "," + Utilities.roundDouble(point.y,1) + ",");
        }
        sb.append("</line>\r\n");
        return sb.toString();
    }

    @Override
    public void fromXML(Node node) {
        Element element = (Element) node; 
        
        StringTokenizer st = new StringTokenizer(node.getTextContent(), ",");
        int counter=st.countTokens()-1;
        while(st.hasMoreTokens()){
          this.add(Double.parseDouble(st.nextToken()),Double.parseDouble(st.nextToken()));  
          counter-=2;
          if(counter==0)
              break;
        }   
        if(element.hasAttribute("thickness")){
            this.setThickness(Integer.parseInt(element.getAttribute("thickness")));   
        }else{ 
            setThickness(Integer.parseInt(st.nextToken()));
        }
        

    }
    @Override
    public AbstractMemento getState(MementoType operationType) {
        AbstractMemento memento = new Memento(operationType);
        memento.saveStateFrom(this);
        return memento;
    }
    
    public static class Memento extends AbstractMemento<Symbol, Line> {

        private double Ax[];

        private double Ay[];
        
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
