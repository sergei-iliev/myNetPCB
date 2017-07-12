package com.mynetpcb.symbol.shape;


import com.mynetpcb.core.capi.Externalizable;
import com.mynetpcb.core.capi.Rectangular;
import com.mynetpcb.core.capi.Reshapeable;
import com.mynetpcb.core.capi.ViewportWindow;
import com.mynetpcb.core.capi.flyweight.FlyweightProvider;
import com.mynetpcb.core.capi.flyweight.ShapeFlyweightFactory;
import com.mynetpcb.core.capi.print.PrintContext;
import com.mynetpcb.core.capi.shape.ResizableShape;
import com.mynetpcb.core.capi.undo.AbstractMemento;
import com.mynetpcb.core.capi.undo.MementoType;
import com.mynetpcb.core.capi.unit.Unit;
import com.mynetpcb.core.utils.Utilities;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import java.util.StringTokenizer;

import org.w3c.dom.Node;


public class Arc extends ResizableShape implements Rectangular,Reshapeable,Externalizable{
    
    private Arc2D arc;
                      
    public Arc(int x,int y,int width,int height) {
        super(x,y,width,height, 1,0);
        this.arc=new Arc2D.Double(x,y,width,height,-90,230,Arc2D.OPEN);
    }
    public Arc(){
        this(0,0,0,0);
    }
    @Override
    public Arc clone() throws CloneNotSupportedException{
        Arc copy= (Arc)super.clone();
        copy.arc=new Arc2D.Double(arc.getX(),arc.getY(),arc.getWidth(),arc.getHeight(),arc.getAngleStart(),arc.getAngleExtent(),arc.getArcType());
        return copy;
    }
    public int getStartAngle(){
        return (int)arc.getAngleStart() ;
    }

    public int getExtendAngle(){
       return (int)arc.getAngleExtent(); 
    }

    public void setArcType(int arcType){
        arc.setArcType(arcType);
    }
    
    public void setStartAngle(double angSt){
        arc.setAngleStart(angSt);
    }
    
    public void setExtendAngle(double angExt){
       arc.setAngleExtent(angExt);
    }
    
    public int getArcType(){
        switch(arc.getArcType()){
          case Arc2D.OPEN:return 0;
          case Arc2D.CHORD:   return 1;        
          case Arc2D.PIE:   return 2;  
        }
       return 0;
    }
    
    @Override
    public void Clear() {
    }
    
    @Override
    public void Mirror(Point A,Point B) {
        super.Mirror(A,B);
        
        int angSt=(int)arc.getAngleStart();
        int angExt=(int)arc.getAngleExtent();
        if(A.x==B.x){
          //***which place in regard to x origine   
          //***tweak angles 
          angSt=(2*90-angSt)-angExt;            
        }else{    //***top-botom mirroring
          //***which place in regard to y origine    
          //***tweak angles
          angSt+=angExt;
          angSt*=-1;                 
        }
        arc.setAngleStart(angSt);
    }

    @Override
    public void Rotate(AffineTransform rotation) {
        super.Rotate(rotation);        
        int angSt=(int)arc.getAngleStart();
        if(rotation.getShearY()>0) {        //right                               
                  angSt+=-90; 
                   if(angSt==-270) angSt=90;                                            
        }else{                          //left                                
                  angSt+=90; 
                   if(angSt==180) angSt=-180;  
        } 
        arc.setAngleStart(angSt);
    }

    @Override
    public void setLocation(int x, int y) {
    }

