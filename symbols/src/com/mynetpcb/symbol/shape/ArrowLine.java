package com.mynetpcb.symbol.shape;

import com.mynetpcb.core.capi.Externalizable;
import com.mynetpcb.core.capi.Resizeable;
import com.mynetpcb.core.capi.ViewportWindow;
import com.mynetpcb.core.capi.layer.Layer;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.undo.AbstractMemento;
import com.mynetpcb.core.capi.undo.MementoType;
import com.mynetpcb.core.utils.Utilities;
import com.mynetpcb.d2.shapes.Box;
import com.mynetpcb.d2.shapes.Point;
import com.mynetpcb.d2.shapes.Polygon;
import com.mynetpcb.d2.shapes.Segment;
import com.mynetpcb.d2.shapes.Utils;
import com.mynetpcb.symbol.unit.Symbol;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import java.util.StringTokenizer;

import org.w3c.dom.Node;

public class ArrowLine extends Shape implements Resizeable,Externalizable {
    private Point resizingPoint;
    private Segment line;
    private Polygon arrow;
    private int headSize;
    
    public ArrowLine(int thickness) {
            super(thickness,Layer.LAYER_ALL);
	    this.setDisplayName("Arrow");		
	    this.selectionRectWidth=4;
	    this.resizingPoint = null;	
	    this.line=new Segment();
            this.line.set(0,0,20,20);
	    this.arrow=new Polygon();
	    this.arrow.points.add(new Point(0,0));
            this.arrow.points.add(new Point(0,0));
            this.arrow.points.add(new Point(0,0));
            
	    this.headSize=3;	    
	    this.setHeadSize(3);
   }
   @Override
   public ArrowLine clone()throws CloneNotSupportedException{
        ArrowLine copy=(ArrowLine)super.clone();
        copy.resizingPoint=null;
        copy.headSize=this.headSize;
        copy.line=this.line.clone();
        copy.arrow=this.arrow.clone();    
        return copy;
   }
   public int getHeadSize(){
       return this.headSize;
   }
    
   public void setHeadSize(int headSize) {
        this.headSize = headSize;
        this.arrow.points.get(0).set(this.line.pe.x,this.line.pe.y);
        this.arrow.points.get(1).set(this.line.pe.x-2*headSize,this.line.pe.y -headSize);
        this.arrow.points.get(2).set(this.line.pe.x-2*headSize,this.line.pe.y+headSize);  
        double angle = Math.atan2(((this.line.pe.y) - (this.line.ps.y)),((this.line.pe.x - this.line.ps.x)));
        double deg=-1*Utils.degrees(angle);
        this.arrow.rotate(deg,this.line.pe);
    }
   
    @Override
    public Point isControlRectClicked(int x, int y) {
        Box rect = Box.fromRect(x-this.selectionRectWidth / 2, y - this.selectionRectWidth/ 2, this.selectionRectWidth, this.selectionRectWidth);        
        if (rect.contains(this.line.ps)){
           return this.line.ps;  
        }else if(rect.contains(this.line.pe)) {                                 
           return this.line.pe;
        }else{
           return null;
        }
    }
    @Override
    public Box getBoundingShape(){
        return this.line.box();           
    }
    @Override
    public Point getResizingPoint() {        
        return resizingPoint;
    }

    @Override
    public void setResizingPoint(Point point) {
        this.resizingPoint=point;
    }

    @Override
    public void resize(int xoffset, int yoffset, Point clickedPoint) {
        clickedPoint.set(clickedPoint.x + xoffset, 
                                 clickedPoint.y + yoffset);
        this.setHeadSize(this.headSize);
    }

    @Override
    public void alignResizingPointToGrid(Point point) {
        // TODO Implement this method
    }

    @Override
    public void rotate(double angle,Point origin){               
            this.arrow.rotate(angle,origin );
            this.line.rotate(angle,origin);
    }
    @Override
    public void move(double xoffset,double yoffset) {
            this.line.move(xoffset,yoffset);
            this.arrow.move(xoffset,yoffset);
    }
    
