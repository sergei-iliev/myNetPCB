package com.mynetpcb.pad.shape;

import com.mynetpcb.core.capi.Externalizable;
import com.mynetpcb.core.capi.Resizeable;
import com.mynetpcb.core.capi.ViewportWindow;
import com.mynetpcb.core.capi.flyweight.FlyweightProvider;
import com.mynetpcb.core.capi.flyweight.ShapeFlyweightFactory;
import com.mynetpcb.core.capi.gerber.ArcGerberable;
import com.mynetpcb.core.capi.print.PrintContext;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.undo.AbstractMemento;
import com.mynetpcb.core.capi.undo.MementoType;
import com.mynetpcb.core.capi.unit.Unit;
import com.mynetpcb.core.pad.Layer;
import com.mynetpcb.core.utils.Utilities;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

/*
 * Arc is described by center, radius, start and end angle
 */
public class Arc  extends Shape implements ArcGerberable, Resizeable,Externalizable {
    private double startAngle,extendAngle;
    
    public Arc(int x,int y,int r,int thickness,int layermaskid) {
        super(x, y, r,r,thickness,layermaskid);  
        this.startAngle=90;
        this.extendAngle=-230;
        this.selectionRectWidth=3000;
    }
    
    public Arc(){
        this(0,0,0,0,Layer.SILKSCREEN_LAYER_FRONT);
    }
    
    @Override
    public Arc clone() throws CloneNotSupportedException{
        Arc copy= (Arc)super.clone();        
        return copy;
    }
    
    @Override
    public Point alignToGrid(boolean isRequired) {
        if(isRequired){
          return super.alignToGrid(isRequired);
        }else{
            return null;
        }
    }
    
    @Override
    public void alignResizingPointToGrid(Point point) {          
        width=getOwningUnit().getGrid().positionOnGrid(width);                
    }
    
    @Override
    public String getDisplayName(){
        return "Arc";
    }
    @Override
    public java.awt.Shape calculateShape() {    
      return new Rectangle(getX()-getWidth(),getY()-getWidth(),2*getWidth(),2*getWidth());
    }
    @Override
    public boolean isClicked(int x, int y) {
        FlyweightProvider rectProvider=ShapeFlyweightFactory.getProvider(Rectangle2D.class);
        Rectangle2D rect=(Rectangle2D)rectProvider.getShape();
        rect.setFrame(x-(selectionRectWidth/2), y-(selectionRectWidth/2),selectionRectWidth, selectionRectWidth);
        
        Arc2D arc= new Arc2D.Double(getX()-getWidth(),getY()-getWidth(),2*getWidth(),2*getWidth(),startAngle,extendAngle,Arc2D.OPEN);
        try{
          if(arc.intersects(rect))
            return true;
          else
            return false;   
        }finally{
            rectProvider.reset();
        }
    }
   
    @Override
    public Point isControlRectClicked(int xx, int yy) {
    
        FlyweightProvider rectFlyweightProvider = ShapeFlyweightFactory.getProvider(Rectangle2D.class);
        Rectangle2D rect = (Rectangle2D)rectFlyweightProvider.getShape();

        
        try{
            rect.setRect((x-width) - selectionRectWidth / 2, (y-width) - selectionRectWidth / 2,
                         selectionRectWidth, selectionRectWidth);
            if (rect.contains(xx,yy)) {
                return new Point((x-width),(y-width));
            }
            rect.setRect((x+width) - selectionRectWidth / 2, (y-width) - selectionRectWidth / 2,
                         selectionRectWidth, selectionRectWidth);
            if (rect.contains(xx,yy)){
                return new Point((x+width),(y-width));
            }
            rect.setRect((x-width) - selectionRectWidth / 2, (y+width) - selectionRectWidth / 2,
                         selectionRectWidth, selectionRectWidth);
            if (rect.contains(xx,yy)){
                return new Point((x-width),(y+width));
            }

            rect.setRect((x+width) - selectionRectWidth / 2, (y+width) - selectionRectWidth / 2,
                         selectionRectWidth, selectionRectWidth);
            if (rect.contains(xx,yy)){
                return new Point((x+width),(y+width));
            }

             Point2D p=getStartPoint();
             rect.setRect((p.getX()) - selectionRectWidth / 2, (p.getY()) - selectionRectWidth / 2,
                             selectionRectWidth, selectionRectWidth);
             if (rect.contains(xx,yy)) {
                    return new Point((int)p.getX(),(int)p.getY());
             }
             p=getEndPoint();
             rect.setRect((p.getX()) - selectionRectWidth / 2, (p.getY()) - selectionRectWidth / 2,
                             selectionRectWidth, selectionRectWidth);
             if (rect.contains(xx,yy)) {
                   return new Point((int)p.getX(),(int)p.getY());
             }
                
            }finally{
                rectFlyweightProvider.reset();
            }
            return null;       
    }
    