    @Override
    public void Paint(Graphics2D g2, ViewportWindow viewportWindow, AffineTransform scale,int layermask) {
        Rectangle2D scaledRect = Utilities.getScaleRect(getBoundingShape().getBounds() ,scale); 
        if(!scaledRect.intersects(viewportWindow)){
          return;   
        }
        arc.setFrame(scaledRect.getX()-viewportWindow.x ,scaledRect.getY()-viewportWindow.y,scaledRect.getWidth(),scaledRect.getHeight());
 
        
        g2.setStroke(new BasicStroke((float)(thickness*scale.getScaleX()),1,1)); 
        g2.setColor(isSelected()?Color.GRAY:fillColor); 
        
        if(fill == Fill.EMPTY)   //***empty
          g2.draw(arc);
        if(this.getFill() == Fill.FILLED)  //***filled
          g2.fill(arc);
        if(this.getFill() == Fill.GRADIENT){   //***gradual
            GradientPaint gp = 
                new GradientPaint(arc.getBounds().x, arc.getBounds().y, 
                                  Color.white, arc.getBounds().x, 
                                  (arc.getBounds().y+arc.getBounds().height), Color.gray, true);
            g2.setPaint(gp);
            g2.fill(arc);
            g2.setColor(Color.black);
            g2.draw(arc);
        }         
        
        if(isSelected()){         
            this.drawControlShape(g2,viewportWindow,scale);  
            //draw reshapeable circles
            FlyweightProvider ellipseProvider=ShapeFlyweightFactory.getProvider(Ellipse2D.class);
            Ellipse2D circle=(Ellipse2D)ellipseProvider.getShape();
             circle.setFrame(arc.getStartPoint().getX() -  selectionRectWidth *
                                     scale.getScaleX(),
                                     arc.getStartPoint().getY() - selectionRectWidth *
                                     scale.getScaleX(),
                                     selectionRectWidth * 2 *
                                     scale.getScaleX(),
                                     selectionRectWidth * 2 *
                                     scale.getScaleX());
            g2.draw(circle);
            
            circle.setFrame(arc.getEndPoint().getX() - selectionRectWidth *
                                  scale.getScaleX(),
                                  arc.getEndPoint().getY() - selectionRectWidth *
                                  scale.getScaleX(),
                                  selectionRectWidth * 2 *
                                  scale.getScaleX(),
                                  selectionRectWidth * 2 *
                                  scale.getScaleX());
            g2.draw(circle);
            ellipseProvider.reset();
        }
    }
    @Override
    public void Print(Graphics2D g2,PrintContext printContext,int layermask) {
        Rectangle2D rect = getBoundingShape().getBounds(); 
        arc.setFrame(rect.getX() ,rect.getY(),rect.getWidth(),rect.getHeight());
        
        if(thickness!=-1){    //framed   
          double wireWidth=thickness;       
          g2.setStroke(new BasicStroke((float)wireWidth,1,1));    
          g2.setPaint(Color.BLACK);        
          g2.draw(arc);
        }else{               //filled  
          g2.setColor(Color.BLACK);  
          g2.fill(arc);  
        }     
    }
    @Override
    public boolean isClicked(int x, int y) {
        arc.setFrame(getX(),getY(),getWidth(),getHeight());
        if(arc.contains(x,y))
         return true;
        else
         return false;   
    }
    
//    @Override
//    public Rectangle calculateShapeCacheBounds() {
//      return new Rectangle(getX(),getY(),getWidth(),getHeight());
//    }
    
    @Override
    public void Reshape(int x,int y,int targetid) {
        if(targetid==ARC_START_POINT){
           arc.setAngleStart(arc.getAngleStart()+x*3);  
        }
        if(targetid==ARC_END_POINT){
           arc.setAngleExtent(arc.getAngleExtent()+y*3); 
        }
    }
    
    @Override
    public Point isControlRectClicked(int x,int y) {
       Point point= super.isControlRectClicked(x, y);
       if(point!=null){
           return point;
       }else{
           Point2D point2d= isReshapeRectClicked(x, y);
           return point2d==null?null:new Point((int)point2d.getX(),(int)point2d.getY());
       }
    }
    
