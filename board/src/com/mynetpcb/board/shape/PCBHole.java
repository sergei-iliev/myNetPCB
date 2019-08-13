package com.mynetpcb.board.shape;

import com.mynetpcb.board.unit.Board;
import com.mynetpcb.core.board.ClearanceSource;
import com.mynetpcb.core.board.PCBShape;
import com.mynetpcb.core.board.shape.HoleShape;
import com.mynetpcb.core.capi.Grid;
import com.mynetpcb.core.capi.ViewportWindow;
import com.mynetpcb.core.capi.flyweight.FlyweightProvider;
import com.mynetpcb.core.capi.flyweight.ShapeFlyweightFactory;
import com.mynetpcb.core.capi.print.PrintContext;
import com.mynetpcb.core.capi.undo.AbstractMemento;
import com.mynetpcb.core.capi.undo.MementoType;
import com.mynetpcb.core.utils.Utilities;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class PCBHole extends HoleShape implements PCBShape{
    
    private static final int THICKNESS=1000;
    
    private int clearance;
    
    public PCBHole() {
        this.fillColor=Color.WHITE;
        this.setWidth(Grid.MM_TO_COORD(1.6));
    }
    @Override
    public void setWidth(int width){
       super.setWidth(width);
       super.setHeight(width);
    }
    
    @Override
    public PCBHole clone() throws CloneNotSupportedException {
        PCBHole copy = (PCBHole)super.clone();
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
    public String getDisplayName() {
        return "Hole";
    }
    @Override
    public long getOrderWeight() {
        return 3;
    }
    @Override
    public Rectangle calculateShape() {
        return new Rectangle(getX() - getWidth()/2, getY() - getWidth()/2, getWidth(),getWidth());
    }

    @Override
    public void Paint(Graphics2D g2, ViewportWindow viewportWindow, AffineTransform scale, int layermask) {
        Rectangle2D scaledRect = Utilities.getScaleRect(getBoundingShape().getBounds(), scale);

        if (!scaledRect.intersects(viewportWindow)) {
            return;
        }
        g2.setColor(isSelected() ? Color.GRAY : fillColor);

        FlyweightProvider ellipseProvider = ShapeFlyweightFactory.getProvider(Ellipse2D.class);
        Ellipse2D ellipse = (Ellipse2D)ellipseProvider.getShape();
        ellipse.setFrame(scaledRect.getX() - viewportWindow.x, scaledRect.getY() - viewportWindow.y,
                         scaledRect.getWidth(), scaledRect.getHeight());
        g2.setStroke(new BasicStroke((float)(THICKNESS*scale.getScaleX())));                        
        g2.draw(ellipse);
        
        ellipseProvider.reset();

        drawControlShape(g2,viewportWindow,scale);
    }
    
    @Override
    public void Print(Graphics2D g2,PrintContext printContext,int layermaskId) {
        FlyweightProvider ellipseProvider = ShapeFlyweightFactory.getProvider(Ellipse2D.class);
        Ellipse2D ellipse = (Ellipse2D)ellipseProvider.getShape();
        
        ellipse.setFrame(getX() - getWidth()/2, getY() - getWidth()/2, getWidth(),getWidth());
        g2.setStroke(new BasicStroke(Grid.MM_TO_COORD(0.2)));
        g2.setColor(printContext.getBackgroundColor()==Color.BLACK?Color.WHITE:Color.BLACK); 
        g2.draw(ellipse);
        
        ellipseProvider.reset();
        
      
    } 
    
    @Override
    public <T extends PCBShape & ClearanceSource> void printClearence(Graphics2D g2,PrintContext printContext, T source) {
        FlyweightProvider ellipseProvider = ShapeFlyweightFactory.getProvider(Ellipse2D.class);
        Ellipse2D ellipse = (Ellipse2D)ellipseProvider.getShape();
        
        Rectangle rect = new Rectangle(getX() - getWidth()/2, getY() - getWidth()/2, getWidth(),getWidth());
        rect.grow(this.clearance!=0?this.clearance:source.getClearance(),this.clearance!=0?this.clearance:source.getClearance());
        ellipse.setFrame(rect.x ,rect.y,rect.getWidth(),rect.getWidth());
                                        
        g2.setColor(printContext.getBackgroundColor());                
        g2.fill(ellipse);
        
        ellipseProvider.reset();   
    }     
    
    public void drawControlShape(Graphics2D g2,ViewportWindow viewportWindow,AffineTransform scale){   
        Utilities.drawCrosshair(g2, viewportWindow, scale,null, getWidth(),new Point(getX(),getY()));
    }
    
    @Override
    public <T extends PCBShape & ClearanceSource> void drawClearence(Graphics2D g2,
                                                                     ViewportWindow viewportWindow,
                                                                     AffineTransform scale, T source) {
                
        Rectangle inner=getBoundingShape().getBounds();             
        inner.grow(this.clearance!=0?this.clearance:source.getClearance(),this.clearance!=0?this.clearance:source.getClearance());
        
        Rectangle2D scaledRect = Utilities.getScaleRect(inner ,scale); 
        if(!scaledRect.intersects(viewportWindow)){
          return;   
        }
        
        FlyweightProvider ellipseProvider = ShapeFlyweightFactory.getProvider(Ellipse2D.class);
        Ellipse2D ellipse = (Ellipse2D)ellipseProvider.getShape();
        
        ellipse.setFrame(scaledRect.getX() - viewportWindow.x, scaledRect.getY() - viewportWindow.y,
                         scaledRect.getWidth(), scaledRect.getHeight());
        
        g2.setColor(Color.BLACK);                
        g2.fill(ellipse);

        ellipseProvider.reset();
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
        xml.append("<hole x=\""+getX()+"\" y=\""+getY()+"\" width=\""+getWidth()+"\"  clearance=\""+this.clearance+"\" />");
        return xml.toString();
    }

    @Override
    public void fromXML(Node node) {
        Element element=(Element)node;
        setX(Integer.parseInt(element.getAttribute("x")));
        setY(Integer.parseInt(element.getAttribute("y")));
        setWidth(Integer.parseInt(element.getAttribute("width")));  
        if(!element.getAttribute("clearance").isEmpty()){
            setClearance(Integer.parseInt(element.getAttribute("clearance")));
        }
    }   
    
    @Override
    public AbstractMemento getState(MementoType operationType) {
        AbstractMemento memento = new Memento(operationType);
        memento.saveStateFrom(this);
        return memento;
    }

    @Override
    public void setState(AbstractMemento memento) {
        memento.loadStateTo(this);
    }




    static class Memento extends AbstractMemento<Board,PCBHole>{
        private int Ax;
        
        private int Ay;
        
        private int width;
        
        private int clearance;
        
        public Memento(MementoType mementoType){
           super(mementoType); 
        }
        
        public void loadStateTo(PCBHole shape) {
            super.loadStateTo(shape);
            shape.setX(Ax);
            shape.setY(Ay);
            shape.setWidth(width);
            shape.clearance=clearance;
        }
        

        public void saveStateFrom(PCBHole shape){
            super.saveStateFrom(shape);
            Ax=shape.getX();
            Ay=shape.getY();
            width=shape.getWidth();
            clearance=shape.clearance;
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
        
            return(getUUID().equals(other.getUUID())&&
                   getMementoType().equals(other.getMementoType())&&
                   Ax==other.Ax&&width==other.width&&
                   Ay==other.Ay&&clearance==other.clearance                
                );
                      
        }
        
        @Override
        public int hashCode(){
            int hash=getUUID().hashCode();
                hash+=this.getMementoType().hashCode();
                hash+=Ax+Ay+width+clearance;
            return hash;
        }        
        public boolean isSameState(Board unit) {
            PCBHole hole=(PCBHole)unit.getShape(getUUID());
            return( 
                  Ax==hole.getX()&&
                  Ay==hole.getY()&&                  
                  width==hole.getWidth()&&
                  clearance==hole.getClearance()
                );
        }
    }    
}