    public boolean isStartAnglePointClicked(int x,int y){
        FlyweightProvider rectFlyweightProvider = ShapeFlyweightFactory.getProvider(Rectangle2D.class);
        Rectangle2D rect = (Rectangle2D)rectFlyweightProvider.getShape();

        
        try{
            Point2D p=getStartPoint();
            rect.setRect((p.getX()) - selectionRectWidth / 2, (p.getY()) - selectionRectWidth / 2,
                         selectionRectWidth, selectionRectWidth);
            if (rect.contains(x,y)) {
                return true;
            }            
        }finally{
            rectFlyweightProvider.reset();
        }        
        return false;
    }
    public boolean isExtendAnglePointClicked(int x,int y){
        FlyweightProvider rectFlyweightProvider = ShapeFlyweightFactory.getProvider(Rectangle2D.class);
        Rectangle2D rect = (Rectangle2D)rectFlyweightProvider.getShape();

        
        try{
            
            Point2D  p=getEndPoint();
            rect.setRect((p.getX()) - selectionRectWidth / 2, (p.getY()) - selectionRectWidth / 2,
                         selectionRectWidth, selectionRectWidth);
            if (rect.contains(x,y)) {
                return true;
            }
            
        }finally{
            rectFlyweightProvider.reset();
        }         
        return false;
    }
    public double getStartAngle(){
        return startAngle ;
    }

    public double getExtendAngle(){
       return extendAngle;
    }
    public void setExtendAngle(double extendAngle){
    
       this.extendAngle=Math.round(extendAngle*100.0)/100.0;;
    
    }
    public void setStartAngle(double startAngle){        
       this.startAngle=Math.round(startAngle*100.0)/100.0;
    }
    
    @Override
    public void Mirror(Point A,Point B) {
        super.Mirror(A,B);
        if(A.x==B.x){
          //***which place in regard to x origine   
          //***tweak angles 
            if(startAngle<=180){
             startAngle=(180-startAngle);
            }else{
             startAngle=(180+(360 -startAngle));
            }
          extendAngle=(-1)*extendAngle;
        }else{    //***top-botom mirroring
          //***which place in regard to y origine    
          //***tweak angles
          startAngle=360-startAngle;
          extendAngle=(-1)*extendAngle;
        }
    }

    @Override
    public void Rotate(AffineTransform rotation) {
        super.Rotate(rotation);        
       
        if(rotation.getShearY()>0) {        //right                               
            if((startAngle-90)<0){
               startAngle=360-(90-startAngle); 
            }else{
               startAngle+=-90;   
            }                                 
        }else{                          //left                                
            if((startAngle+90)>360){
              startAngle=90-(360-startAngle);
            }else{
              startAngle+=90; 
            }             
        } 
    }
    @Override
    public void Resize(int xoffset, int yoffset, Point point) {    
        Utilities.QUADRANT quadrant= Utilities.getQuadrantLocation(point,x,y);
        switch(quadrant){
        case FIRST:case FORTH: 
            //uright
             if(xoffset<0){
               //grows             
                width+=Math.abs(xoffset);
             }else{
               //shrinks
                width-=Math.abs(xoffset);
             }             
            break;
        case SECOND:case THIRD:
            //uleft
             if(xoffset<0){
               //shrinks             
                width-=Math.abs(xoffset);
             }else{
               //grows
                width+=Math.abs(xoffset);
             }             
            break;        
        }

    }  
    @Override
    public Point getResizingPoint() {
        return null;
    }

