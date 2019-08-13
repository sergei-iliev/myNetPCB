package com.mynetpcb.circuit.shape;

import com.mynetpcb.circuit.unit.Circuit;
import com.mynetpcb.core.capi.Externalizable;
import com.mynetpcb.core.capi.ViewportWindow;
import com.mynetpcb.core.capi.flyweight.FlyweightProvider;
import com.mynetpcb.core.capi.flyweight.ShapeFlyweightFactory;
import com.mynetpcb.core.capi.print.PrintContext;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.text.font.FontTexture;
import com.mynetpcb.core.capi.text.Text;
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

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class SCHNetLabel extends Shape implements  Externalizable{
    private static final int OFFSET=1;
    private FontTexture texture;
    
    public SCHNetLabel() {
        super(0, 0, 0, 0, -1,0);
        this.setSelectionRectWidth(3);
        texture=new FontTexture("net.name", "SPI", 0, 0, Text.Alignment.LEFT, Utilities.POINT_TO_POINT);
        setTextLocation();        
        texture.setFillColor(Color.BLUE);
    }

    public SCHNetLabel clone() throws CloneNotSupportedException {
        SCHNetLabel copy = (SCHNetLabel)super.clone();
        copy.texture = this.texture.clone();
        return copy;
    }
    
    @Override
    public Point alignToGrid(boolean isRequired) {
//        Point point=getOwningUnit().getGrid().positionOnGrid(getX(),getY());             
//        setX(point.x);
//        setY(point.y);
        super.alignToGrid(isRequired);
        setTextLocation();
        return null;
    }
    public String getText(){
       return this.texture.getText(); 
    }
    public void setText(String text){
       this.texture.setText(text); 
    }    
    
    public Text.Orientation getOrientation(){
        return this.texture.getAlignment().getOrientation();
    }
    
    @Override
    public String getDisplayName() {
        return "NetLabel";
    }
    
    
    @Override
    public Rectangle calculateShape() {
       return texture.getBoundingShape();
    }
    
    @Override
    public void Move(int xoffset, int yoffset) {
        setX(getX() + xoffset);
        setY(getY() + yoffset);
        setTextLocation();
    }

    @Override
    public void Mirror(Point A,Point B) {
        texture.Mirror(A,B);
    }

    @Override
    public void Translate(AffineTransform translate) {
        Point point = new Point(getX(), getY());
        translate.transform(point, point);
        setX(point.x);
        setY(point.y);
        setTextLocation();
        
    }

    @Override
    public void Rotate(AffineTransform rotation) {
        Point point = new Point(getX(), getY());
        rotation.transform(point, point);
        setX(point.x);
        setY(point.y);
        texture.Rotate(rotation);
        setTextLocation();
    }
    
    private void setTextLocation(){
        if(texture.getAlignment().getOrientation()==Text.Orientation.HORIZONTAL){
          texture.setAlignment(Text.Alignment.LEFT);
          texture.setLocation(getX()+OFFSET,getY()-OFFSET);
        }else{
          texture.setAlignment(Text.Alignment.BOTTOM);
          texture.setLocation(getX()-OFFSET,getY()-OFFSET);  
        }
    }
    
    @Override
    public void fromXML(Node node) throws XPathExpressionException, ParserConfigurationException {        
        Element element=(Element)node;
        setX(Integer.parseInt(element.getAttribute("x")));
        setY(Integer.parseInt(element.getAttribute("y")));
        this.texture.fromXML(node);
    }
    
    @Override
    public String toXML() {        
        return "<netlabel x=\""+getX()+"\" y=\""+getY()+"\">" + texture.toXML() + "</netlabel>\r\n";        
    }
    
    @Override
    public void Paint(Graphics2D g2, ViewportWindow viewportWindow, AffineTransform scale, int layermask) {
        Rectangle2D scaledRect = Utilities.getScaleRect(getBoundingShape().getBounds(), scale);
        if (!scaledRect.intersects(viewportWindow)) {
            return;
        }
     
        texture.Paint(g2, viewportWindow, scale,layermask);
        if(isSelected()){
          drawControlShape(g2, viewportWindow, scale);
        }
    }
    private void drawControlShape(Graphics2D g2, ViewportWindow viewportWindow, AffineTransform scale){
        FlyweightProvider provider=ShapeFlyweightFactory.getProvider(Ellipse2D.class);
        Ellipse2D ellipse=(Ellipse2D)provider.getShape();
        
        g2.setColor(Color.GRAY);
        g2.setStroke(new BasicStroke(1));
               
        Utilities.setScaleRect(getX()-selectionRectWidth , getY()-selectionRectWidth, 2*selectionRectWidth, 2*selectionRectWidth,ellipse,scale);
        ellipse.setFrame(ellipse.getMinX()-viewportWindow.x, ellipse.getMinY()-viewportWindow.y, ellipse.getWidth(),ellipse.getHeight());
    
        g2.draw(ellipse); 

        provider.reset();
    }
    @Override
    public void Print(Graphics2D g2,PrintContext printContext, int layermask) {
        texture.Paint(g2, new ViewportWindow(0, 0, 0, 0), AffineTransform.getScaleInstance(1, 1),layermask);
    }
    
    public AbstractMemento getState(MementoType operationType) {
        AbstractMemento memento = new Memento(operationType);
        memento.saveStateFrom(this);
        return memento;
    }

    public void setState(AbstractMemento memento) {
        memento.loadStateTo(this);
    }
    
    static class Memento extends AbstractMemento<Circuit, SCHNetLabel> {

        FontTexture.Memento textureMemento;
        private int Ax;        
        private int Ay;
        
        public Memento(MementoType mementoType) {
            super(mementoType);
            textureMemento = new FontTexture.Memento();
        }

        @Override
        public void loadStateTo(SCHNetLabel shape) {
            super.loadStateTo(shape);
            shape.setX(Ax);
            shape.setY(Ay);
            textureMemento.loadStateTo(shape.texture);
        }

        @Override
        public void saveStateFrom(SCHNetLabel shape) {
            super.saveStateFrom(shape);
            Ax=shape.getX();
            Ay=shape.getY();
            textureMemento.saveStateFrom(shape.texture);            
        }
        
        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof Memento)) {
                return false;
            }

            Memento other = (Memento)obj;

            return (this.getUUID().equals(other.getUUID()) && getMementoType() == other.getMementoType() &&
                    textureMemento.equals(other.textureMemento)&&
                    Ax==other.Ax&&
                    Ay==other.Ay);
        }

        @Override
        public int hashCode() {
            int hash = getUUID().hashCode();
            hash += getMementoType().hashCode();
            hash += textureMemento.hashCode();
            hash+=Ax+Ay;
            return hash;
        }

        public boolean isSameState(Circuit unit) {
            SCHNetLabel label = (SCHNetLabel)unit.getShape(getUUID());
            return (label.getState(getMementoType()).equals(this));
        }
    }    
    
}