    @Override
    public Point2D isReshapeRectClicked(int x, int y) {
        Rectangle r =
            new Rectangle(x - selectionRectWidth, y - selectionRectWidth,
                          selectionRectWidth * 2, selectionRectWidth * 2);
        Rectangle2D rect = getBoundingShape().getBounds(); 
        arc.setFrame(rect.getX() ,rect.getY(),rect.getWidth(),rect.getHeight());

        if(r.contains(arc.getStartPoint())){
            
            return arc.getStartPoint();
        }
        if(r.contains(arc.getEndPoint())){
            return arc.getEndPoint();
        }

        return null;
    }
    @Override
    public int getReshapeRectID(int x,int y){
        Rectangle r =
            new Rectangle(x - selectionRectWidth, y - selectionRectWidth,
                          selectionRectWidth * 2, selectionRectWidth * 2);

        if(r.contains(arc.getStartPoint())){
            return ARC_START_POINT;
        }
        if(r.contains(arc.getEndPoint())){
            return ARC_END_POINT;
        }
        return -1;        
    }
    
    @Override
    public String getDisplayName(){
        return "Arc";
    }

    public String toXML() {
        return "<arc type=\""+arc.getArcType()+"\">"+upperLeft.x+","+upperLeft.y+","+getWidth()+","+getHeight()+","+this.getExtendAngle()+","+this.getStartAngle()+","+this.getThickness()+","+this.getFill().index+"</arc>\r\n";
    }
    public void fromXML(Node node) {  
        StringTokenizer st=new StringTokenizer(node.getTextContent(),",");                           
        Initialize(Integer.parseInt(st.nextToken()),Integer.parseInt(st.nextToken()),Integer.parseInt(st.nextToken()),Integer.parseInt(st.nextToken()));
        setExtendAngle(Integer.parseInt(st.nextToken()));
        setStartAngle(Integer.parseInt(st.nextToken()));
        setThickness(Byte.parseByte(st.nextToken()));
        setFill(Fill.byIndex(Byte.parseByte(st.nextToken())));   
        if(node.getAttributes().getNamedItem("type")!=null){
           setArcType(Integer.parseInt(node.getAttributes().getNamedItem("type").getNodeValue()));
        }
    }
    @Override
    public AbstractMemento getState(MementoType operationType) {
        Memento memento=new Memento(operationType);
        memento.saveStateFrom(this);        
        return memento;
    }

    @Override
    public void setState(AbstractMemento memento) {
        ((Memento)memento).loadStateTo(this);  
    }
    
    public static class Memento extends ResizableShape.Memento{
        private double angSt;
        
        private double angExt;
        
        private int arcType;
        
        public Memento(MementoType mementoType) {
           super(mementoType);            
        }
        @Override
        public void saveStateFrom(ResizableShape shape) {
           super.saveStateFrom(shape);         
            this.angSt=((Arc)shape).getStartAngle();
            this.angExt=((Arc)shape).getExtendAngle();
            this.arcType=((Arc)shape).getArcType();        
        }
        @Override
        public void loadStateTo(ResizableShape shape) {
           super.loadStateTo(shape);
           ((Arc)shape).setStartAngle(this.angSt);
           ((Arc)shape).setExtendAngle(this.angExt);
           ((Arc)shape).setArcType(this.arcType);
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
            return super.equals(obj)&&Double.compare(this.angExt,other.angExt)==0&&Double.compare(this.angSt,other.angSt)==0&&this.arcType==other.arcType;
        }
        
        @Override
        public int hashCode(){
          return super.hashCode()+new Double(angSt).hashCode()+new Double(angExt).hashCode()+(int)arcType;          
        }
        
        @Override
        public boolean isSameState(Unit unit) {
            Arc other=(Arc)unit.getShape(getUUID()); 
            return super.isSameState(unit)&&Double.compare(this.angExt,other.getExtendAngle())==0&&Double.compare(this.angSt,other.getStartAngle())==0&&this.arcType==other.getArcType();
        }
    }



}