    @Override
    public void setResizingPoint(Point point) {
        //this.resizingPoint = point;
    }    
    @Override
    public String toXML() {
       return "<arc copper=\""+getCopper().getName()+"\" type=\"0\" x=\""+(x-width)+"\" y=\""+(y-width)+"\" width=\""+(2*width)+"\"  thickness=\""+this.getThickness()+"\" start=\""+this.startAngle+"\" extend=\""+this.extendAngle+"\" fill=\""+this.getFill().ordinal()+"\" />\r\n";
    }

    @Override
    public void fromXML(Node node) {
        Element  element= (Element)node;
        if(element.hasAttribute("copper")){
          this.setCopper(Layer.Copper.valueOf(element.getAttribute("copper")));    
        }    
        int xx=(Integer.parseInt(element.getAttribute("x")));
        int yy=(Integer.parseInt(element.getAttribute("y")));        
        int diameter=(Integer.parseInt(element.getAttribute("width")));
        //center x
        setX(xx+((int)(diameter/2)));
        //center y
        setY(yy+((int)(diameter/2)));
        //radius
        setWidth((diameter/2));
        setHeight((diameter/2));        
        
        this.setStartAngle(Double.parseDouble(element.getAttribute("start")));
        this.setExtendAngle(Double.parseDouble(element.getAttribute("extend")));

        this.setThickness(Integer.parseInt(element.getAttribute("thickness")));
        this.setFill(Fill.values()[(element.getAttribute("fill")==""?0:Integer.parseInt(element.getAttribute("fill")))]);

    }

    @Override
    public void Paint(Graphics2D g2, ViewportWindow viewportWindow, AffineTransform scale,int layermask) {
        //is this my layer mask
        if((this.getCopper().getLayerMaskID()&layermask)==0){
            return;
        }
        Rectangle2D scaledRect = Utilities.getScaleRect(getBoundingShape().getBounds() ,scale); 
        if(!scaledRect.intersects(viewportWindow)){
          return;   
        }
        FlyweightProvider provider =ShapeFlyweightFactory.getProvider(Arc2D.class);
        Arc2D temporal=(Arc2D)provider.getShape(); 
        temporal.setArc(scaledRect.getX()-viewportWindow.x ,scaledRect.getY()-viewportWindow.y,scaledRect.getWidth(),scaledRect.getWidth(),startAngle,extendAngle,Arc2D.OPEN);

        AlphaComposite composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f);   
        Composite originalComposite = g2.getComposite();                     
        g2.setComposite(composite ); 
        
        g2.setColor(isSelected()?Color.GRAY:copper.getColor()); 
        double wireWidth=thickness*scale.getScaleX();       
        g2.setStroke(new BasicStroke((float)wireWidth,1,1));          
        
        if(this.fill==Fill.EMPTY){
         g2.draw(temporal);       
        }else{
         g2.fill(temporal);   
        }
        g2.setComposite(originalComposite);
        
        provider.reset();
        
