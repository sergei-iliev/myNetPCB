package com.mynetpcb.symbol.shape;

import com.mynetpcb.core.capi.ViewportWindow;
import com.mynetpcb.core.capi.layer.Layer;
import com.mynetpcb.core.capi.pin.Pinable;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.text.CompositeTextable;
import com.mynetpcb.core.capi.text.Texture;
import com.mynetpcb.core.capi.text.font.FontTexture;
import com.mynetpcb.core.capi.text.font.SymbolFontTexture;
import com.mynetpcb.core.capi.undo.AbstractMemento;
import com.mynetpcb.core.capi.undo.MementoType;
import com.mynetpcb.core.utils.Utilities;
import com.mynetpcb.d2.shapes.Box;
import com.mynetpcb.d2.shapes.Point;
import com.mynetpcb.d2.shapes.Segment;
import com.mynetpcb.d2.shapes.Utils;
import com.mynetpcb.symbol.unit.Symbol;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

public class Pin extends Shape implements Pinable,CompositeTextable{

    public enum PinType {
        SIMPLE,
        COMPLEX;
    }
    public enum Style {
        LINE, /*default*/
        INVERTED,
        CLOCK,
        INVERTED_CLOCK,
        INPUT_LOW,
        CLOCK_LOW,
        OUTPUT_LOW,
        FALLING_EDGE_CLOCK,
        NON_LOGIC                
    }
    private int PIN_LENGTH = 2 * Utilities.POINT_TO_POINT;
    private Segment segment;
    private PinType type;
    private Style style;
    private SymbolFontTexture name,number;
    private Pinable.Orientation orientation;
    
    public Pin() {
        super(1,Layer.LAYER_ALL);
        this.setDisplayName("Pin");             
        this.selectionRectWidth=2;
        
        
        this.segment=new Segment();
        this.type = PinType.COMPLEX;
        this.style = Style.LINE;

        this.name=new SymbolFontTexture("XXX","name",-8,0,8,0);
        this.number=new SymbolFontTexture("1","number",10,-4,8,0);
        this.init(Orientation.EAST);        
    }
    @Override
    public Pin clone() throws CloneNotSupportedException {    
        Pin copy=(Pin)super.clone();
        copy.segment=this.segment.clone();
        copy.number = this.number.clone();
        copy.name = this.name.clone();
        return copy;
    }
    @Override
    public Box getBoundingShape(){
        return this.segment.box();
    }
    private void init(Pinable.Orientation orientation){
            this.orientation=orientation;
        switch (this.orientation) {
        case EAST:        
            this.segment.pe.set(this.segment.ps.x + (this.type == PinType.COMPLEX ? PIN_LENGTH : PIN_LENGTH / 2), this.segment.ps.y);
            break;
        case WEST:
            this.segment.pe.set(this.segment.ps.x - (this.type == PinType.COMPLEX ? PIN_LENGTH : PIN_LENGTH / 2), this.segment.ps.y);       
            break;
        case NORTH:
            this.segment.pe.set(this.segment.ps.x, this.segment.ps.y - (this.type == PinType.COMPLEX ? PIN_LENGTH : PIN_LENGTH / 2));       
            break;
        case SOUTH:     
            this.segment.pe.set(this.segment.ps.x, this.segment.ps.y + (this.type == PinType.COMPLEX ? PIN_LENGTH : PIN_LENGTH / 2));
        }   
    }
    
    @Override
    public boolean isClicked(int x,int y) {
              Box rect = Box.fromRect(x
                                    - (this.selectionRectWidth / 2), y
                                    - (this.selectionRectWidth / 2), this.selectionRectWidth,
                                    this.selectionRectWidth);
              
                    if (Utils.intersectLineRectangle(
                                    this.segment.ps,this.segment.pe, rect.min, rect.max)) {                 
                            return true;
                    }else{
                            return false;
                    }                    
    }    
    
