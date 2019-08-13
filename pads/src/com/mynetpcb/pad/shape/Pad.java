package com.mynetpcb.pad.shape;


import com.mynetpcb.core.board.ClearanceSource;
import com.mynetpcb.core.board.shape.CopperAreaShape;
import com.mynetpcb.core.capi.Grid;
import com.mynetpcb.core.capi.ViewportWindow;
import com.mynetpcb.core.capi.event.MouseScaledEvent;
import com.mynetpcb.core.capi.flyweight.FlyweightProvider;
import com.mynetpcb.core.capi.flyweight.ShapeFlyweightFactory;
import com.mynetpcb.core.capi.print.PrintContext;
import com.mynetpcb.core.capi.print.Printaware;
import com.mynetpcb.core.capi.text.ChipText;
import com.mynetpcb.core.capi.text.Text;
import com.mynetpcb.core.capi.text.font.FontTexture;
import com.mynetpcb.core.capi.undo.AbstractMemento;
import com.mynetpcb.core.capi.undo.MementoType;
import com.mynetpcb.core.pad.Layer;
import com.mynetpcb.core.pad.shape.PadShape;
import com.mynetpcb.core.utils.Utilities;
import com.mynetpcb.pad.popup.FootprintPopupMenu;
import com.mynetpcb.pad.unit.Footprint;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

import java.lang.reflect.Method;

import org.w3c.dom.Element;
import org.w3c.dom.Node;


/**
 *Pad is a composite shape consisting of circle,ellipse,rectangle combination
 * @author Sergey Iliev
 */
public class Pad extends PadShape {
    
    private int arc;

    private Drill drill;

    private PadDrawing shape;

    private Type type;

    private Point offset;

    private ChipText text;


    public Pad(int x, int y, int width, int height) {
        this(x, y, width, height, Shape.CIRCULAR);
    }

    public Pad(int x, int y, int width, int height, Shape shape) {
        super(x, y, width, height);
        this.arc = width;
        text = new ChipText();
        text.Add(new FontTexture("number", "", x, y, Text.Alignment.LEFT, 4000));
        text.Add(new FontTexture("netvalue", "", x, y, Text.Alignment.LEFT, 4000));
        this.setType(Type.THROUGH_HOLE);
        this.setShape(shape);
        this.offset = new Point();
    }

    public Pad() {
        this(0, 0, 0, 0);
    }
    public Pad clone() throws CloneNotSupportedException {
        Pad copy = (Pad) super.clone();
        copy.offset=new Point(this.offset.x,this.offset.y);
        copy.setShape(this.getShape());
        copy.setType(this.getType());
        copy.text = this.text.clone();
        if (drill != null) {
            copy.drill = drill.clone();
        }
        return copy;
    }
    @Override
    public Method showContextPopup() throws NoSuchMethodException, SecurityException {
        return FootprintPopupMenu.class
               .getDeclaredMethod("registerShapePopup",
                                  new Class[] { MouseScaledEvent.class, com.mynetpcb.core.capi.shape.Shape.class });
    }

    public void setType(Pad.Type type) {
        this.type = type;
        switch (type) {
        case THROUGH_HOLE:
            if (drill == null) {
                drill = new Drill(Grid.MM_TO_COORD(0.8), Grid.MM_TO_COORD(0.8));
                drill.setLocation(getX(), getY());
            }
            break;
        case SMD:
            if (drill != null) {
                drill.Clear();
                drill = null;
            }
            break;
        case CONNECTOR:
            if(!(this.shape instanceof CircularShape)){
                this.shape=new CircularShape();
            }
            if (drill == null) {
                drill = new Drill(Grid.MM_TO_COORD(0.8), Grid.MM_TO_COORD(0.8));
                drill.setLocation(getX(), getY());
            }            
            break;
        }
    }

    @Override
    public ChipText getChipText() {
        return text;
    }

    @Override
    public String getNetName() {
        return text.getTextureByTag("netvalue").getText();
    }

    @Override
    public void setNetName(String net) {
        text.getTextureByTag("netvalue").setText(net);
    }

    @Override
    public Point getPinPoint() {
        return new Point(getX(), getY());
    }

    public Point getOffset() {
        return offset;
    }

    public PadShape.Type getType() {
        return type;
    }

    public PadShape.Shape getShape() {
        return this.shape.getShape();
    }

    public void setShape(Shape shape) {
        switch (shape) {
        case CIRCULAR:
            this.shape = new CircularShape();
            break;
        case OVAL:
            this.shape = new OvalShape();
            break;
        case RECTANGULAR:
            this.shape = new RectangularShape();
            break;
        case POLYGON:
            this.shape = new PolygonShape();
            break;
        }
    }

    @Override
    public void setWidth(int width) {
        this.shape.setWidth(width);
    }