        if(isSelected()){         
            this.drawControlShape(g2,viewportWindow,scale);            
            this.calculate(g2, viewportWindow, scale);
        }
        
        
    }
    public void drawControlShape(Graphics2D g2, ViewportWindow viewportWindow, AffineTransform scale) {
        FlyweightProvider provider = ShapeFlyweightFactory.getProvider(Line2D.class);
        Line2D line = (Line2D) provider.getShape();

        g2.setStroke(new BasicStroke(1));

        g2.setColor(Color.BLUE);
        
        //top
            line.setLine((getX()-getWidth()) - selectionRectWidth, getY()-getWidth(), (getX()-getWidth()) + selectionRectWidth, getY()-getWidth());
            Utilities.drawLine(line, g2, viewportWindow, scale);

            line.setLine((getX()-getWidth()), getY()-getWidth() - selectionRectWidth, (getX()-getWidth()), getY()-getWidth() + selectionRectWidth);
            Utilities.drawLine(line, g2, viewportWindow, scale);
        
            line.setLine((getX()+getWidth()) - selectionRectWidth, getY()-getWidth(), (getX()+getWidth()) + selectionRectWidth, getY()-getWidth());
            Utilities.drawLine(line, g2, viewportWindow, scale);

            line.setLine((getX()+getWidth()), getY()-getWidth() - selectionRectWidth, (getX()+getWidth()), getY()-getWidth() + selectionRectWidth);
            Utilities.drawLine(line, g2, viewportWindow, scale);
        //bottom
            line.setLine((getX()-getWidth()) - selectionRectWidth, getY()+getWidth(), (getX()-getWidth()) + selectionRectWidth, getY()+getWidth());
            Utilities.drawLine(line, g2, viewportWindow, scale);

            line.setLine((getX()-getWidth()), getY()+getWidth() - selectionRectWidth, (getX()-getWidth()), getY()+getWidth() + selectionRectWidth);
            Utilities.drawLine(line, g2, viewportWindow, scale);
        
            line.setLine((getX()+getWidth()) - selectionRectWidth, getY()+getWidth(), (getX()+getWidth()) + selectionRectWidth, getY()+getWidth());
            Utilities.drawLine(line, g2, viewportWindow, scale);

            line.setLine((getX()+getWidth()), getY()+getWidth() - selectionRectWidth, (getX()+getWidth()), getY()+getWidth() + selectionRectWidth);
            Utilities.drawLine(line, g2, viewportWindow, scale);
      
    //center
            line.setLine((getX()) - selectionRectWidth, getY(), (getX()) + selectionRectWidth, getY());
            Utilities.drawLine(line, g2, viewportWindow, scale);

            line.setLine((getX()), getY() - selectionRectWidth, getX(), getY() + selectionRectWidth);
            Utilities.drawLine(line, g2, viewportWindow, scale);

        provider.reset();
    }    
    @Override
    public void Print(Graphics2D g2,PrintContext printContext,int layermask) {
        Rectangle2D rect = getBoundingShape().getBounds(); 
        FlyweightProvider provider =ShapeFlyweightFactory.getProvider(Arc2D.class);
        Arc2D temporal=(Arc2D)provider.getShape(); 
        temporal.setArc(rect.getX() ,rect.getY(),rect.getWidth(),rect.getWidth(),startAngle,extendAngle,Arc2D.OPEN);

        double wireWidth=thickness;       
        g2.setStroke(new BasicStroke((float)wireWidth,1,1));    
        g2.setPaint(printContext.isBlackAndWhite()?Color.BLACK:copper.getColor());        
        if(this.fill==Fill.EMPTY){
         g2.draw(temporal);       
        }else{
         g2.fill(temporal);   
        }
        
        provider.reset();
    }
    
    private void calculate(Graphics2D g2, ViewportWindow viewportWindow, AffineTransform scale){
    
            
            Utilities.drawCrosshair(g2, viewportWindow, scale, null, selectionRectWidth, getStartPoint());
                    
            Utilities.drawCrosshair(g2, viewportWindow, scale, null, selectionRectWidth, getEndPoint());
            
            
    }

    @Override
    public Point2D getStartPoint() {
        double r=getWidth();                
        double x = r * Math.cos(-Math.PI/180*(startAngle)) + getX();
        double y = r * Math.sin(-Math.PI/180*(startAngle)) + getY();
        return new Point2D.Double(x,y);
    }

    @Override
    public Point2D getEndPoint() {
        double r=getWidth();  
        double x = r * Math.cos(-Math.PI/180*(startAngle+extendAngle)) + getX();
        double y = r * Math.sin(-Math.PI/180*(startAngle+extendAngle)) + getY();
        return new Point2D.Double(x,y);
    }

