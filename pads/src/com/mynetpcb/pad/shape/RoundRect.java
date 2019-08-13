package com.mynetpcb.pad.shape;


import com.mynetpcb.core.capi.Externalizable;
import com.mynetpcb.core.capi.ViewportWindow;
import com.mynetpcb.core.capi.print.PrintContext;
import com.mynetpcb.core.capi.shape.ResizableShape;
import com.mynetpcb.core.capi.undo.AbstractMemento;
import com.mynetpcb.core.capi.undo.MementoType;
import com.mynetpcb.core.pad.Layer;
import com.mynetpcb.core.utils.Utilities;
import com.mynetpcb.pad.unit.Footprint;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

import org.w3c.dom.Element;
import org.w3c.dom.Node;


public class RoundRect extends ResizableShape implements  Externalizable {

    private RoundRectangle2D roundRect;

    protected int arc;

    public RoundRect(int x, int y, int width, int height, int arc, int thickness, int layermaskid) {
        super(x, y, width, height, thickness, layermaskid);
        this.roundRect = new RoundRectangle2D.Double();
        this.arc = arc;
        this.selectionRectWidth = 3000;
    }

    public RoundRect() {
        this(0, 0, 0, 0, 0, 0, Layer.SILKSCREEN_LAYER_FRONT);
    }

    @Override
    public RoundRect clone() throws CloneNotSupportedException {
        RoundRect copy = (RoundRect) super.clone();
        copy.roundRect = new RoundRectangle2D.Double();
        return copy;
    }

    public void setArc(int arc) {
        this.arc = arc;
    }

    public int getArc() {
        return arc;
    }

    @Override
    public void Clear() {
    }

    @Override
    public void setLocation(int x, int y) {
    }


    @Override
    public void Paint(Graphics2D g2, ViewportWindow viewportWindow, AffineTransform scale, int layermask) {
        //is this my layer mask
        if ((this.getCopper().getLayerMaskID() & layermask) == 0) {
            return;
        }
        Rectangle2D scaledRect = Utilities.getScaleRect(getBoundingShape().getBounds(), scale);
        if (!scaledRect.intersects(viewportWindow)) {
            return;
        }
        roundRect.setRoundRect(scaledRect.getX() - viewportWindow.x, scaledRect.getY() - viewportWindow.y,
                               scaledRect.getWidth(), scaledRect.getHeight(), arc * scale.getScaleX(),
                               arc * scale.getScaleY());

        g2.setColor(isSelected() ? Color.GRAY : copper.getColor());
        AlphaComposite composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f);
        Composite originalComposite = g2.getComposite();
        g2.setComposite(composite);
        if (fill == Fill.EMPTY) { //framed
            double wireWidth = thickness * scale.getScaleX();
            g2.setStroke(new BasicStroke((float) wireWidth, 1, 1));
            //transparent rect
            g2.draw(roundRect);
        } else { //filled
            g2.fill(roundRect);
        }
        g2.setComposite(originalComposite);
        if (this.isSelected()) {
            this.drawControlShape(g2, viewportWindow, scale);
        }
    }

    @Override
    public boolean isClicked(int x, int y) {
        roundRect.setFrame(getX(), getY(), getWidth(), getHeight());
        if (roundRect.contains(x, y))
            return true;
        else
            return false;
    }

    @Override
    public Rectangle calculateShape() {
        return new Rectangle(getX(), getY(), getWidth(), getHeight());
        //return new Rectangle(getX()-getWidth()/2,getY()-getHeight()/2,getWidth(),getHeight());
    }

    @Override
    public void Print(Graphics2D g2, PrintContext printContext, int layermask) {
        Rectangle2D rect = getBoundingShape().getBounds();
        roundRect.setRoundRect(rect.getX(), rect.getY(), rect.getWidth(), rect.getHeight(), arc, arc);

        if (thickness != -1) { //framed
            double wireWidth = thickness;
            g2.setStroke(new BasicStroke((float) wireWidth, 1, 1));
            g2.setColor(printContext.isBlackAndWhite() ? Color.BLACK : copper.getColor());
            g2.draw(roundRect);
        } else { //filled
            g2.setColor(printContext.isBlackAndWhite() ? Color.BLACK : copper.getColor());
            g2.fill(roundRect);
        }
    }

    @Override
    public String getDisplayName() {
        return "Rect";
    }

    @Override
    public String toXML() {
        return "<rectangle copper=\"" + getCopper().getName() + "\" x=\"" + upperLeft.x + "\" y=\"" + upperLeft.y +
               "\" width=\"" + getWidth() + "\" height=\"" + getHeight() + "\" thickness=\"" + this.getThickness() +
               "\" fill=\"" + this.getFill().ordinal() + "\" arc=\"" + arc + "\"/>\r\n";
    }

    @Override
    public void fromXML(Node node) {
        Element element = (Element) node;
        if (element.hasAttribute("copper")) {
            this.setCopper(Layer.Copper.valueOf(element.getAttribute("copper")));
        }
        this.setX(Integer.parseInt(element.getAttribute("x")));
        this.setY(Integer.parseInt(element.getAttribute("y")));
        this.setWidth(Integer.parseInt(element.getAttribute("width")));
        this.setHeight(Integer.parseInt(element.getAttribute("height")));
        this.arc = (Integer.parseInt(element.getAttribute("arc")));
        this.setThickness(Integer.parseInt(element.getAttribute("thickness")));
        this.setFill(Fill.values()[(element.getAttribute("fill") == "" ? 0 :
                                    Integer.parseInt(element.getAttribute("fill")))]);
    }

    @Override
    public AbstractMemento getState(MementoType operationType) {
        Memento memento = new Memento(operationType);
        memento.saveStateFrom(this);
        return memento;
    }

    @Override
    public void setState(AbstractMemento memento) {
        memento.loadStateTo(this);
    }

    public static class Memento extends ResizableShape.Memento {
        private int arc;

        public Memento(MementoType mementoType) {
            super(mementoType);
        }

        @Override
        public void loadStateTo(ResizableShape shape) {
            super.loadStateTo(shape);
            ((RoundRect) shape).arc = arc;
        }

        @Override
        public void saveStateFrom(ResizableShape shape) {
            super.saveStateFrom(shape);
            this.arc = ((RoundRect) shape).arc;
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
            return super.equals(obj) && this.arc == other.arc;
        }

        @Override
        public int hashCode() {
            int hash = 1;
            hash = super.hashCode();
            hash += this.arc;
            return hash;
        }

        public boolean isSameState(Footprint unit) {
            boolean flag = super.isSameState(unit);
            RoundRect other = (RoundRect) unit.getShape(this.getUUID());
            return other.arc == this.arc && flag;
        }
    }
}
