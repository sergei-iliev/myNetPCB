package com.mynetpcb.board.shape;

import com.mynetpcb.board.unit.Board;
import com.mynetpcb.core.board.PCBShape;
import com.mynetpcb.core.board.shape.HoleShape;
import com.mynetpcb.core.capi.Grid;
import com.mynetpcb.core.capi.ViewportWindow;
import com.mynetpcb.core.capi.layer.ClearanceSource;
import com.mynetpcb.core.capi.print.PrintContext;
import com.mynetpcb.core.capi.undo.AbstractMemento;
import com.mynetpcb.core.capi.undo.MementoType;
import com.mynetpcb.core.capi.unit.Unit;
import com.mynetpcb.core.utils.Utilities;
import com.mynetpcb.d2.shapes.Box;
import com.mynetpcb.d2.shapes.Circle;
import com.mynetpcb.d2.shapes.Line;
import com.mynetpcb.d2.shapes.Point;
import com.mynetpcb.d2.shapes.Utils;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Node;

public class PCBHole extends HoleShape implements PCBShape{
    private int clearance;    
    private Circle circle;
    
    public PCBHole() {
        this.fillColor=Color.WHITE; 
        this.displayName="Hole";
        this.thickness=2000;
        this.selectionRectWidth=3000;
        this.circle=new Circle(new Point(0,0),Grid.MM_TO_COORD(0.8));
    }
    @Override
    public PCBHole clone() throws CloneNotSupportedException {
        PCBHole copy = (PCBHole)super.clone();
        copy.circle=this.circle.clone();
        return copy;
    }
    public Circle getInner(){
        return circle;
    }
    @Override
    public long getOrderWeight() {
        return 3;
    }
    @Override
    public Point getCenter() {
        return this.circle.pc;
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
        return this.circle.box();         
    }
    @Override
    public boolean isClicked(int x, int y) {        
        if (this.circle.contains(new Point(x, y)))
            return true;
         else
            return false;                
    }
    @Override
    public void move(double xoffset,double yoffset) {
        this.circle.move(xoffset,yoffset);
    }   
    @Override
    public void mirror(Line line) {
        this.circle.mirror(line);        
    }
    @Override
    public <T extends ClearanceSource> void drawClearance(Graphics2D graphics2D, ViewportWindow viewportWindow,
                                                          AffineTransform affineTransform, T clearanceSource) {
        // TODO Implement this method

    }

    @Override
    public <T extends ClearanceSource> void printClearance(Graphics2D graphics2D, PrintContext printContext,
                                                           T clearanceSource) {
        // TODO Implement this method

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
        c.scale(scale.getScaleX());
        c.move(-viewportWindow.getX(),- viewportWindow.getY());

        g2.setStroke(new BasicStroke((float)(thickness*scale.getScaleX())));            
        c.paint(g2, false);
        
        Utilities.drawCrosshair(g2,  null,(int)(selectionRectWidth*scale.getScaleX()),c.getCenter());
        

    }
    @Override
    public void print(Graphics2D g2, PrintContext printContext, int layermask) {        
        g2.setStroke(new BasicStroke((float)(thickness)));      
        this.circle.paint(g2, false);
    }
    @Override
    public AbstractMemento getState(MementoType operationType) {
        AbstractMemento memento = new Memento(operationType);
        memento.saveStateFrom(this);
        return memento;
    }






    static class Memento extends AbstractMemento<Board,PCBHole>{
        private double x,y;
        private double r;
        
        
        public Memento(MementoType mementoType) {
            super(mementoType);
        }

        @Override
        public void saveStateFrom(PCBHole shape) {
            super.saveStateFrom(shape);
            this.x=shape.circle.pc.x;
            this.y=shape.circle.pc.y;
            this.r=shape.circle.r;
        }
        
        public void loadStateTo(PCBHole shape) {
            super.loadStateTo(shape);
            shape.circle.pc.set(x,y);
            shape.circle.r=r;
        }
        
        @Override
        public boolean isSameState(Unit unit) {
            boolean flag = super.isSameState(unit);
            PCBHole other=(PCBHole)unit.getShape(getUUID());
            return flag&&Utils.EQ(this.x,other.circle.pc.x)&&Utils.EQ(this.y,other.circle.pc.y)&&Utils.EQ(this.r,other.circle.r);
                        
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
            return super.equals(obj) && Utils.EQ(this.x, other.x)&&
            Utils.EQ(this.y, other.y)&&Utils.EQ(this.r,other.r);   
          
         
              
        }
        
        @Override
        public int hashCode(){
            int hash = 1;
            hash = super.hashCode();
            hash += Double.hashCode(this.x)+
                    Double.hashCode(this.y)+Double.hashCode(this.r);
            return hash;
        }
    }      
}