//    @Override
//    public Point getCenterPoint() {        
//        return new Point(x,y);
//    }

    @Override
    public boolean isSingleQuadrant() {
        return Math.abs(extendAngle)<=90;
    }

    @Override
    public boolean isClockwise() {
        
        return extendAngle <0;
    }
    public int getI(){
        int i=0;
        //loss of pressiosion!!!!!!!!!!!!!!!
        Utilities.QUADRANT quadrant= Utilities.getQuadrantLocation( getCenter(),(int)getStartPoint().getX(),(int)getStartPoint().getY());
        //if(isSingleQuadrant()){
            switch(quadrant){
             case SECOND:case THIRD:
                i=x-(int)getStartPoint().getX();
                break;
             case FIRST:case FORTH:
                //convert to -
                i=(x-(int)getStartPoint().getX());
             break;
            }
        
        return i;
    }
    
    public int getJ(){
        int j=0;
        Utilities.QUADRANT quadrant= Utilities.getQuadrantLocation(getCenter(),(int)getStartPoint().getX(),(int)getStartPoint().getY());
        //if(isSingleQuadrant()){
            switch(quadrant){
             case FIRST:case SECOND:
                j=y-(int)getStartPoint().getY();
                break;
             case THIRD:case FORTH:
                //convert to -
                j=y-(int)getStartPoint().getY();
             break;
            }        
        return j;
    }

    @Override
    public AbstractMemento getState(MementoType operationType) {
        Memento memento=new Memento(operationType);
        memento.saveStateFrom(this);        
        return memento;
    }

    @Override
    public void setState(AbstractMemento memento) {
        memento.loadStateTo(this);  
    }
    
    public static class Memento extends AbstractMemento<Unit,Arc>{
        private double startAngle;        
        private double extendAngle;
        private int x;
        private int radios;
        private int y;
        
        
        public Memento(MementoType mementoType) {
           super(mementoType);            
        }
        @Override
        public void saveStateFrom(Arc shape) {
            super.saveStateFrom(shape);         
            this.startAngle=((Arc)shape).startAngle;
            this.extendAngle=((Arc)shape).extendAngle;
            this.x=shape.getX();
            this.y=shape.getY();
            this.radios=shape.getWidth();
        }
        @Override
        public void loadStateTo(Arc shape) {
           super.loadStateTo(shape);
           ((Arc)shape).setStartAngle(startAngle);
           ((Arc)shape).setExtendAngle(extendAngle); 
            shape.setX(x);
            shape.setY(y);
            shape.setWidth(radios);
        }
        
        @Override
        public boolean equals(Object obj){
            if(this==obj){
              return true;  
            }
            if(!(obj instanceof Memento)){
              return false;  
            }         
            Memento other=(Memento)obj;
            return (other.getMementoType().equals(this.getMementoType())&&
                    other.getUUID().equals(this.getUUID())&&
                    other.thickness==this.thickness&&
                    other.fill==this.fill&&
                    other.layerindex==this.layerindex&&
                    x==other.x&&
                    y==other.y&&radios==other.radios&&
                    Double.compare(this.startAngle,other.startAngle)==0&&Double.compare(this.extendAngle,other.extendAngle)==0
                   );
            
          
        }
        
        @Override
        public int hashCode(){            
           int hash=31+getUUID().hashCode()+this.getMementoType().hashCode()+this.fill+this.thickness+this.layerindex;
           hash+=x;
           hash+=y;
           hash+=radios+new Double(startAngle).hashCode()+new Double(extendAngle).hashCode();             
           return hash;
        }
        
        @Override
        public boolean isSameState(Unit unit) {
            Arc other=(Arc)unit.getShape(getUUID());              
            return (other.getThickness()==this.thickness&&other.getFill().ordinal()==this.fill&&other.copper.ordinal()==this.layerindex&&
                    (other.getX()==this.x)&&(other.getY()==this.y)&&
                    (other.getWidth()==this.radios)&&Double.compare(this.extendAngle,other.getExtendAngle())==0&&Double.compare(this.startAngle,other.getStartAngle())==0);                    
        }
    }
    
}
