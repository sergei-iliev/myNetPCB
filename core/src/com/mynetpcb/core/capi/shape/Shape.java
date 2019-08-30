package com.mynetpcb.core.capi.shape;


import com.mynetpcb.core.capi.Moveable;
import com.mynetpcb.core.capi.layer.Layerable;
import com.mynetpcb.core.capi.print.PrintContext;
import com.mynetpcb.core.capi.print.Printaware;
import com.mynetpcb.core.capi.undo.AbstractMemento;
import com.mynetpcb.core.capi.undo.MementoType;
import com.mynetpcb.core.capi.undo.Stateable;
import com.mynetpcb.core.capi.unit.Unit;
import com.mynetpcb.core.capi.unit.Unitable;
import com.mynetpcb.core.pad.Layer;

import com.mynetpcb.d2.shapes.Point;

import com.mynetpcb.d2.shapes.Rectangle;

import java.awt.Color;
import java.awt.Graphics2D;

import java.awt.geom.AffineTransform;

import java.lang.reflect.Method;

import java.util.UUID;


/**
 *Base drawing premitive, extended by Symbol,Pad
 * Shape belongs to a layer wich is not accounted for in the context of circuit
 * @author Sergey Iliev
 */
public abstract class Shape implements Moveable,Printaware,Stateable,Unitable<Unit>,Layerable{
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
    
    protected UUID uuid;    
    
    private  Unit owningUnit;
    
    private boolean selected;
    
    protected int thickness;
    
    protected Fill fill;
    
    protected  Color fillColor;
    
    protected int selectionRectWidth;
    
    protected Layer.Copper copper;
    
    public Shape(int thickness,int layermask) {
      this.uuid = UUID.randomUUID(); 

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

    public Point getCenter(){
        return null;
    }
    
    @Override
    public void move(int xoffset, int yoffset) {
        
    }

    @Override
    public void mirror(Point A,Point B) {

    }
    
    @Override
    public void rotate(AffineTransform rotation) {

    }
    

    @Override
    public void translate(AffineTransform translate) {
    
    }


    @Override
    public void setLocation(int x, int y) {

    }

    @Override
    public long getOrderWeight(){
       return 1;
    }

    @Override
    public boolean isClicked(int x, int y) {
        return this.getBoundingShape().contains(x,y);         
    }

    @Override
    public boolean isInRect(Rectangle r) {
//        if(r.contains(getBoundingShape().getBounds().getCenterX(),getBoundingShape().getBounds().getCenterY()))
//         return true;
//        else
         return false; 
    }

    @Override
    public Rectangle getBoundingShape() {
          return null;
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
