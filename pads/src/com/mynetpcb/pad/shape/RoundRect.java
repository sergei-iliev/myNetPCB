package com.mynetpcb.pad.shape;

import com.mynetpcb.core.capi.Externalizable;
import com.mynetpcb.core.capi.Resizeable;
import com.mynetpcb.core.capi.ViewportWindow;
import com.mynetpcb.core.capi.gerber.Fillable;
import com.mynetpcb.core.capi.layer.Layer;
import com.mynetpcb.core.capi.print.PrintContext;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.shape.Shape.Fill;
import com.mynetpcb.core.capi.undo.AbstractMemento;
import com.mynetpcb.core.capi.undo.MementoType;
import com.mynetpcb.core.capi.unit.Unit;
import com.mynetpcb.core.utils.Utilities;
import com.mynetpcb.d2.shapes.Box;
import com.mynetpcb.d2.shapes.Line;
import com.mynetpcb.d2.shapes.Point;
import com.mynetpcb.d2.shapes.RoundRectangle;
import com.mynetpcb.d2.shapes.Utils;
import com.mynetpcb.pad.unit.Footprint;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import java.util.StringTokenizer;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class RoundRect extends Shape implements Resizeable,Fillable, Externalizable{
  
    private Point resizingPoint;
    private RoundRectangle roundRect;
    
    public RoundRect(double x,double y,double width,double height,int arc,int thickness,int layermaskid) {
        super(thickness,layermaskid);
        this.displayName="Rect";
        this.selectionRectWidth=3000;
        this.resizingPoint = null;
        this.rotate=0;  
        this.roundRect=new RoundRectangle(x,y,width,height,arc);   
        //this.roundRect.rotate(30, this.roundRect.box().getCenter());
    }
    public RoundRect() {
        this(0, 0, 0, 0, 0, 0, Layer.SILKSCREEN_LAYER_FRONT);
    }
    @Override
    public void clear() {      
       roundRect=null;
    }
    @Override
    public RoundRect clone()throws CloneNotSupportedException {
            RoundRect copy = (RoundRect)super.clone();
            copy.roundRect = this.roundRect.clone();                        
            return copy;
    }
    @Override
    public long getClickableOrder(){
            return (long)this.roundRect.area(); 
    }
    @Override
    public Point isControlRectClicked(double x, double y) {
                    Point pt=new Point(x,y);                    
                    for(Point p:this.roundRect.points){
                        if(Utils.LE(pt.distanceTo(p),this.selectionRectWidth/2)){                                  
                            return p;
                         }
                    }
                    return null;
    }
    
    public RoundRectangle getShape(){
        return roundRect;
    }
    
    @Override
    public Box getBoundingShape(){
        return this.roundRect.box();              
    }
    
    public void setRounding(int rounding){    
      this.roundRect.setRounding(rounding);
    }
    public int getRounding(){    
      return this.roundRect.rounding;
    }
    
    @Override
    public void resize(double x, double y, Point point) {
        this.roundRect.resize(x, y,point);
    }
    

    @Override
    public void alignResizingPointToGrid(Point targetPoint) {
        Point point=this.getOwningUnit().getGrid().positionOnGrid(targetPoint.x,targetPoint.y);  
        this.resize((int)(point.x -targetPoint.x),(int)(point.y-targetPoint.y),targetPoint); 
    }    
    @Override
    public void move(double xoffset,double yoffset) {
        this.roundRect.move(xoffset,yoffset);
    }
    @Override
    public void setSide(Layer.Side side, com.mynetpcb.d2.shapes.Line line,double angle) {
        this.setCopper(Layer.Side.change(this.getCopper().getLayerMaskID()));
        this.mirror(line);
        this.rotate=angle;
    }    
    @Override
    public void mirror(Line line) {        
        this.roundRect.mirror(line);        
    }
    @Override
    public void paint(Graphics2D g2, ViewportWindow viewportWindow, AffineTransform scale, int layermask) {
        if((this.getCopper().getLayerMaskID()&layermask)==0){
            return;
        }
        
        Box rect = this.roundRect.box();
        rect.scale(scale.getScaleX());
        if (!rect.intersects(viewportWindow)) {
            return;
        }
        
        g2.setColor(isSelected() ? Color.GRAY : copper.getColor());
        
        RoundRectangle r=this.roundRect.clone();   
        r.scale(scale.getScaleX());
        r.move(-viewportWindow.getX(),- viewportWindow.getY());
        
        if (fill == Fill.EMPTY) { //framed
            double wireWidth = thickness * scale.getScaleX();
            g2.setStroke(new BasicStroke((float) wireWidth, 1, 1));
            //transparent rect
            r.paint(g2, false);
        } else { //filled
            //AlphaComposite composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f);   
            //Composite originalComposite = g2.getComposite();                     
            //g2.setComposite(composite ); 
            r.paint(g2,true);
            //g2.setComposite(originalComposite);
        }

        
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
    public void print(Graphics2D g2,PrintContext printContext,int layermask) {
        if((this.getCopper().getLayerMaskID()&layermask)==0){
            return;
        }

        g2.setStroke(new BasicStroke(thickness,1,1));    
        g2.setPaint(printContext.isBlackAndWhite()?Color.BLACK:copper.getColor());        
        
        if(this.fill==Fill.EMPTY){
          this.roundRect.paint(g2, false);      
        }else{
          this.roundRect.paint(g2, true);      
        }    
        
    
    }
    @Override
    public boolean isClicked(double x, double y) {
            return this.roundRect.isPointOn(new Point(x, y),this.thickness);
                    
    }
    public void setRotation(double rotate,Point center){
            double alpha=rotate-this.rotate;
            this.roundRect.rotate(alpha,center);                      
            this.rotate=rotate;
    }
    @Override
    public void rotate(double angle,Point origin) {        
        //fix angle
        double alpha=this.rotate+angle;
        if(alpha>=360){
                alpha-=360;
        }
        if(alpha<0){
         alpha+=360; 
        }       
        this.rotate=alpha;              
        this.roundRect.rotate(angle,origin);        
        
    }
    public void setResizingPoint(Point pt){
            this.resizingPoint=pt;
    }
    public Point getResizingPoint() {
            return this.resizingPoint;
    }
    public Point getCenter() {
        Box box=this.roundRect.box();
        return box.getCenter();
    }
    @Override
    public void setSelected(boolean selection) {
        super.setSelected(selection);
        if (!selection) {
            resizingPoint = null;
        }
    }
    @Override
    public String toXML() {
        StringBuffer sb = new StringBuffer();
        sb.append("<rectangle copper=\"" + getCopper().getName() + "\" thickness=\"" + this.getThickness()+"\"");
        sb.append(" fill=\"" + this.getFill().index + "\" arc=\"" + this.roundRect.rounding + "\" points=\"");
        for (Point point : this.roundRect.points) {
            sb.append(Utilities.roundDouble(point.x) + "," + Utilities.roundDouble(point.y) + ",");
        }
        sb.append("\"></rectangle>\r\n");
        return sb.toString();
    }
    @Override
    public void fromXML(Node node){
        Element element = (Element) node;
        if (element.hasAttribute("copper")) {
            this.setCopper(Layer.Copper.valueOf(element.getAttribute("copper")));
        }
      
        if(element.getAttribute("width").length()>0){
            this.roundRect.setRect(Integer.parseInt(element.getAttribute("x")),Integer.parseInt(element.getAttribute("y")),Integer.parseInt(element.getAttribute("width")),Integer.parseInt(element.getAttribute("height")),Integer.parseInt(element.getAttribute("arc"))/2);  
        }else{
            StringTokenizer st = new StringTokenizer(element.getAttribute("points"), ",");
             
            this.roundRect.points.clear();
            
            while (st.hasMoreTokens()) {
                this.roundRect.points.add(new Point(Double.parseDouble(st.nextToken()), Double.parseDouble(st.nextToken())));
            }
                        
            this.roundRect.setRounding(Integer.parseInt(element.getAttribute("arc")));           
        }
        
        this.setThickness(Integer.parseInt(element.getAttribute("thickness")));

//        this.setFill(Fill.values()[(element.getAttribute("fill") == "" ? 0 :
//                                            Integer.parseInt(element.getAttribute("fill")))]);   
        this.setFill(Fill.byIndex(Integer.parseInt(element.getAttribute("fill"))==0?1:Integer.parseInt(element.getAttribute("fill"))));
    
    }
    @Override
    public AbstractMemento getState(MementoType operationType) {
        AbstractMemento memento = new Memento(operationType);
        memento.saveStateFrom(this);
        return memento;
    }
    



    public static class Memento extends AbstractMemento<Footprint,RoundRect> {
        private double rotate;
        private int rounding;
        private double x1,x2,x3,x4;
        private double y1,y2,y3,y4;
        
        public Memento(MementoType mementoType) {
            super(mementoType);
        }

        @Override
        public void loadStateTo(RoundRect shape) {
            super.loadStateTo(shape);
            shape.rotate = rotate;
            shape.roundRect.points.get(0).set(x1, y1);
            shape.roundRect.points.get(1).set(x2, y2);
            shape.roundRect.points.get(2).set(x3, y3);
            shape.roundRect.points.get(3).set(x4, y4);
            shape.roundRect.setRounding(rounding);
        }

        @Override
        public void saveStateFrom(RoundRect shape) {
            super.saveStateFrom(shape);
            this.rotate = shape.rotate;
            this.x1=shape.roundRect.points.get(0).x;
            this.y1=shape.roundRect.points.get(0).y;
            this.x2=shape.roundRect.points.get(1).x;
            this.y2=shape.roundRect.points.get(1).y;
            this.x3=shape.roundRect.points.get(2).x;
            this.y3=shape.roundRect.points.get(2).y;
            this.x4=shape.roundRect.points.get(3).x;
            this.y4=shape.roundRect.points.get(3).y;   
            this.rounding=shape.roundRect.rounding;
            
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
            return super.equals(obj) && Utils.EQ(this.rotate, other.rotate)&&(this.rounding==other.rounding)&&
            Utils.EQ(this.x1, other.x1)&&Utils.EQ(this.y1,other.y1)&&    
            Utils.EQ(this.x2, other.x2)&&Utils.EQ(this.y2,other.y2)&&    
            Utils.EQ(this.x3, other.x3)&&Utils.EQ(this.y3,other.y3)&&    
            Utils.EQ(this.x4, other.x4)&&Utils.EQ(this.y4,other.y4);   
        }

        @Override
        public int hashCode() {
            int hash = 1;
            hash = super.hashCode();
            hash += Double.hashCode(this.rotate)+this.rounding+
                    Double.hashCode(this.x1)+Double.hashCode(this.y1)+
                    Double.hashCode(this.x2)+Double.hashCode(this.y2)+
                    Double.hashCode(this.x3)+Double.hashCode(this.y3)+
                    Double.hashCode(this.x4)+Double.hashCode(this.y4);
            return hash;
        }
        @Override
        public boolean isSameState(Unit unit) {
            boolean flag = super.isSameState(unit);
            RoundRect other = (RoundRect) unit.getShape(this.getUUID());
            return flag&&Utils.EQ(this.rotate, other.rotate)&&(this.rounding==other.roundRect.rounding)&&
            Utils.EQ(this.x1, other.roundRect.points.get(0).x)&&Utils.EQ(this.y1, other.roundRect.points.get(0).y)&&    
            Utils.EQ(this.x2, other.roundRect.points.get(1).x)&&Utils.EQ(this.y2, other.roundRect.points.get(1).y)&& 
            Utils.EQ(this.x3, other.roundRect.points.get(2).x)&&Utils.EQ(this.y3, other.roundRect.points.get(2).y)&& 
            Utils.EQ(this.x4, other.roundRect.points.get(3).x)&&Utils.EQ(this.y4, other.roundRect.points.get(3).y);
        }
    }    
}
