package com.mynetpcb.board.shape;

import com.mynetpcb.board.unit.Board;
import com.mynetpcb.core.board.PCBShape;
import com.mynetpcb.core.board.shape.ViaShape;
import com.mynetpcb.core.capi.Grid;
import com.mynetpcb.core.capi.ViewportWindow;
import com.mynetpcb.core.capi.layer.ClearanceSource;
import com.mynetpcb.core.capi.print.PrintContext;
import com.mynetpcb.core.capi.undo.AbstractMemento;
import com.mynetpcb.core.capi.undo.MementoType;
import com.mynetpcb.core.utils.Utilities;
import com.mynetpcb.d2.shapes.Box;
import com.mynetpcb.d2.shapes.Circle;
import com.mynetpcb.d2.shapes.Point;
import com.mynetpcb.d2.shapes.Utils;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Node;

public class PCBVia extends ViaShape implements PCBShape{

    //private String net;
    private int clearance;    
    private Circle circle;
    private double diameter;
    
    public PCBVia() {        
        this.fillColor=Color.WHITE; 
        this.displayName="Via";        
        this.selectionRectWidth=3000;
        this.diameter=Grid.MM_TO_COORD(1.205);
        this.circle=new Circle(new Point(0,0),Grid.MM_TO_COORD(0.4)); 
    }

    @Override
    public PCBVia clone() throws CloneNotSupportedException {
        PCBVia copy = (PCBVia)super.clone();
        copy.circle=this.circle.clone();
        return copy;
    }
    @Override
    public Point alignToGrid(boolean isRequired) {
        if(isRequired){
            Point point=getOwningUnit().getGrid().positionOnGrid(circle.pc.x, circle.pc.y);
            circle.pc.set(point);            
            return null;                      
        }else{
          return null;
        }
    } 
    @Override
    public Box getBoundingShape() {
        Box box=this.circle.box();         
        box.grow(diameter/2-this.circle.r);
        return box;         
    }
    @Override
    public void move(double xoffset,double yoffset) {
        this.circle.move(xoffset,yoffset);
    }
    @Override
    public <T extends ClearanceSource> void drawClearence(Graphics2D graphics2D, ViewportWindow viewportWindow,
                                                          AffineTransform affineTransform, T clearanceSource) {
        // TODO Implement this method

    }

    @Override
    public <T extends ClearanceSource> void printClearence(Graphics2D graphics2D, PrintContext printContext,
                                                           T clearanceSource) {
        // TODO Implement this method

    }
    @Override
    public Point getCenter() {
        return this.circle.pc;
    }
    @Override
    public void setClearance(int clearance) {
        this.clearance=clearance;
    }

    @Override
    public int getClearance() {    
        return clearance;
    }

    @Override
    public String toXML() {
        // TODO Implement this method
        return null;
    }

    @Override
    public void fromXML(Node node) throws XPathExpressionException, ParserConfigurationException {
        // TODO Implement this method

    }

    @Override
    public void paint(Graphics2D g2, ViewportWindow viewportWindow, AffineTransform scale, int layermask) {
        //is this my layer mask
        if((this.getCopper().getLayerMaskID()&layermask)==0){
            return;
        }

        Box rect = this.circle.box();
        rect.scale(scale.getScaleX());
        if (!rect.intersects(viewportWindow)) {
                return;
        }
        g2.setColor(isSelected() ? Color.GRAY : fillColor);
        
        Circle  c=this.circle.clone();
        double r=c.r;
        double x=c.getCenter().x,y=c.getCenter().y;
        
        c.r=diameter/2;
        c.scale(scale.getScaleX());
        c.move(-viewportWindow.getX(),- viewportWindow.getY());
        c.paint(g2, true);
        
        g2.setColor(Color.BLACK);
        c.r=r;
        c.pc.set(x, y);
        c.scale(scale.getScaleX());
        c.move(-viewportWindow.getX(),- viewportWindow.getY());
        c.paint(g2, true);
                          
        Utilities.drawCrosshair(g2,  null,(int)(selectionRectWidth*scale.getScaleX()),c.getCenter());

    }
    
    @Override
    public AbstractMemento getState(MementoType operationType) {
        AbstractMemento memento = new Memento(operationType);
        memento.saveStateFrom(this);
        return memento;
    }




    static class Memento extends AbstractMemento<Board,PCBVia>{
        private double x,y;
        private double r,diameter;
        
        
        
        
        
        public Memento(MementoType mementoType){
           super(mementoType); 
        }
        
        public void saveStateFrom(PCBVia shape) {
            super.saveStateFrom(shape);
            this.diameter=shape.diameter;
            this.x=shape.circle.pc.x;
            this.y=shape.circle.pc.y;
            this.r=shape.circle.r;
        }
        

        public void loadStateTo(PCBVia shape){
            super.loadStateTo(shape);
            shape.diameter=diameter;
            shape.circle.pc.set(x,y);
            shape.circle.r=r;
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
        
            return super.equals(obj) && Utils.EQ(this.x, other.x)&&Utils.EQ(this.diameter,other.diameter)&&
            Utils.EQ(this.y, other.y)&&Utils.EQ(this.r,other.r); 
                      
        }
        
        @Override
        public int hashCode(){
            int hash = 1;
            hash = super.hashCode();
            hash += Double.hashCode(this.x)+Double.hashCode(diameter)+
                    Double.hashCode(this.y)+Double.hashCode(this.r);
            return hash;
        }        
        public boolean isSameState(Board unit) {
            boolean flag = super.isSameState(unit);
            PCBVia other=(PCBVia)unit.getShape(getUUID());
            return flag&&Utils.EQ(this.x,other.circle.pc.x)&&Utils.EQ(this.y,other.circle.pc.y)&&Utils.EQ(this.r,other.circle.r)&&Utils.EQ(this.diameter,other.diameter);
        }
        
    }    
}
