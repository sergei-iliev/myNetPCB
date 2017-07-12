package com.mynetpcb.core.capi.shape;


import com.mynetpcb.core.board.Layerable;
import com.mynetpcb.core.capi.Moveable;
import com.mynetpcb.core.capi.print.PrintContext;
import com.mynetpcb.core.capi.print.Printaware;
import com.mynetpcb.core.capi.undo.AbstractMemento;
import com.mynetpcb.core.capi.undo.MementoType;
import com.mynetpcb.core.capi.undo.Stateable;
import com.mynetpcb.core.capi.unit.Unit;
import com.mynetpcb.core.capi.unit.Unitable;
import com.mynetpcb.core.pad.Layer;
import com.mynetpcb.core.utils.Utilities;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;

import java.lang.reflect.Method;

import java.util.UUID;


/**
 *Base drawing premitive, extended by Symbol,Pad
 * Shape belongs to a layer wich is not accounted for in the context of circuit
 * @author Sergey Iliev
 */
public abstract class Shape implements Moveable, BoundsCacheable,Printaware,Stateable,Unitable<Unit>,Layerable{
    public enum Fill{
        EMPTY(1),
        FILLED(2),
        GRADIENT(3);
        
        public final int index;
        
        Fill(int index){
          this.index=index;  
        }
    
        public static Fill byIndex(int index){
          return Fill.values()[index-1];    
        }
        
    }
    
    protected java.awt.Shape shapeCacheBounds;

    protected boolean isCasheEnabled;
    
    protected UUID uuid;    
    
    private  Unit owningUnit;
    
    private boolean selected;
    
    protected int x,y;
    
    protected int width,height;
    
    protected int thickness;
    
    protected Fill fill;
    
    protected  Color fillColor;
    
    protected int selectionRectWidth;
    
    protected Layer.Copper copper;
    
    public Shape(int x,int y,int width,int height,int thickness,int layermask) {
      this.uuid = UUID.randomUUID(); 
      this.x=x;
      this.y=y;
      this.width=width;
      this.height=height;
      this.thickness=thickness;
      this.fill=Fill.EMPTY;
      this.copper=Layer.Copper.resolve(layermask);
      this.selectionRectWidth=4;
    }

 
    public UUID getUUID() {
        return uuid;
    }
    
    public void setUUID(UUID uuid){
       this.uuid=uuid; 
    }
    
    public void setThickness(int thickness) {
        this.thickness = thickness;
    }

    public int getThickness() {
        return thickness;
    }
    @Override
    public Point alignToGrid(boolean isRequired) {
        Point point=getOwningUnit().getGrid().positionOnGrid(getX(), getY());
        setX(point.x);
        setY(point.y);      
        return null;
    }
    public void Clear() {
        owningUnit=null;
    }
    
    public void setSelectionRectWidth(int selectionRectWidth) {
        this.selectionRectWidth = selectionRectWidth;
    }
    
    @Override
    public Shape clone()throws CloneNotSupportedException{
        Shape copy=(Shape)super.clone();
        copy.uuid = UUID.randomUUID(); 
        copy.shapeCacheBounds=null;
        copy.owningUnit=null;
        return copy;
    }
        @Override
        public AbstractMemento getState(MementoType operationType) {
            return null;
        }
    
        @Override
        public void setState(AbstractMemento memento) {
        }
    public Fill getFill(){
        return fill;
    }
    
    public void setFill(Fill fill){
       this.fill=fill; 
    }
    
    public String getDisplayName() {
        return "noname";
    }


    public int getX() {
        return x;
    }
    public void setX(int x){
      this.x=x;  
    }

    public int getY() {
        return y;
    }
    public void setY(int y){
      this.y=y;  
    }
    
    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
    public void setWidth(int width) {
       this.width=width;
    }

    public void setHeight(int height) {
        this.height = height;
    }
    public int getCenterX(){
        return x;
    }
    