    @Override
    public void move(double xoffset,double yoffset) {
        this.segment.move(xoffset,yoffset);
            this.name.move(xoffset,yoffset);
            this.number.move(xoffset,yoffset);
    }
    public void setPinType(PinType type){
            this.type=type;
            this.init(this.orientation);
    }
    @Override
    public Point getCenter(){
       return this.segment.ps;
    }
    @Override
    public void setSelected (boolean selection) {
            super.setSelected(selection);
            this.number.setSelected(selection);
            this.name.setSelected(selection);
    }    
    @Override
    public void paint(Graphics2D g2, ViewportWindow viewportWindow, AffineTransform scale, int layermask) {
        Box rect = this.segment.box();
        rect.scale(scale.getScaleX());
        if (!rect.intersects(viewportWindow)) {
                return;
        }
        
        if (this.isSelected()) {
            g2.setColor(Color.gray);
            this.name.setFillColor(Color.gray);
            this.number.setFillColor(Color.gray);
        } else {
            g2.setColor(Color.black);
            this.name.setFillColor(Color.black);
            this.number.setFillColor(Color.black);
        }
        switch(this.style){
        case LINE:
                this.drawPinLine(g2, viewportWindow, scale,0);
                break;
//        case Style.INVERTED:
//                this.drawPinLine(g2, viewportWindow, scale,(PIN_LENGTH / 3));
//                this.drawInverted(g2, viewportWindow, scale);
//                break;    
//        case Style.CLOCK:
//                this.drawPinLine(g2, viewportWindow, scale,0);
//                this.drawTriState(g2, viewportWindow, scale);
//                break;
//        case Style.INVERTED_CLOCK:
//                this.drawPinLine(g2, viewportWindow, scale,(PIN_LENGTH / 3));
//                this.drawInverted(g2, viewportWindow, scale);
//                this.drawTriState(g2, viewportWindow, scale);
//                break;
//        case Style.INPUT_LOW:
//                this.drawPinLine(g2, viewportWindow, scale,0);
//                this.drawInputLow(g2, viewportWindow, scale);
//                break;
//        case Style.CLOCK_LOW:
//                this.drawPinLine(g2, viewportWindow, scale,0);
//                this.drawInputLow(g2, viewportWindow, scale);
//                this.drawTriState(g2, viewportWindow, scale);
//                break;
//        case  Style.OUTPUT_LOW:
//                this.drawPinLine(g2, viewportWindow, scale,0);
//                this.drawOutputLow(g2, viewportWindow, scale);
//                break;
//        case  Style.FALLING_EDGE_CLOCK:
//                this.drawPinLine(g2, viewportWindow, scale,PIN_LENGTH/ 6);
//                this.drawFallingEdgeClock(g2, viewportWindow, scale);
//                break;
                  
        }
        if (this.type == PinType.COMPLEX) {               
            this.name.paint(g2, viewportWindow, scale,0);
            this.number.paint(g2, viewportWindow, scale,0);
        }        
    }
   private void drawPinLine(Graphics2D g2, ViewportWindow viewportWindow, AffineTransform scale, int offset) { 
        Segment line=this.segment.clone();            
            
        switch (this.orientation) {
        case SOUTH:
            line.ps.set(line.ps.x,line.ps.y+offset);
            break;
        case NORTH:
            line.ps.set(line.ps.x,line.ps.y-offset);
            break;
        case WEST:
            line.ps.set(line.ps.x-offset,line.ps.y);  
            break;
        case EAST:
            line.ps.set(line.ps.x+offset,line.ps.y);        
            break;
        }
            line.scale(scale.getScaleX());
        line.move(-viewportWindow.getX(),- viewportWindow.getY());
        
        line.paint(g2,false);
    }
    @Override
    public Point getPinPoint() {        
        return segment.ps;
    }
    @Override
    public Texture getClickedTexture(int x, int y) {
        if(this.type==PinType.SIMPLE){
            return null;
        }
        if(this.name.isClicked(x, y))
        return this.name;
        else if(this.number.isClicked(x, y))
        return this.number;
        else
        return null;     
    
    }

    @Override
    public boolean isClickedTexture(int x, int y) {
        return this.getClickedTexture(x, y)!=null;
    }
    @Override
    public Texture getTextureByTag(String tag) {
        if(tag.equals(number.getTag()))
            return number;
        else if(tag.equals(name.getTag()))
            return name;
        else
        return null;
    }
    @Override
    public AbstractMemento getState(MementoType operationType) {
        AbstractMemento memento = new Memento(operationType);
        memento.saveStateFrom(this);
        return memento;
    }
    
    public static class Memento extends AbstractMemento<Symbol, Pin> {
        private double psx,psy;

        private double pex,pey;

        
        private Texture.Memento name,number;

        public Memento(MementoType mementoType) {
            super(mementoType);
            number=new FontTexture.Memento();
            name=new FontTexture.Memento();    
        }

        public void loadStateTo(Pin shape) {
            super.loadStateTo(shape);
        }

        public void saveStateFrom(Pin shape) {
            super.saveStateFrom(shape);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof Memento)) {
                return false;
            }
            Memento other = (Memento) obj;
            return (other.getUUID().equals(this.getUUID()) && other.getMementoType() == this.getMementoType());
        }

        @Override
        public int hashCode() {
            int hash = 1;
            hash =
                hash * 31 + this.getUUID().hashCode() + getMementoType().hashCode();
                
            return hash;
        }


        public void Clear() {
            super.clear();                        
        }

        public boolean isSameState(Symbol unit) {
            Pin pin = (Pin) unit.getShape(getUUID());
            return (pin.getState(getMementoType()).equals(this));
        }
    }    
    
    
}
