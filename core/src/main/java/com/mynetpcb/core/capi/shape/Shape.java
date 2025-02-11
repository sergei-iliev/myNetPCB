package com.mynetpcb.core.capi.shape;


import com.mynetpcb.core.capi.Moveable;
import com.mynetpcb.core.capi.layer.Layer;
import com.mynetpcb.core.capi.layer.Layerable;
import com.mynetpcb.core.capi.print.PrintContext;
import com.mynetpcb.core.capi.print.Printable;
import com.mynetpcb.core.capi.undo.AbstractMemento;
import com.mynetpcb.core.capi.undo.MementoType;
import com.mynetpcb.core.capi.undo.Stateable;
import com.mynetpcb.core.capi.unit.Unit;
import com.mynetpcb.core.capi.unit.Unitable;
import com.mynetpcb.d2.shapes.Box;
import com.mynetpcb.d2.shapes.Line;
import com.mynetpcb.d2.shapes.Point;

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
public abstract class Shape implements Moveable,Printable,Stateable,Unitable<Unit>,Layerable{
    public enum ArcType{
    	TWO_POINT_ARC,
    	CENTER_POINT_ARC
    }
	public enum Fill{
        EMPTY(1),
        FILLED(2),
        GRADIENT(3);
        
        public final int index;
        
        Fill(int index){
          this.index=index;  
        }
    
        public static Fill byIndex(int index){
            if(index==0){
                return Fill.values()[0];    
            }
            return Fill.values()[index-1];    
        }
        
    }
    
    protected UUID uuid;    
    
    private  Unit owningUnit;
    
    private boolean selected;
    
    protected int thickness;
    
    protected Fill fill;
    
    protected double rotate;
    
    protected  Color fillColor;
    
    protected int selectionRectWidth;
    
    protected int bendingPointDistance;
    
    protected String displayName;
    
    protected Layer.Copper copper;
    
    
    protected boolean isControlPointVisible;
    
    public Shape(int thickness,int layermask) {
      this.uuid = UUID.randomUUID(); 
      this.isControlPointVisible=true;
      this.thickness=thickness;
      this.fill=Fill.EMPTY;
      this.copper=Layer.Copper.resolve(layermask);
      this.selectionRectWidth=6;
      this.bendingPointDistance=6;
      this.fillColor=Color.BLACK;
    }
    /**
     * Board does not need to see control points of shapes
     */
    public void setControlPointVisibility(boolean isControlPointVisible){
        this.isControlPointVisible=isControlPointVisible;
    }
    public String getDisplayName() {
        return displayName;
    }
    public void setDisplayName(String displayName){
        this.displayName=displayName;
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

    public Point alignToGrid(boolean isRequired) {
        return null;
    }
    public void clear() {
        owningUnit=null;
    }
    
    public int getBendingPointDistance() {
		return bendingPointDistance;
	}
    
    @Override
    public Shape clone()throws CloneNotSupportedException{
        Shape copy=(Shape)super.clone();
        copy.uuid = UUID.randomUUID(); 
        copy.owningUnit=null;
        return copy;
    }
    
    public double getRotate(){
        return rotate;
    }
    
    public void setRotate(double rotate){
        this.rotate=rotate;
    }
    
    public void setRotation(double alpha,Point center){
        
    }
    public Fill getFill(){
        return fill;
    }
    
    public void setFill(Fill fill){
       this.fill=fill; 
    }
    
    @Override
    public Point getCenter(){
        return getBoundingShape().getCenter();
    }
    
    @Override
    public void move(double xoffset, double yoffset) {
        
    }

    @Override
    public void mirror(Line line) {
        
    }
    @Override
    public void rotate(double angle,Point origin) {

    }
    

    @Override
    public void translate(AffineTransform translate) {
    
    }


    @Override
    public void setLocation(double x, double y) {

    }

    @Override
    public long getClickableOrder(){
       return 100;
    }

    @Override
    public boolean isClicked(double x, double y) {
        return this.getBoundingShape().contains(x,y);         
    }
    
    @Override
    public boolean isClicked(double x, double y, int layermaskId) {        
        return this.isClicked(x, y);
    }
    
    @Override
    public boolean isInRect(Box r) {
        if(r.contains(getBoundingShape().getCenter()))
         return true;
        else
         return false; 
    }

    @Override
    public Box getBoundingShape() {
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
    public int getDrawingLayerPriority() {
        return 100;
    }  
    /*
     * Defines specific context menu for each shape
     */
    public Method showContextPopup()throws NoSuchMethodException, SecurityException{        
       return null;
    }
    
    @Override
    public void print(Graphics2D g2,PrintContext printContext,int layermask) {
    }
    public boolean isVisibleOnLayers(int layermasks){
        if((copper.getLayerMaskID()&layermasks)!=0){
            return true;
        }else{
            return false;
        }
    }
    
    public AbstractMemento getState(MementoType operationType) {
        throw new IllegalAccessError("Unknown memento state");
    }
    
    public void setState(AbstractMemento memento) {
        memento.loadStateTo(this);
    }    
}