    public void setHeight(int height) {
        this.shape.setHeight(height);
    }

    public void setDrill(Drill drill) {
        this.drill = drill;
    }


    public Drill getDrill() {
        return drill;
    }

    public long getOrderWeight() {
        return 1;
    }

    @Override
    public void setLocation(int x, int y) {
        setX(x);
        setY(y);
        if (drill != null) {
            drill.setLocation(x, y);
        }
        text.get(0).getAnchorPoint().setLocation(0, 0);
        text.get(1).getAnchorPoint().setLocation(0, 0);
    }

    @Override
    public void Move(int xoffset, int yoffset) {
        setX(getX() + xoffset);
        setY(getY() + yoffset);
        if (drill != null) {
            drill.Move(xoffset, yoffset);
        }
        text.Move(xoffset, yoffset);
    }

    @Override
    public boolean isInRect(Rectangle r) {
        if (r.contains(getBoundingShape().getBounds().getCenterX(), getBoundingShape().getBounds().getCenterY()))
            return true;
        else
            return false;
    }

    @Override
    public Rectangle calculateShape() {
        return new Rectangle((getX() - getWidth() / 2) - offset.x, (getY() - getHeight() / 2) - offset.y, getWidth(),
                             getHeight());
    }

    @Override
    public Point alignToGrid(boolean isRequired) {
        Point point = getOwningUnit().getGrid().positionOnGrid(getX(), getY());
        this.Move(point.x - getX(), point.y - getY());
        return null;
    }

    @Override
    public boolean isClicked(int x, int y) {
        java.awt.Shape r = this.getBoundingShape();
        if (r.contains(x, y))
            return true;
        else
            return false;
    }

    @Override
    public String getDisplayName() {
        return "Pad";
    }

    @Override
    public int getDrawingOrder() {

        int order = 1;
        //is on bottom layer
        if ((this.getCopper().getLayerMaskID() & Layer.LAYER_BACK) > 0) {
            order = 1;
        }
        //is this top layer
        if ((this.getCopper().getLayerMaskID() & Layer.LAYER_FRONT) > 0) {
            order = 2;
        }

        if ((this.getCopper().getLayerMaskID() & Layer.LAYER_FRONT) > 0 &&
            (this.getCopper().getLayerMaskID() & Layer.LAYER_BACK) > 0) {
            order = 3;
        }

        return order;
    }

    @Override
    public void Clear() {
        if (drill != null)
            this.drill.Clear();
    }

    @Override
    public void Mirror(Point A, Point B) {
        Point source = new Point(x,y);
        Utilities.mirrorPoint(A, B, source);
        setX(source.x);
        setY(source.y);
        if (drill != null) {
            drill.Mirror(A, B);
        }
        text.Mirror(A, B);
    }

    @Override
    public void Translate(AffineTransform translate) {
        Point dst = new Point();
        translate.transform(new Point(getX(), getY()), dst);
        setX(dst.x);
        setY(dst.y);
        if (drill != null) {
            drill.Translate(translate);
        }
        text.Translate(translate);
    }

    @Override
    public void Rotate(AffineTransform rotation) {
        Point dst = new Point();
        rotation.transform(new Point(getX(), getY()), dst);
        setX(dst.x);
        setY(dst.y);
        int w = getWidth();
        setWidth(getHeight());
        setHeight(w);
        if (drill != null) {
            drill.Rotate(rotation);
        }
        text.Rotate(rotation);

    }

    private boolean validateClearance(ClearanceSource source){
        //is different layer and SMD -> no clearance
        if ((source.getCopper().getLayerMaskID() & this.copper.getLayerMaskID()) == 0) {
            //if(this.getType()==PadShape.Type.SMD)
               return false; //not on the same layer
        }
        //2. is same net 
        if(Utilities.isSameNet(source,this)&&source.getPadConnection()==PadShape.PadConnection.DIRECT){
            return false;
        }
        //3. is pad  within copper area
        Rectangle rect = getBoundingShape().getBounds();
        rect.grow(source.getClearance(), source.getClearance());
        
        if(!((CopperAreaShape)source).getBoundingShape().intersects(rect)){
           return false; 
        }   
  
        return true;
    }
    
    public void drawClearance(Graphics2D g2, ViewportWindow viewportWindow, AffineTransform scale,
                              ClearanceSource source) {        

        if(!validateClearance(source)){
            return;
        }
        shape.drawClearance(g2, viewportWindow, scale, source);
    }
    
    public void printClearance(Graphics2D g2,PrintContext printContext, ClearanceSource source) {
        if(!validateClearance(source)){
            return;
        }
        shape.printClearance(g2, printContext,source);
    }    

