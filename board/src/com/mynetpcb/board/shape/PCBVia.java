package com.mynetpcb.board.shape;

import com.mynetpcb.board.unit.Board;
import com.mynetpcb.core.board.PCBShape;
import com.mynetpcb.core.board.shape.ViaShape;
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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class PCBVia extends ViaShape implements PCBShape{

    private String net;
    private int clearance;    
    private Circle inner,outer;    
    
    
    public PCBVia() {        
        this.fillColor=Color.WHITE; 
        this.displayName="Via";        
        this.selectionRectWidth=3000;
    
        this.outer=new Circle(new Point(0,0),Grid.MM_TO_COORD(0.8)); 
        this.inner=new Circle(new Point(0,0),Grid.MM_TO_COORD(0.4)); 
    }

    @Override
    public PCBVia clone() throws CloneNotSupportedException {
        PCBVia copy = (PCBVia)super.clone();
        copy.inner=this.inner.clone();
        copy.outer=this.outer.clone();
        return copy;
    }
    @Override
    public int getDrawingLayerPriority() {        
        return 120;
    }
    @Override
    public Point alignToGrid(boolean isRequired) {
        if(isRequired){
            Point point=getOwningUnit().getGrid().positionOnGrid(inner.pc.x, inner.pc.y);
            inner.pc.set(point);            
            outer.pc.set(point);            
            return null;                      
        }else{
          return null;
        }
    } 
    @Override
    public Box getBoundingShape() {
        return this.outer.box();                         
    }
    
    @Override
    public void move(double xoffset,double yoffset) {
        this.inner.move(xoffset,yoffset);
        this.outer.move(xoffset,yoffset);
    }
    @Override
    public void mirror(Line line) {
        this.inner.mirror(line);
        this.outer.mirror(line);        
    }
    @Override
    public <T extends ClearanceSource> void drawClearance(Graphics2D g2, ViewportWindow viewportWindow,
                                                          AffineTransform scale, T source) {
        
        
//        if(Utilities.isSameNet(source, this)){
//            return;
//        } 
        
        Box rect = this.outer.box();
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
    public <T extends ClearanceSource> void printClearance(Graphics2D graphics2D, PrintContext printContext,
                                                           T clearanceSource) {
        // TODO Implement this method

    }
    public Circle getInner(){
        return inner;
    }
    
    public Circle getOuter(){
        return outer;
    }
    
    
    @Override
    public Point getCenter() {
        return this.inner.pc;
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
        StringBuffer sb=new StringBuffer(); 
        sb.append("<via x=\""+Utilities.roundDouble(this.inner.pc.x)+"\" y=\""+Utilities.roundDouble(this.inner.pc.y)+"\" width=\""+this.outer.r*2+"\" drill=\""+this.inner.r*2+"\"   clearance=\""+this.clearance+"\" net=\""+(this.net==null?"":this.net)+"\" />");            
        return sb.toString();
    }

    @Override
    public void fromXML(Node node) throws XPathExpressionException, ParserConfigurationException {
        Element element=(Element)node;
        double x=(Double.parseDouble(element.getAttribute("x")));
        double y=(Double.parseDouble(element.getAttribute("y")));

        
        this.inner.pc.set(x,y);
        this.outer.pc.set(x,y);
        
        this.outer.r=(Double.parseDouble(element.getAttribute("width")))/2;
        this.inner.r=(Double.parseDouble(element.getAttribute("drill")))/2;
        
        this.clearance=element.getAttribute("clearance").equals("")?0:Integer.parseInt(element.getAttribute("clearance"));        
        this.net=element.getAttribute("net").isEmpty()?null:element.getAttribute("net");  
        
    }

    @Override
    public void paint(Graphics2D g2, ViewportWindow viewportWindow, AffineTransform scale, int layermask) {
        //is this my layer mask
        if((this.getCopper().getLayerMaskID()&layermask)==0){
            return;
        }

        Box rect = this.outer.box();
        rect.scale(scale.getScaleX());
        if (!rect.intersects(viewportWindow)) {
                return;
        }
        g2.setColor(isSelected() ? Color.GRAY : fillColor);
        
        Circle  c=this.outer.clone();
            
        c.scale(scale.getScaleX());
        c.move(-viewportWindow.getX(),- viewportWindow.getY());
        c.paint(g2, true);
        
        
        g2.setColor(Color.BLACK);
        c.r=inner.r;
        c.pc.set(inner.pc.x, inner.pc.y);
        c.scale(scale.getScaleX());
        c.move(-viewportWindow.getX(),- viewportWindow.getY());
        c.paint(g2, true);
        //if(this.isSelected()){                          
        //   Utilities.drawCrosshair(g2,  null,(int)(selectionRectWidth*scale.getScaleX()),c.getCenter());
        //}

    }
    @Override
    public void print(Graphics2D g2, PrintContext printContext, int layermask) {
          g2.setColor(printContext.getBackgroundColor()==Color.BLACK?Color.WHITE:Color.BLACK);                
          this.outer.paint(g2, true); 
          
          g2.setColor(printContext.getBackgroundColor()); 
          this.inner.paint(g2, true);
    }
    @Override
    public AbstractMemento getState(MementoType operationType) {
        AbstractMemento memento = new Memento(operationType);
        memento.saveStateFrom(this);
        return memento;
    }




    static class Memento extends AbstractMemento<Board,PCBVia>{
        private double x,y;
        private double rin,rout;
        
        
        
        
        
        public Memento(MementoType mementoType){
           super(mementoType); 
        }
        
        public void saveStateFrom(PCBVia shape) {
            super.saveStateFrom(shape);            
            this.x=shape.inner.pc.x;
            this.y=shape.inner.pc.y;
            this.rin=shape.inner.r;
            this.rout=shape.outer.r;
        }
        

        public void loadStateTo(PCBVia shape){
            super.loadStateTo(shape);
            shape.outer.pc.set(x,y);
            shape.outer.r=rout;
            
            shape.inner.pc.set(x,y);
            shape.inner.r=rin;
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
        
            return super.equals(obj) && Utils.EQ(this.x, other.x)&&Utils.EQ(this.rout,other.rout)&&
            Utils.EQ(this.y, other.y)&&Utils.EQ(this.rin,other.rin); 
                      
        }
        
        @Override
        public int hashCode(){
            int hash = 1;
            hash = super.hashCode();
            hash += Double.hashCode(this.x)+Double.hashCode(rout)+
                    Double.hashCode(this.y)+Double.hashCode(this.rin);
            return hash;
        }        
        @Override
        public boolean isSameState(Unit unit) {
            boolean flag = super.isSameState(unit);
            PCBVia other=(PCBVia)unit.getShape(getUUID());
            return flag&&Utils.EQ(this.x,other.inner.pc.x)&&Utils.EQ(this.y,other.inner.pc.y)&&Utils.EQ(this.rin,other.inner.r)&&Utils.EQ(this.rout,other.outer.r);
        }
        
    }    
}
