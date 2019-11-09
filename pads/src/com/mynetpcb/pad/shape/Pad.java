package com.mynetpcb.pad.shape;

import com.mynetpcb.core.capi.Grid;
import com.mynetpcb.core.capi.ViewportWindow;
import com.mynetpcb.core.capi.print.PrintContext;
import com.mynetpcb.core.capi.text.Texture;
import com.mynetpcb.core.capi.text.font.FontTexture;
import com.mynetpcb.core.capi.undo.AbstractMemento;
import com.mynetpcb.core.capi.undo.MementoType;
import com.mynetpcb.core.pad.shape.PadDrawing;
import com.mynetpcb.core.pad.shape.PadShape;
import com.mynetpcb.d2.shapes.Box;
import com.mynetpcb.d2.shapes.Point;
import com.mynetpcb.pad.shape.pad.CircularShape;
import com.mynetpcb.pad.shape.pad.OvalShape;
import com.mynetpcb.pad.shape.pad.PolygonShape;
import com.mynetpcb.pad.shape.pad.RectangularShape;
import com.mynetpcb.pad.unit.Footprint;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import org.w3c.dom.Node;

/**
 *Pad is a composite shape consisting of circle,ellipse,rectangle combination
 */
public class Pad extends PadShape{
    
    private Drill drill;

    private PadDrawing shape;

    private PadShape.Type type;

    private double width,height;
    
    private FontTexture number,netvalue; 
    
    public Pad(double width,double height) { 
        this.width=width;
        this.height=height;
        this.rotate=0;
        this.displayName="Pad";
        this.shape=new PolygonShape(0,0,width,this);
        this.setType(PadShape.Type.THROUGH_HOLE);  
        
        this.number=new FontTexture("number","1",0,0,4000);
        this.netvalue=new FontTexture("netvalue","",0,0,4000);  
    }
    public Pad clone() throws CloneNotSupportedException {
        Pad copy = (Pad) super.clone();
        copy.setType(this.getType());
        copy.rotate=this.rotate;
        copy.shape=this.shape.copy(copy);        
        //copy.offset=new Point(this.offset.x,this.offset.y);

        copy.number = this.number.clone();
        copy.netvalue = this.netvalue.clone();
        if (drill != null) {
            copy.drill = drill.clone();
        }
        return copy;
    }   
    
    @Override
    public Point alignToGrid(boolean isRequired) {        
        Point center=this.getCenter();
        Point point=this.getOwningUnit().getGrid().positionOnGrid(this.getCenter());
        this.move(point.x - center.x,point.y - center.y);
        return null;                 
    }
    
    @Override
    public Point getPinPoint() {        
        return this.shape.getCenter();
    }
    
    @Override
    public Point getCenter(){
        return this.shape.getCenter();
    }
    public void setType(PadShape.Type type) {
                    this.type = type;
                    switch(type){
                    case THROUGH_HOLE:
                        if(this.drill==null){
                            this.drill=new Drill(this.shape.getCenter().x,this.shape.getCenter().y,Grid.MM_TO_COORD(0.8));                                            
                        }
                        break;
                    case SMD:
                            this.drill=null;
                        break;
                            }
    }
    public void setShape(Shape shape) {
        switch (shape) {
        case CIRCULAR:
            this.shape = new CircularShape(this.shape.getCenter().x,this.shape.getCenter().y,this.width,this);
            break;
        case OVAL:
           this.shape=new OvalShape(this.shape.getCenter().x,this.shape.getCenter().y,this.width,this.height,this);
            break;
        case RECTANGULAR:
            this.shape = new RectangularShape(this.shape.getCenter().x,this.shape.getCenter().y,this.width,this.height,this);
            break;
        case POLYGON:
            this.shape = new PolygonShape(this.shape.getCenter().x,this.shape.getCenter().y,this.width,this);
            break;
        }
    }  
    public double getWidth(){
        return width;
    }
    public double getHeight(){
        return height;
    }
    
    public void setWidth(double width){
                    this.width=width;
                    this.shape.setSize(width,height);    
    }
    