    @Override
    public void Paint(Graphics2D g2, ViewportWindow viewportWindow, AffineTransform scale, int layermask) {
        if ((this.getCopper().getLayerMaskID() & layermask) == 0) {
            return;
        }
        switch (type) {
        case THROUGH_HOLE:
            if (shape.Paint(g2, viewportWindow, scale)) {
                if (drill != null) {
                    drill.Paint(g2, viewportWindow, scale, layermask);
                }
                text.Paint(g2, viewportWindow, scale, layermask);
            }
            break;
        case SMD:
            if(shape.Paint(g2, viewportWindow, scale))
                text.Paint(g2, viewportWindow, scale, layermask);
            break;
        case CONNECTOR:
            if (shape.Paint(g2, viewportWindow, scale)) {
                if (drill != null) {
                    drill.Paint(g2, viewportWindow, scale, layermask);
                }
            }
            
        }
        
    }

    @Override
    public void Print(Graphics2D g2, PrintContext printContext, int layermask) {
        switch (type) {
        case THROUGH_HOLE:case CONNECTOR:
            shape.Print(g2, printContext, layermask);
            if (drill != null) {
                drill.Print(g2, printContext, layermask);
            }
            break;
        case SMD:
            shape.Print(g2, printContext, layermask);
            break;
        }
    }
    
    @Override
    public String toXML() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("<pad copper=\"" + getCopper().getName() + "\" type=\"" + getType() + "\" shape=\"" + getShape() +
                      "\" x=\"" + getX() + "\" y=\"" + getY() + "\" width=\"" + getWidth() + "\" height=\"" +
                      getHeight() + "\" arc=\"" + arc + "\">\r\n");
        buffer.append("<offset x=\"" + offset.x + "\" y=\"" + offset.y + "\" />\r\n");