    @Override
    public void paint(Graphics2D g2, ViewportWindow viewportWindow, AffineTransform scale, int layermask) {
        Box rect = this.line.box();
        rect.scale(scale.getScaleX());
        if (!rect.intersects(viewportWindow)) {
                return;
        }

        g2.setColor(isSelected() ? Color.GRAY : this.fillColor);
        double wireWidth = thickness * scale.getScaleX();
        g2.setStroke(new BasicStroke((float) wireWidth, 1, 1));
        
        Segment l=this.line.clone();
        l.pe.set((this.arrow.points.get(1).x + this.arrow.points.get(2).x)/2, (this.arrow.points.get(1).y + this.arrow.points.get(2).y)/2);
        l.scale(scale.getScaleX());
        l.move(-viewportWindow.getX(),- viewportWindow.getY());
        l.paint(g2,false);
        
        Polygon a=this.arrow.clone();       
        a.scale(scale.getScaleX());
        a.move(-viewportWindow.getX(),- viewportWindow.getY());
        
        
        if (fill == Fill.EMPTY) { //framed
            //transparent rect
            a.paint(g2,false);
        } else { //filled
            a.paint(g2,true);
        }                
        if (this.isSelected()) {
            Point pt=null;
            if(resizingPoint!=null){
                pt=resizingPoint.clone();
                pt.scale(scale.getScaleX());
                pt.move(-viewportWindow.getX(),- viewportWindow.getY());
            }
            Utilities.drawCrosshair(g2,  pt,(int)(selectionRectWidth*scale.getScaleX()),l.ps,a.points.get(0));             
        }
    }
    @Override
    public String toXML() {
        return "<arrow thickness=\"" + this.thickness + "\" fill=\"" + this.fill + "\"  head=\"" + this.headSize+ "\">" + Utilities.roundDouble(this.line.ps.x,1) + "," + Utilities.roundDouble(this.line.ps.y,1) + "," + Utilities.roundDouble(this.line.pe.x,1) + "," + Utilities.roundDouble(this.line.pe.y,1) + "</arrow>";
    }

    @Override
    public void fromXML(Node node) {
        StringTokenizer st=new StringTokenizer(node.getTextContent(),",");      
        this.line.ps.set(Double.parseDouble(st.nextToken()),Double.parseDouble(st.nextToken()));      
        this.line.pe.set(Double.parseDouble(st.nextToken()),Double.parseDouble(st.nextToken()));
        this.thickness=Integer.parseInt(st.nextToken());
        this.setHeadSize(Integer.parseInt(st.nextToken()));
        setFill(Fill.byIndex(Byte.parseByte(st.nextToken())));    
    }
    @Override
    public AbstractMemento getState(MementoType operationType) {
        Memento memento = new Memento(operationType);
        memento.saveStateFrom(this);
        return memento;
    }


    public static class Memento extends AbstractMemento<Symbol,ArrowLine> {
        private int headSize;        
        private double x1,y1,x2,y2;
        
        
        public Memento(MementoType mementoType) {
            super(mementoType);
        }

        public void saveStateFrom(ArrowLine shape) {
            super.saveStateFrom(shape);
            this.headSize = shape.headSize;
            this.x1=shape.line.ps.x;
            this.y1=shape.line.ps.y;
            this.x2=shape.line.pe.x;
            this.y2=shape.line.pe.y;
        }

        public void loadStateTo(ArrowLine shape) {
            super.loadStateTo(shape);
            shape.headSize = headSize;
            shape.line.ps.set(x1, y1);
            shape.line.pe.set(x2,y2);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof Memento)) {
                return false;
            }
            Memento other = (Memento)obj;
            
            return super.equals(obj) && Utils.EQ(this.headSize, other.headSize)&&
                Utils.EQ(this.x1, other.x1)&&Utils.EQ(this.y1,other.y1)&&    
                Utils.EQ(this.x2, other.x2)&&Utils.EQ(this.y2,other.y2);   
        }

        @Override
        public int hashCode() {
            int hash = 1;
            hash = super.hashCode();
            hash += this.headSize+
                    Double.hashCode(this.x1)+Double.hashCode(this.y1)+
                    Double.hashCode(this.x2)+Double.hashCode(this.y2);                   
            return hash;            
        }

        public boolean isSameState(Symbol unit) {
            boolean flag = super.isSameState(unit);
            ArrowLine other = (ArrowLine) unit.getShape(this.getUUID());
            return flag&&(this.headSize==other.headSize)&&
            Utils.EQ(this.x1, other.line.ps.x)&&Utils.EQ(this.y1, other.line.ps.y)&&    
            Utils.EQ(this.x2, other.line.pe.x)&&Utils.EQ(this.y2, other.line.pe.y);          
        }
    }    
    
}
