package com.mynetpcb.symbol.shape;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.StringTokenizer;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.mynetpcb.core.capi.Externalizable;
import com.mynetpcb.core.capi.Resizeable;
import com.mynetpcb.core.capi.ViewportWindow;
import com.mynetpcb.core.capi.layer.Layer;
import com.mynetpcb.core.capi.print.PrintContext;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.undo.AbstractMemento;
import com.mynetpcb.core.capi.undo.MementoType;
import com.mynetpcb.core.capi.unit.Unit;
import com.mynetpcb.core.utils.Utilities;
import com.mynetpcb.d2.shapes.Box;
import com.mynetpcb.d2.shapes.Line;
import com.mynetpcb.d2.shapes.Point;
import com.mynetpcb.d2.shapes.Utils;
import com.mynetpcb.symbol.unit.Symbol;

public class Ellipse extends Shape implements Resizeable, Externalizable{
    private  com.mynetpcb.d2.shapes.Ellipse ellipse;
    private Point resizingPoint;
    
    public Ellipse(int thickness) {
                    super(thickness,Layer.LAYER_ALL);
                    this.setDisplayName("Ellipse");         
                    this.ellipse=new com.mynetpcb.d2.shapes.Ellipse(0,0,20,10);
                    //this.selectionRectWidth=8;
                    this.fillColor=Color.BLACK;
                    
    }
    @Override
    public Ellipse clone() throws CloneNotSupportedException {
        Ellipse copy=(Ellipse)super.clone();
        copy.resizingPoint=null;
        copy.ellipse=this.ellipse.clone();
        return copy;
    }
    @Override
    public long getClickableOrder() {        
        return (long)getBoundingShape().area();
    }
    @Override
    public Box getBoundingShape() {        
        return this.ellipse.box();                
    }
    @Override
    public boolean isClicked(double x,double y) {		    
    	if(this.fill==Fill.EMPTY){
    		return this.ellipse.isPointOn(new Point(x, y),this.thickness);
      	}else {
      	  return this.ellipse.contains(x, y);   	  
      	}
    	
    }
    public com.mynetpcb.d2.shapes.Ellipse getShape(){
        return ellipse;
    }
//    @Override
//    public Point isControlRectClicked(double x, double y) {
//        Point pt=new Point(x,y);        
//        for(Point v:this.ellipse.vertices()){        	
//            if(Utils.LE(pt.distanceTo(v),selectionRectWidth/2)){            	
//              return v;
//            }                        
//        };
//        return null;
//    }
    @Override
    public Point isControlRectClicked(double x, double y,ViewportWindow viewportWindow) {
        Point pt=new Point(x,y);
		pt.scale(getOwningUnit().getScalableTransformation().getCurrentTransformation().getScaleX());
		pt.move(-viewportWindow.getX(),- viewportWindow.getY());
        
        for(Point v:this.ellipse.vertices()){
        	var tmp=v.clone();
        		tmp.scale(getOwningUnit().getScalableTransformation().getCurrentTransformation().getScaleX());
        		tmp.move(-viewportWindow.getX(),- viewportWindow.getY());

            if(Utils.LE(pt.distanceTo(tmp),selectionRectWidth/2)){
              return v;
            }                        
        };
        return null;
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
    public void rotate(double angle,Point origin) {                      
        this.ellipse.pc.rotate(angle,origin);
        double w=this.ellipse.width;
        this.ellipse.width=this.ellipse.height;
        this.ellipse.height=w;               
    }
    @Override
    public void resize(double xoffset, double yoffset, Point clickedPoint) {
        this.resizingPoint=this.ellipse.resize(xoffset, yoffset,clickedPoint);
    }

    @Override
    public void alignResizingPointToGrid(Point point) {
        // TODO Implement this method
    }

    @Override
    public String toXML() {
        return "<ellipse x=\""+Utilities.roundDouble(this.ellipse.pc.x,1)+"\" y=\""+Utilities.roundDouble(this.ellipse.pc.y,1)+"\" width=\""+Utilities.roundDouble(this.ellipse.width,1)+"\" height=\""+Utilities.roundDouble(this.ellipse.height,1)+"\" thickness=\""+this.thickness+"\" fill=\""+this.fill.index+"\"/>\r\n";
    }
    
    @Override
    public void move(double xoffset, double yoffset) {
        this.ellipse.move(xoffset, yoffset);        
    }
    @Override
    public void mirror(Line line) {        
        this.ellipse.mirror(line);
    }
    @Override
    public void paint(Graphics2D g2, ViewportWindow viewportWindow, AffineTransform scale, int layermask) {
        Box rect = this.ellipse.box();
        rect.scale(scale.getScaleX());
        if (!rect.intersects(viewportWindow)) {
                return;
        }
        g2.setColor(isSelected() ? Color.GRAY : this.fillColor);
        
        com.mynetpcb.d2.shapes.Ellipse e=this.ellipse.clone();   
        e.scale(scale.getScaleX());
        e.move(-viewportWindow.getX(),- viewportWindow.getY());
        
        if (fill == Fill.EMPTY) { //framed
            double wireWidth = thickness * scale.getScaleX();
            g2.setStroke(new BasicStroke((float) wireWidth, 1, 1));
            //transparent rect
            e.paint(g2, false);
        }else if(fill==Fill.GRADIENT){ 
            GradientPaint gp = 
                new GradientPaint((float)e.box().getX(), (float)e.box().getY(), 
                                  Color.white, (float)e.box().getX(), 
                                  (float)(e.box().getY()+e.box().getHeight()), Color.gray, true);
            g2.setPaint(gp);
            e.paint(g2,true);
            g2.setColor(Color.black);
            e.paint(g2,false);
        }else { //filled
            e.paint(g2,true);
        }
        
        if (this.isSelected()) {
            Point pt=null;
            if(resizingPoint!=null){
                pt=resizingPoint.clone();
                pt.scale(scale.getScaleX());
                pt.move(-viewportWindow.getX(),- viewportWindow.getY());
            }
            for(Point p:e.vertices()){
              Utilities.drawCircle(g2,  pt,p); 
            }
        }        
    }
    @Override
    public void print(Graphics2D g2, PrintContext printContext, int layermask) {
        g2.setStroke(new BasicStroke(thickness, 1, 1));
        g2.setColor(Color.BLACK);  
        if (fill == Fill.EMPTY) { //framed            
            ellipse.paint(g2, false);
        }else if(fill==Fill.GRADIENT){ 
            GradientPaint gp = 
                new GradientPaint((float)ellipse.box().getX(), (float)ellipse.box().getY(), 
                                  Color.white, (float)ellipse.box().getX(), 
                                  (float)(ellipse.box().getY()+ellipse.box().getHeight()), Color.gray, true);
            g2.setPaint(gp);
            ellipse.paint(g2,true);
            g2.setColor(Color.black);
            ellipse.paint(g2,false);
        }         
        else { //filled
            ellipse.paint(g2,true);
        }
    }
    
    @Override
    public void fromXML(Node node) {        
        Element  element= (Element)node;                           
        if(element.hasAttribute("width")){  
            double x=(Double.parseDouble(element.getAttribute("x")));
            double y=(Double.parseDouble(element.getAttribute("y")));
            this.ellipse.pc.set(x,y);
            this.ellipse.width=Double.parseDouble(element.getAttribute("width"));
            this.ellipse.height=Double.parseDouble(element.getAttribute("height"));  
            this.setThickness(Integer.parseInt(element.getAttribute("thickness")));
            this.setFill(Fill.byIndex(element.getAttribute("fill")==""?1:Integer.parseInt(element.getAttribute("fill"))));  
        }else{
            StringTokenizer st=new StringTokenizer(node.getTextContent(),","); 
            double x=Double.parseDouble(st.nextToken());
            double y=Double.parseDouble(st.nextToken());
            double w=Double.parseDouble(st.nextToken());
            double h=Double.parseDouble(st.nextToken());
            
            this.ellipse.pc.set(x+w/2,y+h/2);
            this.ellipse.width=w/2;
            this.ellipse.height=h/2;
            
            
            this.thickness = Integer.parseInt(st.nextToken());
            setFill(Fill.byIndex(Byte.parseByte(st.nextToken()))); 
        }        
    }
    
    @Override
    public AbstractMemento getState(MementoType operationType) {
        Memento memento=new Memento(operationType);
        memento.saveStateFrom(this);        
        return memento;
    }
    
    public static class Memento extends AbstractMemento<Symbol,Ellipse> {
        
        private double rotate;
        private double x,y;
        private double width,height;
        
        public Memento(MementoType mementoType) {
            super(mementoType);
        }

        @Override
        public void loadStateTo(Ellipse shape) {
            super.loadStateTo(shape);
            shape.ellipse.pc.set(x,y);
            shape.ellipse.width=width;
            shape.ellipse.height=height;
            shape.ellipse.rotate=rotate;            
        }

        @Override
        public void saveStateFrom(Ellipse shape) {
            super.saveStateFrom(shape);
            this.x=shape.ellipse.pc.x;
            this.y=shape.ellipse.pc.y;
            this.width=shape.ellipse.width;
            this.height=shape.ellipse.height;
            this.rotate=shape.ellipse.rotate;                        
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
            return super.equals(obj) && Utils.EQ(this.rotate, other.rotate)&&
            Utils.EQ(this.x, other.x)&&Utils.EQ(this.y,other.y)&&    
            Utils.EQ(this.width, other.width)&&Utils.EQ(this.height,other.height);                
        }

        @Override
        public int hashCode() {
            int hash = 1;
            hash = super.hashCode();
            hash += Double.hashCode(this.rotate)+
                    Double.hashCode(this.x)+Double.hashCode(this.y)+                   
                    Double.hashCode(this.width)+Double.hashCode(this.height);                    
            return hash;
        }
        @Override
        public boolean isSameState(Unit unit) {
            boolean flag = super.isSameState(unit);
            Ellipse other = (Ellipse) unit.getShape(this.getUUID());
            return flag&&
            Utils.EQ(this.rotate, other.ellipse.rotate)&&Utils.EQ(this.x, other.ellipse.pc.x)&&    
            Utils.EQ(this.y, other.ellipse.pc.y)&&Utils.EQ(this.width, other.ellipse.width)&&Utils.EQ(this.height, other.ellipse.height);             
        }

    }

}