    public int getCenterY(){
        return y;
    }
    
    @Override
    public void Move(int xoffset, int yoffset) {
        setX(getX() + xoffset);
        setY(getY() + yoffset);    
    }

    @Override
    public void Mirror(Point A,Point B) {
        Point point = new Point(getX(), getY());
        Utilities.mirrorPoint(A,B, point);
        setX(point.x);
        setY(point.y);
    }
    
    @Override
    public void Rotate(AffineTransform rotation) {
        Point point = new Point(getX(), getY());
        rotation.transform(point, point);
        setX(point.x);
        setY(point.y);
    }
    
    public void Mirror(Moveable.Mirror type) {
        Rectangle r=getBoundingShape().getBounds();
        Point p=new Point((int)r.getCenterX(),(int)r.getCenterY()); 
        switch(type){
         case HORIZONTAL:
            Mirror(new Point(p.x-10,p.y),new Point(p.x+10,p.y));
        break;
         case VERTICAL:
            Mirror(new Point(p.x,p.y-10),new Point(p.x,p.y+10)); 
        }
    }
    @Override
    public void Translate(AffineTransform translate) {
    
    }

    
    public void Rotate(Moveable.Rotate type) {
        switch(type){
        case LEFT:
            Rotate(AffineTransform.getRotateInstance(Math.PI/2,getBoundingShape().getBounds().getCenterX(),getBoundingShape().getBounds().getCenterY()));          
            break;
        case RIGHT:
            Rotate(AffineTransform.getRotateInstance(-Math.PI/2,getBoundingShape().getBounds().getCenterX(),getBoundingShape().getBounds().getCenterY()));         
        }
    }

    @Override
    public void setLocation(int x, int y) {
       this.x=x;
       this.y=y;
    }

    @Override
    public long getOrderWeight(){
       return ((long)getWidth()*(long)getHeight()); 
    }

    @Override
    public boolean isClicked(int x, int y) {
        return this.getBoundingShape().contains(x, y);         
    }

    @Override
    public boolean isInRect(Rectangle r) {
        if(r.contains(getBoundingShape().getBounds().getCenterX(),getBoundingShape().getBounds().getCenterY()))
         return true;
        else
         return false; 
    }

    @Override
    public java.awt.Shape getBoundingShape() {
        //if(shapeCacheBounds==null||(!isCasheEnabled)){
            shapeCacheBounds=calculateShape();
            
       // }
        return shapeCacheBounds;        
    }

    @Override
    public void setSelected(boolean selected) {
      this.selected=selected;
    }

    @Override
    public boolean isSelected() {
        return selected;
    }

    @Override
    public java.awt.Shape calculateShape(){
     throw new RuntimeException("Shape does not implement cacheing");
    }

    @Override
    public void enableCache(boolean enable) {
        isCasheEnabled=enable;
    }

    @Override
    public void clearCache() {
       shapeCacheBounds=null; 
    }

    @Override
    public Unit getOwningUnit(){
      return owningUnit;  
    }
    
    @Override
    public void setOwningUnit(Unit owningUnit){
       this.owningUnit=owningUnit; 
    }
    @Override
    public Color getFillColor(){
        return fillColor;
    }
    
    @Override
    public void setFillColor(Color color){
        this.fillColor=color;
    }
    
    public void setCopper(Layer.Copper copper){
        this.copper=copper;
    }

    public Layer.Copper getCopper(){
        return copper;
    }    
    
    @Override
    public int getDrawingOrder() {
        return 100;
    }  
    /*
     * Defines specific context menu for each shape
     */
    public Method showContextPopup()throws NoSuchMethodException, SecurityException{        
       return null;
    }
    
    @Override
    public void Print(Graphics2D g2,PrintContext printContext,int layermask) {
    }
    public boolean isVisibleOnLayers(int layermasks){
        if((copper.getLayerMaskID()&layermasks)!=0){
            return true;
        }else{
            return false;
        }
    }
}