        if (!text.getTextureByTag("number").isEmpty())
            buffer.append("<number>" + text.getTextureByTag("number").toXML() + "</number>\r\n");
        if (!text.getTextureByTag("netvalue").isEmpty())
            buffer.append("<netvalue>" + text.getTextureByTag("netvalue").toXML() + "</netvalue>\r\n");
        if (drill != null) {
            buffer.append(drill.toXML() + "\r\n");
        }
        buffer.append("</pad>\r\n");
        return buffer.toString();
    }

    @Override
    public void fromXML(Node node) {
        Element element = (Element) node;
        this.setCopper(Layer.Copper.valueOf(element.getAttribute("copper")));
        //fix copper All->copper Cu
        if(this.copper.getLayerMaskID()==Layer.LAYER_ALL){
            this.setCopper(Layer.Copper.Cu);
        }
        this.setType(com.mynetpcb
                        .pad
                        .shape
                        .Pad
                        .Type
                        .valueOf(element.getAttribute("type")));
        this.setX(Integer.parseInt(element.getAttribute("x")));
        this.setY(Integer.parseInt(element.getAttribute("y")));
        this.setWidth(Integer.parseInt(element.getAttribute("width")));
        this.setHeight(Integer.parseInt(element.getAttribute("height")));
        this.arc = (Integer.parseInt(element.getAttribute("arc")));
        this.setShape(com.mynetpcb
                         .pad
                         .shape
                         .Pad
                         .Shape
                         .valueOf(element.getAttribute("shape")));

        Element offset = (Element) element.getElementsByTagName("offset").item(0);
        this.offset.x = (Integer.parseInt(offset.getAttribute("x")));
        this.offset.y = (Integer.parseInt(offset.getAttribute("y")));
        if (drill != null) {
            drill.fromXML(element.getElementsByTagName("drill").item(0));
        }

        Element number = (Element) element.getElementsByTagName("number").item(0);
        if (number == null) {
            this.text
                .getTextureByTag("number")
                .Move(getX(), getY());
        } else {
            this.text
                .getTextureByTag("number")
                .fromXML(number);
        }
        Element netvalue = (Element) element.getElementsByTagName("netvalue").item(0);
        if (netvalue == null) {
            this.text
                .getTextureByTag("netvalue")
                .Move(getX(), getY());
        } else {
            this.text
                .getTextureByTag("netvalue")
                .fromXML(netvalue);
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

    public static class Memento extends AbstractMemento<Footprint, Pad> {

        private ChipText.Memento labelTextMemento;

        private Drill.Memento drillMemento;

        private int x, y, width, height, arc;

        private int shape;

        private int type;

        private int offsetx;

        private int offsety;

        public Memento(MementoType mementoType) {
            super(mementoType);
            labelTextMemento = new ChipText.Memento();
            drillMemento = new Drill.Memento(mementoType);
        }

        @Override
        public void Clear() {
            super.Clear();
            labelTextMemento.Clear();
            drillMemento.Clear();
        }

        @Override
        public void loadStateTo(Pad shape) {
            super.loadStateTo(shape);
            shape.setX(x);
            shape.setY(y);
            shape.setWidth(width);
            shape.setHeight(height);
            shape.arc = arc;
            shape.offset.x = offsetx;
            shape.offset.y = offsety;
            shape.setType(Type.values()[type]);
            shape.setShape(Shape.values()[this.shape]);
            labelTextMemento.loadStateTo(shape.getChipText());
            drillMemento.loadStateTo(shape.getDrill());
        }

        @Override
        public void saveStateFrom(Pad shape) {
            super.saveStateFrom(shape);
            x = shape.getX();
            y = shape.getY();
            width = shape.getWidth();
            height = shape.getHeight();
            arc = shape.arc;
            offsetx = shape.offset.x;
            offsety = shape.offset.y;
            this.shape = shape.getShape().ordinal();
            type = shape.getType().ordinal();
            labelTextMemento.saveStateFrom(shape.getChipText());
            drillMemento.saveStateFrom(shape.getDrill());
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

            return getMementoType() == other.getMementoType() && getUUID().equals(other.getUUID()) &&
                   drillMemento.equals(other.drillMemento) && labelTextMemento.equals(other.labelTextMemento) &&
                   x == other.x && y == other.y && width == other.width && height == other.height && arc == other.arc &&
                   shape == other.shape && layerindex == other.layerindex && type == other.type &&
                   offsetx == other.offsetx && offsety == other.offsety;

        }

        @Override
        public int hashCode() {
            int hash = getUUID().hashCode();
            hash +=
                getMementoType().hashCode() + x + y + width + height + arc + shape + type + offsetx + offsety +
                layerindex;
            hash += labelTextMemento.hashCode();
            hash += drillMemento.hashCode();
            return hash;
        }

        @Override
        public boolean isSameState(Footprint unit) {
            Pad pad = (Pad) unit.getShape(getUUID());
            return (pad.getState(getMementoType()).equals(this));
        }

    }


    private interface PadDrawing extends Printaware {
        public Shape getShape();

        public boolean Paint(Graphics2D g2, ViewportWindow viewportWindow, AffineTransform scale);

        public void drawClearance(Graphics2D g2, ViewportWindow viewportWindow, AffineTransform scale,
                                  ClearanceSource source);

        public void printClearance(Graphics2D g2,PrintContext printContext, ClearanceSource source);

        public void setWidth(int width);

        public void setHeight(int height);

    }

    private class CircularShape implements PadDrawing {

        private final Ellipse2D ellipse;

        private CircularShape() {
            this.ellipse = new Ellipse2D.Double();
            this.setWidth(getWidth());
        }

        @Override
        public void drawClearance(Graphics2D g2, ViewportWindow viewportWindow, AffineTransform scale,
                                  ClearanceSource source) {                            
            Rectangle rect = getBoundingShape().getBounds();
            rect.grow(source.getClearance(), source.getClearance());                                    
            
            Rectangle2D scaledRect = Utilities.getScaleRect(rect, scale);
            ellipse.setFrame(scaledRect.getX() - viewportWindow.x, scaledRect.getY() - viewportWindow.y,
                             scaledRect.getWidth(), scaledRect.getWidth());
            
            g2.setColor(Color.BLACK);
            g2.fill(ellipse);
            
            //1. THERMAL makes sense if pad has copper on source layer
            if ((source.getCopper().getLayerMaskID() & Pad.this.copper.getLayerMaskID()) == 0) {
                return; //not on the same layer
            }
            if(Utilities.isSameNet(source,Pad.this) &&source.getPadConnection()==PadShape.PadConnection.THERMAL){
               //draw thermal
                g2.setClip(ellipse);                              
                FlyweightProvider provider =ShapeFlyweightFactory.getProvider(Line2D.class);
                Line2D temporal=(Line2D)provider.getShape(); 
                //radius of outer whole
                g2.setStroke(new BasicStroke((float)((Pad.this.getWidth()/2)*scale.getScaleX()),BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));            
                g2.setColor(source.isSelected()? Color.GRAY :source.getCopper().getColor());
            

                
                temporal.setLine((scaledRect.getMinX()+(scaledRect.getHeight()/2))-viewportWindow.x, scaledRect.getMinY()-viewportWindow.y,(scaledRect.getMinX()+(scaledRect.getHeight()/2))-viewportWindow.x, scaledRect.getMaxY()-viewportWindow.y);            
                g2.draw(temporal);

                temporal.setLine(scaledRect.getMinX()-viewportWindow.x, (scaledRect.getMinY()+(scaledRect.getWidth()/2))-viewportWindow.y,scaledRect.getMaxX()-viewportWindow.x, (scaledRect.getMinY()+(scaledRect.getWidth()/2))-viewportWindow.y);            
                g2.draw(temporal);
            
                g2.setClip(null);
                
                provider.reset();
            }
        }

        @Override
        public void printClearance(Graphics2D g2,PrintContext printContext, ClearanceSource source) {
            Rectangle rect = getBoundingShape().getBounds();
            rect.grow(source.getClearance(), source.getClearance());
            ellipse.setFrame(rect.x, rect.y, rect.getWidth(), rect.getWidth());

            g2.setColor(printContext.getBackgroundColor());
            g2.fill(ellipse);                        
            
            //1. THERMAL makes sense if pad has copper on source layer
            if ((source.getCopper().getLayerMaskID() & Pad.this.copper.getLayerMaskID()) == 0) {
                return; //not on the same layer
            }
            if(Utilities.isSameNet(source,Pad.this) &&source.getPadConnection()==PadShape.PadConnection.THERMAL){
               //draw thermal
                
                g2.setColor(printContext.isBlackAndWhite() ? Color.BLACK : copper.getColor());
                
                FlyweightProvider provider =ShapeFlyweightFactory.getProvider(Line2D.class);
                Line2D temporal=(Line2D)provider.getShape(); 
                //radius of outer whole
                g2.setStroke(new BasicStroke(((Pad.this.getWidth()/2)),BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));            
                            
                
                temporal.setLine((rect.getMinX()+(rect.getHeight()/2)), rect.getMinY(),(rect.getMinX()+(rect.getHeight()/2)), rect.getMaxY());            
                g2.draw(temporal);

                temporal.setLine(rect.getMinX(), (rect.getMinY()+(rect.getWidth()/2)),rect.getMaxX(), (rect.getMinY()+(rect.getWidth()/2)));            
                g2.draw(temporal);
                            
                
                provider.reset();
            }            
        }

        @Override
        public boolean Paint(Graphics2D g2, ViewportWindow viewportWindow, AffineTransform scale) {
            Rectangle2D scaledRect = Utilities.getScaleRect(getBoundingShape().getBounds(), scale);
            if (!scaledRect.intersects(viewportWindow)) {
                return false;
            }

            ellipse.setFrame(scaledRect.getX() - viewportWindow.x, scaledRect.getY() - viewportWindow.y,
                             scaledRect.getWidth(), scaledRect.getWidth());
            
            if(getType()==Type.CONNECTOR){
              g2.setColor(isSelected() ? Color.GRAY : Color.WHITE);
            }else{    
              g2.setColor(isSelected() ? Color.GRAY : copper.getColor());
            }
            g2.fill(ellipse);

            return true;

        }

        @Override
        public void Print(Graphics2D g2, PrintContext printContext, int layermask) {
            Rectangle rect = getBoundingShape().getBounds();
            ellipse.setFrame(rect.x, rect.y, rect.getWidth(), rect.getWidth());
            g2.setColor(printContext.isBlackAndWhite() ? Color.BLACK : copper.getColor());
            g2.fill(ellipse);
        }

        @Override
        public Pad.Shape getShape() {
            return com.mynetpcb
                      .pad
                      .shape
                      .Pad
                      .Shape
                      .CIRCULAR;
        }


        @Override
        public void setWidth(int width) {
            Pad.super.setWidth(width);
            setHeight(width);
        }

        @Override
        public void setHeight(int height) {
            Pad.super.setHeight(height);
        }
    }
    
    

    private class OvalShape implements PadDrawing {

        private final RoundRectangle2D roundRect;

        public OvalShape() {
            this.roundRect = new RoundRectangle2D.Double();
        }

        @Override
        public Pad.Shape getShape() {
            return com.mynetpcb
                      .pad
                      .shape
                      .Pad
                      .Shape
                      .OVAL;
        }

        @Override
        public void drawClearance(Graphics2D g2, ViewportWindow viewportWindow, AffineTransform scale,
                                  ClearanceSource source) {
            Rectangle rect = getBoundingShape().getBounds();
            rect.grow(source.getClearance(), source.getClearance());
            double rounding = rect.getWidth() < rect.getHeight() ? rect.getWidth() : rect.getHeight();
            Rectangle2D scaledRect = Utilities.getScaleRect(rect, scale);

            roundRect.setRoundRect(scaledRect.getX() - viewportWindow.x, scaledRect.getY() - viewportWindow.y,
                                   scaledRect.getWidth(), scaledRect.getHeight(), rounding * scale.getScaleX(),
                                   rounding * scale.getScaleY());
            g2.setColor(Color.BLACK);
            g2.fill(roundRect);

            //1. THERMAL makes sense if pad has copper on source layer
            if ((source.getCopper().getLayerMaskID() & Pad.this.copper.getLayerMaskID()) == 0) {
                return; //not on the same layer
            }
            if(Utilities.isSameNet(source,Pad.this) &&source.getPadConnection()==PadShape.PadConnection.THERMAL){
               //draw thermal
                g2.setClip(roundRect);                              
                FlyweightProvider provider =ShapeFlyweightFactory.getProvider(Line2D.class);
                Line2D temporal=(Line2D)provider.getShape(); 
                
                g2.setColor(source.isSelected()? Color.GRAY :source.getCopper().getColor());
                //radius of outer whole
                g2.setStroke(new BasicStroke((float)((Pad.this.getWidth()/2)*scale.getScaleX()),BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));                                            
                temporal.setLine((scaledRect.getMinX()+(scaledRect.getWidth()/2))-viewportWindow.x, scaledRect.getMinY()-viewportWindow.y,(scaledRect.getMinX()+(scaledRect.getWidth()/2))-viewportWindow.x, scaledRect.getMaxY()-viewportWindow.y);            
                g2.draw(temporal);

                g2.setStroke(new BasicStroke((float)((Pad.this.getHeight()/2)*scale.getScaleX()),BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));            
                temporal.setLine(scaledRect.getMinX()-viewportWindow.x, (scaledRect.getMinY()+(scaledRect.getHeight()/2))-viewportWindow.y,scaledRect.getMaxX()-viewportWindow.x, (scaledRect.getMinY()+(scaledRect.getHeight()/2))-viewportWindow.y);            
                g2.draw(temporal);
            
                g2.setClip(null);
                
                provider.reset();
            }
        }

        @Override
        public void printClearance(Graphics2D g2,PrintContext printContext, ClearanceSource source) {
            Rectangle rect = getBoundingShape().getBounds();
            rect.grow(source.getClearance(), source.getClearance());
            roundRect.setRoundRect(rect.x, rect.y, rect.getWidth(), rect.getHeight(), arc, arc);
            g2.setColor(printContext.getBackgroundColor());
            g2.fill(roundRect);          

            //1. THERMAL makes sense if pad has copper on source layer
            if ((source.getCopper().getLayerMaskID() & Pad.this.copper.getLayerMaskID()) == 0) {
                return; //not on the same layer
            }
            if(Utilities.isSameNet(source,Pad.this) &&source.getPadConnection()==PadShape.PadConnection.THERMAL){
               //draw thermal
                          
                FlyweightProvider provider =ShapeFlyweightFactory.getProvider(Line2D.class);
                Line2D temporal=(Line2D)provider.getShape(); 
                
                g2.setColor(printContext.isBlackAndWhite() ? Color.BLACK : copper.getColor());
                //radius of outer whole
                g2.setStroke(new BasicStroke(((Pad.this.getWidth()/2)),BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));                                            
                temporal.setLine((roundRect.getMinX()+(roundRect.getWidth()/2)), roundRect.getMinY(),(roundRect.getMinX()+(roundRect.getWidth()/2)), roundRect.getMaxY());            
                g2.draw(temporal);

                g2.setStroke(new BasicStroke((float)((Pad.this.getHeight()/2)),BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));            
                temporal.setLine(roundRect.getMinX(), (roundRect.getMinY()+(roundRect.getHeight()/2)),roundRect.getMaxX(), (roundRect.getMinY()+(roundRect.getHeight()/2)));            
                g2.draw(temporal);            
                
                provider.reset();
            }            
        }

        @Override
        public boolean Paint(Graphics2D g2, ViewportWindow viewportWindow, AffineTransform scale) {
            Rectangle2D scaledRect = Utilities.getScaleRect(getBoundingShape().getBounds(), scale);
            roundRect.setRoundRect(scaledRect.getX() - viewportWindow.x, scaledRect.getY() - viewportWindow.y,
                                   scaledRect.getWidth(), scaledRect.getHeight(), arc * scale.getScaleX(),
                                   arc * scale.getScaleY());

            if (!scaledRect.intersects(viewportWindow)) {
                return false;
            }
            g2.setColor(isSelected() ? Color.GRAY : copper.getColor());
            g2.fill(roundRect);

            return true;
        }

        @Override
        public void Print(Graphics2D g2, PrintContext printContext, int layermask) {
            Rectangle rect = getBoundingShape().getBounds();
            roundRect.setRoundRect(rect.x, rect.y, rect.getWidth(), rect.getHeight(), arc, arc);

            g2.setColor(printContext.isBlackAndWhite() ? Color.BLACK : copper.getColor());
            g2.fill(roundRect);

        }

        @Override
        public void setWidth(int width) {
            Pad.super.setWidth(width);
            if (width < getHeight()) {
                Pad.this.arc = width;
            } else {
                Pad.this.arc = getHeight();
            }
        }

        @Override
        public void setHeight(int height) {
            Pad.super.setHeight(height);
            if (height < getWidth()) {
                Pad.this.arc = getHeight();
            } else {
                Pad.this.arc = getWidth();
            }
        }

//        private int getRoundingArc() {
//            return getWidth() < getHeight() ? getWidth() : getHeight();
//        }
    }

    private class RectangularShape implements PadDrawing {
        private final Rectangle2D rectangle;

        private RectangularShape() {
            this.rectangle = new Rectangle2D.Double();
        }

        @Override
        public boolean Paint(Graphics2D g2, ViewportWindow viewportWindow, AffineTransform scale) {
            Rectangle inner = getBoundingShape().getBounds();
            Rectangle2D scaledRect = Utilities.getScaleRect(inner, scale);
            if (!scaledRect.intersects(viewportWindow)) {
                return false;
            }
            rectangle.setFrame(scaledRect.getX() - viewportWindow.x, scaledRect.getY() - viewportWindow.y,
                               scaledRect.getWidth(), scaledRect.getHeight());

            g2.setColor(isSelected() ? Color.GRAY : copper.getColor());
            g2.fill(rectangle);

            return true;
        }

        @Override
        public void drawClearance(Graphics2D g2, ViewportWindow viewportWindow, AffineTransform scale,
                                  ClearanceSource source) {
            Rectangle rect = getBoundingShape().getBounds();
            rect.grow(source.getClearance(), source.getClearance());

            Rectangle2D scaledRect = Utilities.getScaleRect(rect, scale);
            rectangle.setFrame(scaledRect.getX() - viewportWindow.x, scaledRect.getY() - viewportWindow.y,
                               scaledRect.getWidth(), scaledRect.getHeight());

            g2.setColor(Color.BLACK);
            g2.fill(rectangle);
            
            //1. THERMAL makes sense if pad has copper on source layer
            if ((source.getCopper().getLayerMaskID() & Pad.this.copper.getLayerMaskID()) == 0) {
                return; //not on the same layer
            }
            if(Utilities.isSameNet(source,Pad.this) &&source.getPadConnection()==PadShape.PadConnection.THERMAL){
               //draw thermal
                g2.setClip(rectangle);                              
                FlyweightProvider provider =ShapeFlyweightFactory.getProvider(Line2D.class);
                Line2D temporal=(Line2D)provider.getShape(); 
                
                g2.setColor(source.isSelected()? Color.GRAY :source.getCopper().getColor());
                //radius of outer whole
                g2.setStroke(new BasicStroke((float)((Pad.this.getWidth()/2)*scale.getScaleX()),BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));                                            
                temporal.setLine((scaledRect.getMinX()+(scaledRect.getWidth()/2))-viewportWindow.x, scaledRect.getMinY()-viewportWindow.y,(scaledRect.getMinX()+(scaledRect.getWidth()/2))-viewportWindow.x, scaledRect.getMaxY()-viewportWindow.y);            
                g2.draw(temporal);

                g2.setStroke(new BasicStroke((float)((Pad.this.getHeight()/2)*scale.getScaleX()),BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));            
                temporal.setLine(scaledRect.getMinX()-viewportWindow.x, (scaledRect.getMinY()+(scaledRect.getHeight()/2))-viewportWindow.y,scaledRect.getMaxX()-viewportWindow.x, (scaledRect.getMinY()+(scaledRect.getHeight()/2))-viewportWindow.y);            
                g2.draw(temporal);
            
                g2.setClip(null);
                
                provider.reset();
            }            
            
        }

        @Override
        public void printClearance(Graphics2D g2,PrintContext printContext, ClearanceSource source) {
            Rectangle rect = getBoundingShape().getBounds();
            rect.grow(source.getClearance(), source.getClearance());
            rectangle.setFrame(rect.x, rect.y, rect.getWidth(), rect.getHeight());

            g2.setColor(printContext.getBackgroundColor());
            g2.fill(rectangle);

            //1. THERMAL makes sense if pad has copper on source layer
            if ((source.getCopper().getLayerMaskID() & Pad.this.copper.getLayerMaskID()) == 0) {
                return; //not on the same layer
            }
            if(Utilities.isSameNet(source,Pad.this) &&source.getPadConnection()==PadShape.PadConnection.THERMAL){
               //draw thermal                              
                FlyweightProvider provider =ShapeFlyweightFactory.getProvider(Line2D.class);
                Line2D temporal=(Line2D)provider.getShape(); 
                
                g2.setColor(printContext.isBlackAndWhite() ? Color.BLACK : copper.getColor());
                //radius of outer whole
                g2.setStroke(new BasicStroke(((Pad.this.getWidth()/2)),BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));                                            
                temporal.setLine((rectangle.getMinX()+(rectangle.getWidth()/2)), rectangle.getMinY(),(rectangle.getMinX()+(rectangle.getWidth()/2)), rectangle.getMaxY());            
                g2.draw(temporal);
                
                g2.setStroke(new BasicStroke((float)((Pad.this.getHeight()/2)),BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));            
                temporal.setLine(rectangle.getMinX(), (rectangle.getMinY()+(rectangle.getHeight()/2)),rectangle.getMaxX(), (rectangle.getMinY()+(rectangle.getHeight()/2)));            
                g2.draw(temporal);
                
                provider.reset();
            }            

        }

        @Override
        public void Print(Graphics2D g2, PrintContext printContext, int layermask) {
            Rectangle rect = getBoundingShape().getBounds();
            rectangle.setFrame(rect.x, rect.y, rect.getWidth(), rect.getHeight());
            g2.setColor(printContext.isBlackAndWhite() ? Color.BLACK : copper.getColor());
            g2.fill(rectangle);

        }

        @Override
        public Pad.Shape getShape() {
            return com.mynetpcb
                      .pad
                      .shape
                      .Pad
                      .Shape
                      .RECTANGULAR;
        }

        @Override
        public void setWidth(int width) {
            Pad.super.setWidth(width);
        }

        @Override
        public void setHeight(int height) {
            Pad.super.setHeight(height);
        }
    }

    private class PolygonShape implements PadDrawing {
        GeneralPath polygon;
        
        public PolygonShape() {
            this.setWidth(getWidth());
            polygon=initPoints(getWidth() / 2);
        }

        private GeneralPath initPoints(double r) {
           
            double da = (2 * Math.PI) / 6;
            double lim = (2 * Math.PI) - (da / 2);

            
            GeneralPath p = new GeneralPath(GeneralPath.WIND_EVEN_ODD, 6);
            p.moveTo(r * Math.cos(0), r * Math.sin(0));            
            for (double a = da; a < lim; a += da) {
                p.lineTo(r * Math.cos(a),r * Math.sin(a));
            }
            p.closePath();
            return p;
        }

        @Override
        public Pad.Shape getShape() {
            return Pad.Shape.POLYGON;
        }

        @Override
        public boolean Paint(Graphics2D g2, ViewportWindow viewportWindow, AffineTransform scale) {
            Rectangle2D scaledRect = Utilities.getScaleRect(getBoundingShape().getBounds(), scale);
            if (!scaledRect.intersects(viewportWindow)) {
                return false;
            }

            FlyweightProvider provider =ShapeFlyweightFactory.getProvider(GeneralPath.class);
            GeneralPath temporal=(GeneralPath)provider.getShape();
            
            temporal.append(polygon,true);
            
            AffineTransform translate = AffineTransform.getTranslateInstance(getX(), getY());
            temporal.transform(translate);

            translate = AffineTransform.getTranslateInstance(-viewportWindow.x, -viewportWindow.y);
            temporal.transform(scale);
            temporal.transform(translate);

            g2.setColor(isSelected() ? Color.GRAY : copper.getColor());
            g2.fill(temporal);
        
            provider.reset();
            return true;
        }

        @Override
        public void Print(Graphics2D g2, PrintContext printContext, int i) {
            FlyweightProvider provider =ShapeFlyweightFactory.getProvider(GeneralPath.class);
            GeneralPath temporal=(GeneralPath)provider.getShape();
            
            temporal.append(polygon,true);
            
            AffineTransform translate = AffineTransform.getTranslateInstance(getX(), getY());
            temporal.transform(translate);


            g2.setColor(printContext.isBlackAndWhite() ? Color.BLACK : copper.getColor());
            g2.fill(temporal);
            
            provider.reset();
        }
        
        @Override
        public void drawClearance(Graphics2D g2, ViewportWindow viewportWindow, AffineTransform scale,
                                  ClearanceSource source) {
         
            GeneralPath temporal=initPoints((getWidth()+2*source.getClearance())/2);
            AffineTransform translate = AffineTransform.getTranslateInstance(getX(), getY());
            temporal.transform(translate);

            translate = AffineTransform.getTranslateInstance(-viewportWindow.x, -viewportWindow.y);
            temporal.transform(scale);
            temporal.transform(translate);

            g2.setColor(Color.BLACK);
            g2.fill(temporal);    

        }

        @Override
        public void printClearance(Graphics2D g2,PrintContext printContext, ClearanceSource source) {

            GeneralPath temporal=initPoints((getWidth()+2*source.getClearance())/2);
            AffineTransform translate = AffineTransform.getTranslateInstance(getX(), getY());
            temporal.transform(translate);

            g2.setColor(printContext.getBackgroundColor());
            g2.fill(temporal); 
            
        }

        @Override
        public void setWidth(int width) {
            Pad.super.setWidth(width);
            setHeight(width);
            polygon=initPoints(getWidth() / 2);
        }

        @Override
        public void setHeight(int height) {
            Pad.super.setHeight(height);
        }

    }

}