    public void setHeight(double height){
                    this.height=height;
                    this.shape.setSize(width,height);   
    }    
    public void setRotation(double rotate){
        double alpha=rotate-this.rotate;   
        
          this.shape.rotate(alpha,this.shape.getCenter());
          
          this.number.setRotation(rotate,this.shape.getCenter());
          this.netvalue.setRotation(rotate,this.shape.getCenter());
          
          if(this.drill!=null){
                this.drill.rotate(alpha,this.shape.getCenter());
          }               
        
        this.rotate=rotate;        
    }
    @Override
    public void rotate(double rotate, Point pt) {
        //fix angle
        double alpha=this.rotate+rotate;
        if(alpha>=360){
                alpha-=360;
        }
        if(alpha<0){
         alpha+=360; 
        }       
        this.rotate=alpha;
        //rotate anchor point
        this.shape.rotate(rotate,pt);
                 
        if(this.drill!=null){
         this.drill.rotate(rotate,pt);
        }
    }
    public void move(double xoffset,double yoffset){
               this.shape.move(xoffset, yoffset);
               
               if(this.drill!=null){
                 this.drill.move(xoffset, yoffset);
               }
               this.number.move(xoffset,yoffset);
               this.netvalue.move(xoffset,yoffset);
    }
    @Override
    public void print(Graphics2D g2, PrintContext printContext, int layermask) {
        switch (type) {
        case THROUGH_HOLE:case CONNECTOR:
            shape.print(g2, printContext, layermask);
            if (drill != null) {
                drill.print(g2, printContext, layermask);
            }
            break;
        case SMD:
            shape.print(g2, printContext, layermask);
            break;
        }
    }
    @Override
    public void paint(Graphics2D g2, ViewportWindow viewportWindow, AffineTransform scale, int layermask) {
        switch(this.type){
            case THROUGH_HOLE:
                if(this.shape.paint(g2, viewportWindow, scale)){
                 if(this.drill!=null){
                    this.drill.paint(g2, viewportWindow, scale,layermask);
                 }
                }
                break;
            case SMD:
                this.shape.paint(g2, viewportWindow, scale);
                break;
            
            }
            this.number.paint(g2, viewportWindow, scale,0);
            this.netvalue.paint(g2, viewportWindow, scale,0);
     } 
    public Drill getDrill(){
        return drill;
    }
    @Override
    public Box getBoundingShape() {        
        return this.shape.getBoundingShape();
    }
    
    @Override
    public boolean isClicked(int x,int y){
        return this.shape.contains(new Point(x,y));         
    }
    public void setSelected (boolean selection) {
        super.setSelected(selection);
        this.number.setSelected(selection);
        this.netvalue.setSelected(selection);
    }
    @Override
    public PadShape.Shape getShape() {

            if(this.shape instanceof CircularShape)
                return PadShape.Shape.CIRCULAR;
            if(this.shape instanceof RectangularShape)
                return PadShape.Shape.RECTANGULAR;
            if(this.shape instanceof OvalShape)
                return PadShape.Shape.OVAL;
            if(this.shape instanceof PolygonShape)
                return PadShape.Shape.POLYGON;                
        return null;
    }
    
    @Override
    public AbstractMemento getState(MementoType operationType) {
        AbstractMemento memento = new Memento(operationType);
        memento.saveStateFrom(this);
        return memento;
    }
    
    @Override
    public PadShape.Type getType() {        
        return type;
    }

    @Override
    public Texture getClickedTexture(int x, int y) {
        if(number.isClicked(x, y))
            return number;
        else if(netvalue.isClicked(x, y))
            return netvalue;
        else
        return null;
    }

    @Override
    public boolean isClickedTexture(int x, int y) {
        return getClickedTexture(x, y)!=null;
    }

    @Override
    public Texture getTextureByTag(String tag) {
        if(tag.equals(number.getTag()))
            return number;
        else if(tag.equals(netvalue.getTag()))
            return netvalue;
        else
        return null;
    }
    @Override
    public String toXML() {
        // TODO Implement this method
        return null;
    }

    @Override
    public void fromXML(Node node){
        // TODO Implement this method

    }
    

    
    public static class Memento extends AbstractMemento<Footprint, Pad> {

        //private ChipText.Memento labelTextMemento;

        //private Drill.Memento drillMemento;

        private int x, y, width, height, arc;

        private int shape;

        private int type;

        private int offsetx;

        private int offsety;

        public Memento(MementoType mementoType) {
            super(mementoType);
            //labelTextMemento = new ChipText.Memento();
            //drillMemento = new Drill.Memento(mementoType);
        }

        @Override
        public void clear() {
            super.clear();
            //labelTextMemento.Clear();
            //drillMemento.Clear();
        }

        @Override
        public void loadStateTo(Pad shape) {
            super.loadStateTo(shape);
//            shape.setX(x);
//            shape.setY(y);
//            shape.setWidth(width);
//            shape.setHeight(height);
//            shape.arc = arc;
//            shape.offset.x = offsetx;
//            shape.offset.y = offsety;
//            shape.setType(Type.values()[type]);
//            shape.setShape(Shape.values()[this.shape]);
//            labelTextMemento.loadStateTo(shape.getChipText());
//            drillMemento.loadStateTo(shape.getDrill());
        }

        @Override
        public void saveStateFrom(Pad shape) {
            super.saveStateFrom(shape);
//            x = shape.getX();
//            y = shape.getY();
//            width = shape.getWidth();
//            height = shape.getHeight();
//            arc = shape.arc;
//            offsetx = shape.offset.x;
//            offsety = shape.offset.y;
//            this.shape = shape.getShape().ordinal();
//            type = shape.getType().ordinal();
//            labelTextMemento.saveStateFrom(shape.getChipText());
//            drillMemento.saveStateFrom(shape.getDrill());
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

            return super.equals(obj);

        }

        @Override
        public int hashCode() {
            int hash = getUUID().hashCode();
            hash +=
                getMementoType().hashCode() + x + y + width + height + arc + shape + type + offsetx + offsety +
                layerindex;

            return hash;
        }

        @Override
        public boolean isSameState(Footprint unit) {
            Pad pad = (Pad) unit.getShape(getUUID());
            return (pad.getState(getMementoType()).equals(this));
        }

    }
}
