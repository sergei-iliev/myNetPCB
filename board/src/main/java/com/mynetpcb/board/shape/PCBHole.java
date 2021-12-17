package com.mynetpcb.board.shape;

import com.mynetpcb.board.unit.Board;
import com.mynetpcb.core.board.PCBShape;
import com.mynetpcb.core.board.shape.HoleShape;
import com.mynetpcb.core.capi.Grid;
import com.mynetpcb.core.capi.ViewportWindow;
import com.mynetpcb.core.capi.flyweight.FlyweightProvider;
import com.mynetpcb.core.capi.flyweight.ShapeFlyweightFactory;
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
import java.awt.geom.Ellipse2D;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Element;
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
    public int getDrawingLayerPriority() {        
        return 110;
    }
    @Override
    public long getClickableOrder() {
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
    public boolean isClicked(double x, double y) {        
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
    public <T extends ClearanceSource> void drawClearance(Graphics2D g2, ViewportWindow viewportWindow,
                                                          AffineTransform scale, T source) {
        
        //only if explicitly set
        //if(Utils.EQ(this.clearance,0)){
        //    return;
        //}
        Box rect = this.circle.box();
        rect.grow(this.clearance!=0?this.clearance:source.getClearance());        
        
        //is via within copper area
        if(!(source.getBoundingShape().intersects(rect))){
           return; 
        }
        
        rect.scale(scale.getScaleX());
        if (!rect.intersects(viewportWindow)){
                return;
        }
        FlyweightProvider ellipseProvider = ShapeFlyweightFactory.getProvider(Ellipse2D.class);
        Ellipse2D ellipse = (Ellipse2D)ellipseProvider.getShape();
        
        ellipse.setFrame(rect.getX() - viewportWindow.getX(), rect.getY() - viewportWindow.getY(),
                         rect.getWidth(), rect.getHeight());
        
        g2.setColor(Color.BLACK);                
        g2.fill(ellipse);

        ellipseProvider.reset();
    }

    @Override
    public <T extends ClearanceSource> void printClearance(Graphics2D g2, PrintContext printContext,
                                                           T source) {


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
        StringBuffer xml = new StringBuffer();
        xml.append("<hole x=\""+Utilities.roundDouble(this.circle.pc.x)+"\" y=\""+Utilities.roundDouble(this.circle.pc.y)+"\" width=\""+this.circle.r*2+"\"  clearance=\""+this.clearance+"\" />");
        return xml.toString();
    }

    @Override
    public void fromXML(Node node) throws XPathExpressionException, ParserConfigurationException {
        Element element=(Element)node;
        double x=(Double.parseDouble(element.getAttribute("x")));
        double y=(Double.parseDouble(element.getAttribute("y")));
   
        this.circle.pc.set(x,y);
        
        this.circle.r=(Double.parseDouble(element.getAttribute("width")))/2;
                
        this.clearance=element.getAttribute("clearance").equals("")?0:Integer.parseInt(element.getAttribute("clearance"));                

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
        c.grow(this.thickness);
        c.scale(scale.getScaleX());
        c.move(-viewportWindow.getX(),- viewportWindow.getY());

        //g2.setStroke(new BasicStroke((float)(thickness*scale.getScaleX())));            
        g2.setColor(isSelected() ? Color.GRAY : fillColor);        
        c.paint(g2,true);
        
        c=this.circle.clone();
        c.scale(scale.getScaleX());
        c.move(-viewportWindow.getX(),- viewportWindow.getY());        
        g2.setColor(Color.BLACK);
        c.paint(g2,true);

        
        Utilities.drawCrosshair(g2,  null,(int)(selectionRectWidth*scale.getScaleX()),c.getCenter());
        

    }
    @Override
    public void print(Graphics2D g2, PrintContext printContext, int layermask) {    
        g2.setStroke(new BasicStroke(thickness));     
        g2.setColor(printContext.getBackgroundColor()==Color.BLACK?Color.WHITE:Color.BLACK);                
        this.circle.paint(g2, true); 
        
        g2.setColor(printContext.getBackgroundColor());  
        this.circle.paint(g2, true);
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
