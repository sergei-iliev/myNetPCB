package com.mynetpcb.symbol.shape;


import com.mynetpcb.core.capi.Externalizable;
import com.mynetpcb.core.capi.PinLineable;
import com.mynetpcb.core.capi.Pinable;
import com.mynetpcb.core.capi.ViewportWindow;
import com.mynetpcb.core.capi.event.MouseScaledEvent;
import com.mynetpcb.core.capi.flyweight.FlyweightProvider;
import com.mynetpcb.core.capi.flyweight.ShapeFlyweightFactory;
import com.mynetpcb.core.capi.print.PrintContext;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.text.ChipText;
import com.mynetpcb.core.capi.text.Text;
import com.mynetpcb.core.capi.text.Textable;
import com.mynetpcb.core.capi.text.Texture;
import com.mynetpcb.core.capi.text.font.FontTexture;
import com.mynetpcb.core.capi.undo.AbstractMemento;
import com.mynetpcb.core.capi.undo.MementoType;
import com.mynetpcb.core.utils.Utilities;
import com.mynetpcb.symbol.popup.SymbolPopupMenu;
import com.mynetpcb.symbol.unit.Symbol;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import java.lang.reflect.Method;

import java.util.Collection;
import java.util.StringTokenizer;

import org.w3c.dom.Element;
import org.w3c.dom.Node;


public class Pin extends Shape implements PinLineable, Textable, Externalizable {

    public enum Type {
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

    private Pair points;

    private Orientation orientation;

    private Type type;

    private ChipText text;

    private Style style;

    public Pin(int x, int y) {
        super(x, y, 0, 0, 1, 0);
        //startPoint = new Point(x, y);
        orientation = Pinable.Orientation.WEST;
        type = Type.COMPLEX;
        text = new ChipText();
        text.Add(new FontTexture("pinname", "XXX", 6, 2, Text.Alignment.LEFT, 8));
        text.Add(new FontTexture("pinnumber", "0", -6, -1, Text.Alignment.RIGHT, 8));
        text.setFillColor(Color.BLACK);
        this.style = Style.LINE;
        points = new Pair();
    }

    public Pin() {
        this(0, 0);

    }

    @Override
    public Pin clone() throws CloneNotSupportedException {
        Pin copy = (Pin) super.clone();
        copy.text = this.text.clone();
        copy.points = new Pair();
        return copy;
    }

    @Override
    public Method showContextPopup() throws NoSuchMethodException, SecurityException {
        return SymbolPopupMenu.class.getDeclaredMethod("registerShapePopup", new Class[] {
                                                       MouseScaledEvent.class, Shape.class });
    }

    public void setStyle(Style style) {
        this.style = style;
    }

    public Style getStyle() {
        return style;
    }

    @Override
    public Point getPinPoint() {
        return new Point(getX(), getY());
    }

    public long getOrderWeight() {
        return 3;
    }

    @Override
    public ChipText getChipText() {
        return text;
    }

    @Override
    public Collection<Texture> getPinText() {

        return text.getChildren();
    }


    @Override
    public Point alignToGrid(boolean isRequired) {
        Point point = getOwningUnit().getGrid().positionOnGrid(getX(), getY());
        text.Move(point.x - getX(), point.y - getY());
        this.setX(point.x);
        this.setY(point.y);
        return new Point(point.x - getX(), point.y - getY());
    }

    @Override
    public boolean isClicked(int x, int y) {
        boolean result = false;
        FlyweightProvider rectProvider = ShapeFlyweightFactory.getProvider(Rectangle2D.class);
        Rectangle2D rect = (Rectangle2D) rectProvider.getShape();

        rect.setRect(x - selectionRectWidth / 2, y - selectionRectWidth / 2, selectionRectWidth, selectionRectWidth);
        Line2D pinLine = getPinLine();

        if (pinLine.intersects(rect))
            result = true;
        else
            result = false;

        rectProvider.reset();
        return result;
    }

    public void Move(int xOffset, int yOffset) {
        this.setLocation(getX() + xOffset, getY() + yOffset);
        text.Move(xOffset, yOffset);
    }

    @Override
    public void Mirror(Point A, Point B) {
        int x = getX(), y = getY();
        //***is this right-left mirroring
        if (A.x == B.x) {
            //***which place in regard to x origine
            if ((x - A.x) < 0)
                x = A.x + (A.x - x);
            else
                x = A.x - (x - A.x);

            this.orientation = orientation.Mirror(true);
        } else { //***top-botom mirroring
            //***which place in regard to y origine
            if ((y - A.y) < 0)
                y = A.y + (A.y - y);
            else
                y = A.y - (y - A.y);

            this.orientation = orientation.Mirror(false);
        }
        this.setLocation(x, y);
        //***investigate orientation
        text.Mirror(A, B);
    }

    public void Translate(AffineTransform translate) {
        Point point = new Point(getX(), getY());
        translate.transform(point, point);
        this.setLocation(point.x, point.y);
        text.Translate(translate);
    }

    public void Rotate(AffineTransform rotation) {
        //***rotate start_point
        //Point dst = new Point();
        Point point = new Point(getX(), getY());
        rotation.transform(point, point);
        this.setLocation(point.x, point.y);
        //***tweak orientation according to rotation direction
        this.orientation = orientation.Rotate(rotation.getShearY() > 0 ? true : false);
        text.Rotate(rotation);
    }

    public Point getPinEnd() {
        Point endPoint = new Point();
        switch (orientation) {
        case EAST:
            endPoint.setLocation(getX() + (type == Type.COMPLEX ? PIN_LENGTH : PIN_LENGTH / 2), getY());
            break;
        case WEST:
            endPoint.setLocation(getX() - (type == Type.COMPLEX ? PIN_LENGTH : PIN_LENGTH / 2), getY());
            break;
        case NORTH:
            endPoint.setLocation(getX(), getY() - (type == Type.COMPLEX ? PIN_LENGTH : PIN_LENGTH / 2));
            break;
        case SOUTH:
            endPoint.setLocation(getX(), getY() + (type == Type.COMPLEX ? PIN_LENGTH : PIN_LENGTH / 2));
        }
        return endPoint;
    }

    //origin keeps its position in regard to orientation
    @Override
    public Pair getPinPoints() {
        
        switch (orientation) {
        case EAST:
            points.getA().setLocation(getX(), getY());
            points.getB().setLocation(getX() + (type == Type.COMPLEX ? PIN_LENGTH : PIN_LENGTH / 2), getY());
            break;
        case WEST:
            points.getA().setLocation(getX() - (type == Type.COMPLEX ? PIN_LENGTH : PIN_LENGTH / 2), getY());
            points.getB().setLocation(getX(), getY());
            break;
        case NORTH:
            points.getA().setLocation(getX(), getY() - (type == Type.COMPLEX ? PIN_LENGTH : PIN_LENGTH / 2));
            points.getB().setLocation(getX(), getY());
            break;
        case SOUTH:
            points.getA().setLocation(getX(), getY());
            points.getB().setLocation(getX(), getY() + (type == Type.COMPLEX ? PIN_LENGTH : PIN_LENGTH / 2));
        }
        return points;
    }

    private Line2D getPinLine() {
        Line2D line = new Line2D.Double();
        Pair pair=getPinPoints();
        line.setLine(pair.getA(),pair.getB());
        return line;
//        Line2D line = null;
//        switch (orientation) {
//
//        case EAST:
//            line =
//                new Line2D.Double(getX(), getY(), getX() + (type == Type.COMPLEX ? PIN_LENGTH : PIN_LENGTH / 2),
//                                  getY());
//            break;
//        case WEST:
//            line =
//                new Line2D.Double(getX() - (type == Type.COMPLEX ? PIN_LENGTH : PIN_LENGTH / 2), getY(), getX(),
//                                  getY());
//
//            break;
//        case NORTH:
//            line =
//                new Line2D.Double(getX(), getY() - (type == Type.COMPLEX ? PIN_LENGTH : PIN_LENGTH / 2), getX(),
//                                  getY());
//
//            break;
//        case SOUTH:
//            line =
//                new Line2D.Double(getX(), getY(), getX(),
//                                  getY() + (type == Type.COMPLEX ? PIN_LENGTH : PIN_LENGTH / 2));
//
//        }
//        return line;
    }

    /**
     *Paint helper method.
     * Called in the context of Paint method. It is using flywaigth provider!!!
     */
    public void drawInputLow(Graphics2D g2, double x, double y, ViewportWindow viewportWindow, AffineTransform scale) {
        double pinlength = PIN_LENGTH * scale.getScaleX();
        FlyweightProvider lineProvider = ShapeFlyweightFactory.getProvider(Line2D.class);
        Line2D line1 = (Line2D) lineProvider.getShape();
        Line2D line2 = (Line2D) lineProvider.getShape();
        switch (orientation) {
        case SOUTH:
            line1.setLine(x - viewportWindow.x, y - viewportWindow.y, x - (pinlength / 6) - viewportWindow.x,
                          y + (pinlength / 3) - viewportWindow.y);
            line2.setLine(x - (pinlength / 6) - viewportWindow.x, y + (pinlength / 3) - viewportWindow.y,
                          x - viewportWindow.x, y + (pinlength / 3) - viewportWindow.y);
            break;
        case NORTH:
            line1.setLine(x - viewportWindow.x, y - viewportWindow.y, x - (pinlength / 6) - viewportWindow.x,
                          y - (pinlength / 3) - viewportWindow.y);
            line2.setLine(x - (pinlength / 6) - viewportWindow.x, y - (pinlength / 3) - viewportWindow.y,
                          x - viewportWindow.x, y - (pinlength / 3) - viewportWindow.y);
            break;
        case WEST:
            line1.setLine(x - viewportWindow.x, y - viewportWindow.y, x - (pinlength / 3) - viewportWindow.x,
                          y - (pinlength / 6) - viewportWindow.y);
            line2.setLine(x - (pinlength / 3) - viewportWindow.x, y - (pinlength / 6) - viewportWindow.y,
                          x - (pinlength / 3) - viewportWindow.x, y - viewportWindow.y);
            break;
        case EAST:
            line1.setLine(x - viewportWindow.x, y - viewportWindow.y, x + (pinlength / 3) - viewportWindow.x,
                          y - (pinlength / 6) - viewportWindow.y);
            line2.setLine(x + (pinlength / 3) - viewportWindow.x, y - (pinlength / 6) - viewportWindow.y,
                          x + (pinlength / 3) - viewportWindow.x, y - viewportWindow.y);
            break;
        }
        g2.draw(line1);
        g2.draw(line2);

    }

    /**
     *Paint helper method.
     * Called in the context of Paint method. It is using flywaigth provider!!!
     */
    private void drawFallingEdgeClock(Graphics2D g2, double x, double y, ViewportWindow viewportWindow,
                                      AffineTransform scale) {
        double pinlength = PIN_LENGTH * scale.getScaleX();
        FlyweightProvider lineProvider = ShapeFlyweightFactory.getProvider(Line2D.class);
        Line2D line1 = (Line2D) lineProvider.getShape();
        Line2D line2 = (Line2D) lineProvider.getShape();
        switch (orientation) {
        case SOUTH:
            line1.setLine(x - pinlength / 6 - viewportWindow.x, y - viewportWindow.y, x - viewportWindow.x,
                          y + pinlength / 6 - viewportWindow.y);
            line2.setLine(x + pinlength / 6 - viewportWindow.x, y - viewportWindow.y, x - viewportWindow.x,
                          y + pinlength / 6 - viewportWindow.y);
            break;
        case NORTH:
            line1.setLine(x - pinlength / 6 - viewportWindow.x, y - viewportWindow.y, x - viewportWindow.x,
                          y - pinlength / 6 - viewportWindow.y);
            line2.setLine(x + pinlength / 6 - viewportWindow.x, y - viewportWindow.y, x - viewportWindow.x,
                          y - pinlength / 6 - viewportWindow.y);
            break;
        case WEST:
            line1.setLine(x - viewportWindow.x, y - pinlength / 6 - viewportWindow.y,
                          x - pinlength / 6 - viewportWindow.x, y - viewportWindow.y);
            line2.setLine(x - viewportWindow.x, y + pinlength / 6 - viewportWindow.y,
                          x - pinlength / 6 - viewportWindow.x, y - viewportWindow.y);
            break;
        case EAST:
            line1.setLine(x - viewportWindow.x, y - pinlength / 6 - viewportWindow.y,
                          x + pinlength / 6 - viewportWindow.x, y - viewportWindow.y);
            line2.setLine(x - viewportWindow.x, y + pinlength / 6 - viewportWindow.y,
                          x + pinlength / 6 - viewportWindow.x, y - viewportWindow.y);
            break;
        }
        g2.draw(line1);
        g2.draw(line2);
    }

    /**
     *Paint helper method.
     * Called in the context of Paint method. It is using flywaigth provider!!!
     */
    private void drawNonLogic(Graphics2D g2, double x, double y, ViewportWindow viewportWindow, AffineTransform scale) {
        double pinlength = PIN_LENGTH * scale.getScaleX();
        FlyweightProvider lineProvider = ShapeFlyweightFactory.getProvider(Line2D.class);
        Line2D line1 = (Line2D) lineProvider.getShape();
        Line2D line2 = (Line2D) lineProvider.getShape();

        line1.setLine(x - pinlength / 6 - viewportWindow.x, y - pinlength / 6 - viewportWindow.y,
                      x + pinlength / 6 - viewportWindow.x, y + pinlength / 6 - viewportWindow.y);
        line2.setLine(x + pinlength / 6 - viewportWindow.x, y - pinlength / 6 - viewportWindow.y,
                      x - pinlength / 6 - viewportWindow.x, y + pinlength / 6 - viewportWindow.y);

        g2.draw(line1);
        g2.draw(line2);

        //lineProvider.reset();
    }

    private void drawTriState(Graphics2D g2, double x, double y, ViewportWindow viewportWindow, AffineTransform scale) {
        double pinlength = PIN_LENGTH * scale.getScaleX();
        FlyweightProvider lineProvider = ShapeFlyweightFactory.getProvider(Line2D.class);
        Line2D line1 = (Line2D) lineProvider.getShape();
        Line2D line2 = (Line2D) lineProvider.getShape();

        switch (orientation) {
        case SOUTH:
            line1.setLine(x - pinlength / 6 - viewportWindow.x, y - viewportWindow.y, x - viewportWindow.x,
                          y - pinlength / 6 - viewportWindow.y);
            line2.setLine(x + pinlength / 6 - viewportWindow.x, y - viewportWindow.y, x - viewportWindow.x,
                          y - pinlength / 6 - viewportWindow.y);
            break;
        case NORTH:
            line1.setLine(x - pinlength / 6 - viewportWindow.x, y - viewportWindow.y, x - viewportWindow.x,
                          y + pinlength / 6 - viewportWindow.y);
            line2.setLine(x + pinlength / 6 - viewportWindow.x, y - viewportWindow.y, x - viewportWindow.x,
                          y + pinlength / 6 - viewportWindow.y);
            break;
        case WEST:
            line1.setLine(x - viewportWindow.x, y - pinlength / 6 - viewportWindow.y,
                          x + pinlength / 6 - viewportWindow.x, y - viewportWindow.y);
            line2.setLine(x - viewportWindow.x, y + pinlength / 6 - viewportWindow.y,
                          x + pinlength / 6 - viewportWindow.x, y - viewportWindow.y);
            break;
        case EAST:
            line1.setLine(x - viewportWindow.x, y - pinlength / 6 - viewportWindow.y,
                          x - pinlength / 6 - viewportWindow.x, y - viewportWindow.y);
            line2.setLine(x - viewportWindow.x, y + pinlength / 6 - viewportWindow.y,
                          x - pinlength / 6 - viewportWindow.x, y - viewportWindow.y);
            break;
        }
        g2.draw(line1);
        g2.draw(line2);
    }

    @Override
    public Rectangle calculateShape() {
        Rectangle r = getPinLine().getBounds();
        if (this.type == Type.COMPLEX && !this.text.isEmpty()) {
            r.add(this.text.getBoundingShape());
        } else {
            Utilities.IncrementRect(r, 1, 1);
        }
        return r;
    }

    @Override
    public void Paint(Graphics2D g2, ViewportWindow viewportWindow, AffineTransform scale, int layermask) {
        Rectangle2D scaledBoundingRect = Utilities.getScaleRect(getBoundingShape().getBounds(), scale);

        if (!scaledBoundingRect.intersects(viewportWindow)) {
            return;
        }
        //Flyweight
        FlyweightProvider lineProvider = ShapeFlyweightFactory.getProvider(Line2D.class);
        FlyweightProvider ellipseProvider = ShapeFlyweightFactory.getProvider(Ellipse2D.class);

        Line2D scaledLine = (Line2D) lineProvider.getShape();
        Utilities.setScaleLine(getPinLine(), scaledLine, scale);

        Line2D subline = (Line2D) lineProvider.getShape();
        Ellipse2D circle = (Ellipse2D) ellipseProvider.getShape();

        g2.setStroke(new BasicStroke(1));

        double invertCircleRadios = (PIN_LENGTH / 3) * scale.getScaleX();
        double pinlength = PIN_LENGTH * scale.getScaleX();

        g2.setColor(Color.black);
        switch (orientation) {
        case EAST:
            if (type == Type.COMPLEX) {
                switch (style) {
                case LINE:
                case CLOCK:
                case NON_LOGIC:
                case OUTPUT_LOW:
                    subline.setLine(scaledLine.getX1() - viewportWindow.x, scaledLine.getY1() - viewportWindow.y,
                                    scaledLine.getX2() - viewportWindow.x, scaledLine.getY2() - viewportWindow.y);
                    g2.draw(subline);
                    break;
                case INVERTED:
                case INVERTED_CLOCK:
                    subline.setLine((scaledLine.getX1() - viewportWindow.x) + invertCircleRadios,
                                    scaledLine.getY1() - viewportWindow.y, scaledLine.getX2() - viewportWindow.x,
                                    scaledLine.getY2() - viewportWindow.y);
                    g2.draw(subline);

                    circle.setFrame(scaledLine.getX1() - viewportWindow.x,
                                    scaledLine.getY1() - viewportWindow.y - invertCircleRadios / 2, invertCircleRadios,
                                    invertCircleRadios);
                    g2.draw(circle);

                    break;
                case INPUT_LOW:
                case CLOCK_LOW:
                    subline.setLine(scaledLine.getX1() - viewportWindow.x, scaledLine.getY1() - viewportWindow.y,
                                    scaledLine.getX2() - viewportWindow.x, scaledLine.getY2() - viewportWindow.y);
                    g2.draw(subline);
                    this.drawInputLow(g2, scaledLine.getX1(), scaledLine.getY1(), viewportWindow, scale);
                    break;
                case FALLING_EDGE_CLOCK:
                    subline.setLine(scaledLine.getX1() + (pinlength / 6) - viewportWindow.x,
                                    scaledLine.getY1() - viewportWindow.y, scaledLine.getX2() - viewportWindow.x,
                                    scaledLine.getY2() - viewportWindow.y);

                    g2.draw(subline);
                    this.drawFallingEdgeClock(g2, scaledLine.getX1(), scaledLine.getY1(), viewportWindow, scale);
                    break;
                }

                switch (style) {
                case CLOCK:
                case INVERTED_CLOCK:
                case CLOCK_LOW:
                    this.drawTriState(g2, scaledLine.getX1(), scaledLine.getY1(), viewportWindow, scale);
                    break;
                case OUTPUT_LOW:
                    subline.setLine(scaledLine.getX1() - viewportWindow.x,
                                    scaledLine.getY1() - (pinlength / 6) - viewportWindow.y,
                                    scaledLine.getX1() + (pinlength / 3) - viewportWindow.x,
                                    scaledLine.getY1() - viewportWindow.y);
                    g2.draw(subline);
                    break;
                case NON_LOGIC:
                    this.drawNonLogic(g2, scaledLine.getP1().getX(), scaledLine.getP1().getY(), viewportWindow, scale);
                    break;
                }

            } else { //***simple pin type
                subline.setLine(scaledLine.getX1() - viewportWindow.x, scaledLine.getY1() - viewportWindow.y,
                                scaledLine.getX2() - viewportWindow.x, scaledLine.getY2() - viewportWindow.y);
                g2.draw(subline);
            }
            if (isSelected()) {
                g2.setColor(Color.BLUE);
                circle.setFrame((scaledLine.getP2().getX() - viewportWindow.x) - 2 * scale.getScaleX(),
                                (scaledLine.getP2().getY() - viewportWindow.y) - 2 * scale.getScaleX(),
                                4 * scale.getScaleX(), 4 * scale.getScaleX());


                g2.draw(circle);
            }
            break;
        case WEST:
            if (type == Type.COMPLEX) {
                //Phase 1
                switch (style) {
                case LINE:
                case CLOCK:
                case NON_LOGIC:
                case OUTPUT_LOW:
                    subline.setLine(scaledLine.getX1() - viewportWindow.x, scaledLine.getY1() - viewportWindow.y,
                                    scaledLine.getX2() - viewportWindow.x, scaledLine.getY2() - viewportWindow.y);
                    g2.draw(subline);
                    break;
                case INVERTED:
                case INVERTED_CLOCK:
                    subline.setLine(scaledLine.getX1() - viewportWindow.x, scaledLine.getY1() - viewportWindow.y,
                                    scaledLine.getX2() - invertCircleRadios - viewportWindow.x,
                                    scaledLine.getY2() - viewportWindow.y);
                    g2.draw(subline);
                    circle.setFrame(scaledLine.getX2() - invertCircleRadios - viewportWindow.x,
                                    scaledLine.getY2() - (invertCircleRadios / 2) - viewportWindow.y,
                                    invertCircleRadios, invertCircleRadios);
                    g2.draw(circle);
                    break;
                case INPUT_LOW:
                case CLOCK_LOW:
                    subline.setLine(scaledLine.getX1() - viewportWindow.x, scaledLine.getY1() - viewportWindow.y,
                                    scaledLine.getX2() - viewportWindow.x, scaledLine.getY2() - viewportWindow.y);
                    g2.draw(subline);
                    this.drawInputLow(g2, scaledLine.getX2(), scaledLine.getY2(), viewportWindow, scale);
                    break;
                case FALLING_EDGE_CLOCK:
                    subline.setLine(scaledLine.getX1() - viewportWindow.x, scaledLine.getY1() - viewportWindow.y,
                                    scaledLine.getX2() - (pinlength / 6) - viewportWindow.x,
                                    scaledLine.getY2() - viewportWindow.y);

                    g2.draw(subline);
                    this.drawFallingEdgeClock(g2, scaledLine.getX2(), scaledLine.getY2(), viewportWindow, scale);
                    break;
                }
                //Phase 2
                switch (style) {
                case CLOCK:
                case INVERTED_CLOCK:
                case CLOCK_LOW:
                    this.drawTriState(g2, scaledLine.getX2(), scaledLine.getY2(), viewportWindow, scale);
                    break;
                case OUTPUT_LOW:
                    subline.setLine(scaledLine.getX2() - viewportWindow.x,
                                    scaledLine.getY2() - (pinlength / 6) - viewportWindow.y,
                                    scaledLine.getX2() - (pinlength / 3) - viewportWindow.x,
                                    scaledLine.getY2() - viewportWindow.y);
                    g2.draw(subline);
                    break;
                case NON_LOGIC:
                    this.drawNonLogic(g2, scaledLine.getP2().getX(), scaledLine.getP2().getY(), viewportWindow, scale);
                    break;
                }

            } else { //***simple pin type
                subline.setLine(scaledLine.getX1() - viewportWindow.x, scaledLine.getY1() - viewportWindow.y,
                                scaledLine.getX2() - viewportWindow.x, scaledLine.getY2() - viewportWindow.y);
                g2.draw(subline);
            }
            if (this.isSelected()) {
                g2.setColor(Color.BLUE);
                circle.setFrame(scaledLine.getP1().getX() - viewportWindow.x - 2 * scale.getScaleX(),
                                scaledLine.getP1().getY() - viewportWindow.y - 2 * scale.getScaleX(),
                                4 * scale.getScaleX(), 4 * scale.getScaleX());
                g2.draw(circle);
            }

            break;
        case NORTH:
            if (type == Type.COMPLEX) {
                switch (style) {
                case LINE:
                case CLOCK:
                case NON_LOGIC:
                case OUTPUT_LOW:
                    subline.setLine(scaledLine.getX1() - viewportWindow.x, scaledLine.getY1() - viewportWindow.y,
                                    scaledLine.getX2() - viewportWindow.x, scaledLine.getY2() - viewportWindow.y);
                    g2.draw(subline);
                    break;
                case INVERTED:
                case INVERTED_CLOCK:
                    subline.setLine(scaledLine.getX1() - viewportWindow.x, scaledLine.getY1() - viewportWindow.y,
                                    scaledLine.getX2() - viewportWindow.x,
                                    scaledLine.getY2() - viewportWindow.y - invertCircleRadios);
                    g2.draw(subline);
                    circle.setFrame(scaledLine.getX2() - viewportWindow.x - (invertCircleRadios / 2),
                                    scaledLine.getY2() - viewportWindow.y - invertCircleRadios, invertCircleRadios,
                                    invertCircleRadios);
                    g2.draw(circle);
                    break;
                case INPUT_LOW:
                case CLOCK_LOW:
                    subline.setLine(scaledLine.getX1() - viewportWindow.x, scaledLine.getY1() - viewportWindow.y,
                                    scaledLine.getX2() - viewportWindow.x, scaledLine.getY2() - viewportWindow.y);
                    g2.draw(subline);
                    this.drawInputLow(g2, scaledLine.getX2(), scaledLine.getY2(), viewportWindow, scale);
                    break;
                case FALLING_EDGE_CLOCK:
                    subline.setLine(scaledLine.getX1() - viewportWindow.x, scaledLine.getY1() - viewportWindow.y,
                                    scaledLine.getX2() - viewportWindow.x,
                                    scaledLine.getY2() - (pinlength / 6) - viewportWindow.y);

                    g2.draw(subline);
                    this.drawFallingEdgeClock(g2, scaledLine.getX2(), scaledLine.getY2(), viewportWindow, scale);
                    break;
                }

                switch (style) {
                case CLOCK:
                case INVERTED_CLOCK:
                case CLOCK_LOW:
                    this.drawTriState(g2, scaledLine.getX2(), scaledLine.getY2(), viewportWindow, scale);
                    break;
                case OUTPUT_LOW:
                    subline.setLine(scaledLine.getX2() - (pinlength / 6) - viewportWindow.x,
                                    scaledLine.getY2() - viewportWindow.y, scaledLine.getX2() - viewportWindow.x,
                                    scaledLine.getY2() - (pinlength / 3) - viewportWindow.y);
                    g2.draw(subline);
                    break;
                case NON_LOGIC:
                    this.drawNonLogic(g2, scaledLine.getX2(), scaledLine.getY2(), viewportWindow, scale);
                    break;
                }


            } else {
                subline.setLine(scaledLine.getX1() - viewportWindow.x, scaledLine.getY1() - viewportWindow.y,
                                scaledLine.getX2() - viewportWindow.x, scaledLine.getY2() - viewportWindow.y);
                g2.draw(subline);
            }

            if (isSelected()) {
                g2.setColor(Color.BLUE);
                circle.setFrame(scaledLine.getP1().getX() - viewportWindow.x - 2 * scale.getScaleX(),
                                scaledLine.getP1().getY() - viewportWindow.y - 2 * scale.getScaleX(),
                                4 * scale.getScaleX(), 4 * scale.getScaleX());
                g2.draw(circle);
            }
            break;
        case SOUTH:
            if (type == Type.COMPLEX) {
                switch (style) {
                case LINE:
                case CLOCK:
                case NON_LOGIC:
                case OUTPUT_LOW:
                    subline.setLine(scaledLine.getX1() - viewportWindow.x, scaledLine.getY1() - viewportWindow.y,
                                    scaledLine.getX2() - viewportWindow.x, scaledLine.getY2() - viewportWindow.y);
                    g2.draw(subline);
                    break;
                case INVERTED:
                case INVERTED_CLOCK:
                    subline.setLine(scaledLine.getX1() - viewportWindow.x,
                                    scaledLine.getY1() - viewportWindow.y + invertCircleRadios,
                                    scaledLine.getX2() - viewportWindow.x, scaledLine.getY2() - viewportWindow.y);
                    g2.draw(subline);
                    circle.setFrame(scaledLine.getX1() - viewportWindow.x - (invertCircleRadios / 2),
                                    scaledLine.getY1() - viewportWindow.y, invertCircleRadios, invertCircleRadios);
                    g2.draw(circle);
                    break;
                case INPUT_LOW:
                case CLOCK_LOW:
                    subline.setLine(scaledLine.getX1() - viewportWindow.x, scaledLine.getY1() - viewportWindow.y,
                                    scaledLine.getX2() - viewportWindow.x, scaledLine.getY2() - viewportWindow.y);
                    g2.draw(subline);
                    this.drawInputLow(g2, scaledLine.getX1(), scaledLine.getY1(), viewportWindow, scale);
                    break;
                case FALLING_EDGE_CLOCK:
                    subline.setLine(scaledLine.getX1() - viewportWindow.x,
                                    scaledLine.getY1() + (pinlength / 6) - viewportWindow.y,
                                    scaledLine.getX2() - viewportWindow.x, scaledLine.getY2() - viewportWindow.y);

                    g2.draw(subline);
                    this.drawFallingEdgeClock(g2, scaledLine.getX1(), scaledLine.getY1(), viewportWindow, scale);
                    break;
                }

                switch (style) {
                case CLOCK:
                case INVERTED_CLOCK:
                case CLOCK_LOW:
                    this.drawTriState(g2, scaledLine.getX1(), scaledLine.getY1(), viewportWindow, scale);
                    break;
                case OUTPUT_LOW:
                    subline.setLine(scaledLine.getX1() - (pinlength / 6) - viewportWindow.x,
                                    scaledLine.getY1() - viewportWindow.y, scaledLine.getX1() - viewportWindow.x,
                                    scaledLine.getY1() + (pinlength / 3) - viewportWindow.y);
                    g2.draw(subline);
                    break;
                case NON_LOGIC:
                    this.drawNonLogic(g2, scaledLine.getX1(), scaledLine.getY1(), viewportWindow, scale);
                    break;
                }

            } else {
                subline.setLine(scaledLine.getX1() - viewportWindow.x, scaledLine.getY1() - viewportWindow.y,
                                scaledLine.getX2() - viewportWindow.x, scaledLine.getY2() - viewportWindow.y);
                g2.draw(subline);
            }

            if (isSelected()) {
                g2.setColor(Color.BLUE);
                circle.setFrame(scaledLine.getP2().getX() - viewportWindow.x - 2 * scale.getScaleX(),
                                scaledLine.getP2().getY() - viewportWindow.y - 2 * scale.getScaleX(),
                                4 * scale.getScaleX(), 4 * scale.getScaleX());
                g2.draw(circle);
            }
            break;
        }
        //***draw buspin text
        if (type == Type.COMPLEX) {
            this.text.Paint(g2, viewportWindow, scale, layermask);
        }
        //***Beware ->  don't escape!
        lineProvider.reset();
        ellipseProvider.reset();
    }


    @Override
    public void Print(Graphics2D g2, PrintContext printContext, int layermask) {
        ViewportWindow viewportWindow = new ViewportWindow(0, 0, 0, 0);
        AffineTransform scale = AffineTransform.getScaleInstance(1, 1);

        //Flyweight
        FlyweightProvider lineProvider = ShapeFlyweightFactory.getProvider(Line2D.class);
        FlyweightProvider ellipseProvider = ShapeFlyweightFactory.getProvider(Ellipse2D.class);

        Line2D line = getPinLine();

        Line2D subline = (Line2D) lineProvider.getShape();
        Ellipse2D circle = (Ellipse2D) ellipseProvider.getShape();

        g2.setStroke(new BasicStroke(1));

        double invertCircleRadios = (PIN_LENGTH / 3) * scale.getScaleX();
        double pinlength = PIN_LENGTH * scale.getScaleX();

        g2.setColor(Color.BLACK);
        switch (orientation) {
        case EAST:
            if (type == Type.COMPLEX) {
                switch (style) {
                case LINE:
                case CLOCK:
                case NON_LOGIC:
                case OUTPUT_LOW:
                    subline.setLine(line.getX1() - viewportWindow.x, line.getY1() - viewportWindow.y,
                                    line.getX2() - viewportWindow.x, line.getY2() - viewportWindow.y);
                    g2.draw(subline);
                    break;
                case INVERTED:
                case INVERTED_CLOCK:
                    subline.setLine((line.getX1() - viewportWindow.x) + invertCircleRadios,
                                    line.getY1() - viewportWindow.y, line.getX2() - viewportWindow.x,
                                    line.getY2() - viewportWindow.y);
                    g2.draw(subline);

                    circle.setFrame(line.getX1() - viewportWindow.x,
                                    line.getY1() - viewportWindow.y - invertCircleRadios / 2, invertCircleRadios,
                                    invertCircleRadios);
                    g2.draw(circle);

                    break;
                case INPUT_LOW:
                case CLOCK_LOW:
                    subline.setLine(line.getX1() - viewportWindow.x, line.getY1() - viewportWindow.y,
                                    line.getX2() - viewportWindow.x, line.getY2() - viewportWindow.y);
                    g2.draw(subline);
                    this.drawInputLow(g2, line.getX1(), line.getY1(), viewportWindow, scale);
                    break;
                case FALLING_EDGE_CLOCK:
                    subline.setLine(line.getX1() + (pinlength / 6) - viewportWindow.x, line.getY1() - viewportWindow.y,
                                    line.getX2() - viewportWindow.x, line.getY2() - viewportWindow.y);

                    g2.draw(subline);
                    this.drawFallingEdgeClock(g2, line.getX1(), line.getY1(), viewportWindow, scale);
                    break;
                }

                switch (style) {
                case CLOCK:
                case INVERTED_CLOCK:
                case CLOCK_LOW:
                    this.drawTriState(g2, line.getX1(), line.getY1(), viewportWindow, scale);
                    break;
                case OUTPUT_LOW:
                    subline.setLine(line.getX1() - viewportWindow.x, line.getY1() - (pinlength / 6) - viewportWindow.y,
                                    line.getX1() + (pinlength / 3) - viewportWindow.x, line.getY1() - viewportWindow.y);
                    g2.draw(subline);
                    break;
                case NON_LOGIC:
                    this.drawNonLogic(g2, line.getP1().getX(), line.getP1().getY(), viewportWindow, scale);
                    break;
                }

            } else { //***simple pin type
                subline.setLine(line.getX1() - viewportWindow.x, line.getY1() - viewportWindow.y,
                                line.getX2() - viewportWindow.x, line.getY2() - viewportWindow.y);
                g2.draw(subline);
            }

            break;
        case WEST:
            if (type == Type.COMPLEX) {
                //Phase 1
                switch (style) {
                case LINE:
                case CLOCK:
                case NON_LOGIC:
                case OUTPUT_LOW:
                    subline.setLine(line.getX1() - viewportWindow.x, line.getY1() - viewportWindow.y,
                                    line.getX2() - viewportWindow.x, line.getY2() - viewportWindow.y);
                    g2.draw(subline);
                    break;
                case INVERTED:
                case INVERTED_CLOCK:
                    subline.setLine(line.getX1() - viewportWindow.x, line.getY1() - viewportWindow.y,
                                    line.getX2() - invertCircleRadios - viewportWindow.x,
                                    line.getY2() - viewportWindow.y);
                    g2.draw(subline);
                    circle.setFrame(line.getX2() - invertCircleRadios - viewportWindow.x,
                                    line.getY2() - (invertCircleRadios / 2) - viewportWindow.y, invertCircleRadios,
                                    invertCircleRadios);
                    g2.draw(circle);
                    break;
                case INPUT_LOW:
                case CLOCK_LOW:
                    subline.setLine(line.getX1() - viewportWindow.x, line.getY1() - viewportWindow.y,
                                    line.getX2() - viewportWindow.x, line.getY2() - viewportWindow.y);
                    g2.draw(subline);
                    this.drawInputLow(g2, line.getX2(), line.getY2(), viewportWindow, scale);
                    break;
                case FALLING_EDGE_CLOCK:
                    subline.setLine(line.getX1() - viewportWindow.x, line.getY1() - viewportWindow.y,
                                    line.getX2() - (pinlength / 6) - viewportWindow.x, line.getY2() - viewportWindow.y);

                    g2.draw(subline);
                    this.drawFallingEdgeClock(g2, line.getX2(), line.getY2(), viewportWindow, scale);
                    break;
                }
                //Phase 2
                switch (style) {
                case CLOCK:
                case INVERTED_CLOCK:
                case CLOCK_LOW:
                    this.drawTriState(g2, line.getX2(), line.getY2(), viewportWindow, scale);
                    break;
                case OUTPUT_LOW:
                    subline.setLine(line.getX2() - viewportWindow.x, line.getY2() - (pinlength / 6) - viewportWindow.y,
                                    line.getX2() - (pinlength / 3) - viewportWindow.x, line.getY2() - viewportWindow.y);
                    g2.draw(subline);
                    break;
                case NON_LOGIC:
                    this.drawNonLogic(g2, line.getP2().getX(), line.getP2().getY(), viewportWindow, scale);
                    break;
                }

            } else { //***simple pin type
                subline.setLine(line.getX1() - viewportWindow.x, line.getY1() - viewportWindow.y,
                                line.getX2() - viewportWindow.x, line.getY2() - viewportWindow.y);
                g2.draw(subline);
            }

            break;
        case NORTH:
            if (type == Type.COMPLEX) {
                switch (style) {
                case LINE:
                case CLOCK:
                case NON_LOGIC:
                case OUTPUT_LOW:
                    subline.setLine(line.getX1() - viewportWindow.x, line.getY1() - viewportWindow.y,
                                    line.getX2() - viewportWindow.x, line.getY2() - viewportWindow.y);
                    g2.draw(subline);
                    break;
                case INVERTED:
                case INVERTED_CLOCK:
                    subline.setLine(line.getX1() - viewportWindow.x, line.getY1() - viewportWindow.y,
                                    line.getX2() - viewportWindow.x,
                                    line.getY2() - viewportWindow.y - invertCircleRadios);
                    g2.draw(subline);
                    circle.setFrame(line.getX2() - viewportWindow.x - (invertCircleRadios / 2),
                                    line.getY2() - viewportWindow.y - invertCircleRadios, invertCircleRadios,
                                    invertCircleRadios);
                    g2.draw(circle);
                    break;
                case INPUT_LOW:
                case CLOCK_LOW:
                    subline.setLine(line.getX1() - viewportWindow.x, line.getY1() - viewportWindow.y,
                                    line.getX2() - viewportWindow.x, line.getY2() - viewportWindow.y);
                    g2.draw(subline);
                    this.drawInputLow(g2, line.getX2(), line.getY2(), viewportWindow, scale);
                    break;
                case FALLING_EDGE_CLOCK:
                    subline.setLine(line.getX1() - viewportWindow.x, line.getY1() - viewportWindow.y,
                                    line.getX2() - viewportWindow.x, line.getY2() - (pinlength / 6) - viewportWindow.y);

                    g2.draw(subline);
                    this.drawFallingEdgeClock(g2, line.getX2(), line.getY2(), viewportWindow, scale);
                    break;
                }

                switch (style) {
                case CLOCK:
                case INVERTED_CLOCK:
                case CLOCK_LOW:
                    this.drawTriState(g2, line.getX2(), line.getY2(), viewportWindow, scale);
                    break;
                case OUTPUT_LOW:
                    subline.setLine(line.getX2() - (pinlength / 6) - viewportWindow.x, line.getY2() - viewportWindow.y,
                                    line.getX2() - viewportWindow.x, line.getY2() - (pinlength / 3) - viewportWindow.y);
                    g2.draw(subline);
                    break;
                case NON_LOGIC:
                    this.drawNonLogic(g2, line.getX2(), line.getY2(), viewportWindow, scale);
                    break;
                }


            } else {
                subline.setLine(line.getX1() - viewportWindow.x, line.getY1() - viewportWindow.y,
                                line.getX2() - viewportWindow.x, line.getY2() - viewportWindow.y);
                g2.draw(subline);
            }

            break;
        case SOUTH:
            if (type == Type.COMPLEX) {
                switch (style) {
                case LINE:
                case CLOCK:
                case NON_LOGIC:
                case OUTPUT_LOW:
                    subline.setLine(line.getX1() - viewportWindow.x, line.getY1() - viewportWindow.y,
                                    line.getX2() - viewportWindow.x, line.getY2() - viewportWindow.y);
                    g2.draw(subline);
                    break;
                case INVERTED:
                case INVERTED_CLOCK:
                    subline.setLine(line.getX1() - viewportWindow.x,
                                    line.getY1() - viewportWindow.y + invertCircleRadios,
                                    line.getX2() - viewportWindow.x, line.getY2() - viewportWindow.y);
                    g2.draw(subline);
                    circle.setFrame(line.getX1() - viewportWindow.x - (invertCircleRadios / 2),
                                    line.getY1() - viewportWindow.y, invertCircleRadios, invertCircleRadios);
                    g2.draw(circle);
                    break;
                case INPUT_LOW:
                case CLOCK_LOW:
                    subline.setLine(line.getX1() - viewportWindow.x, line.getY1() - viewportWindow.y,
                                    line.getX2() - viewportWindow.x, line.getY2() - viewportWindow.y);
                    g2.draw(subline);
                    this.drawInputLow(g2, line.getX1(), line.getY1(), viewportWindow, scale);
                    break;
                case FALLING_EDGE_CLOCK:
                    subline.setLine(line.getX1() - viewportWindow.x, line.getY1() + (pinlength / 6) - viewportWindow.y,
                                    line.getX2() - viewportWindow.x, line.getY2() - viewportWindow.y);

                    g2.draw(subline);
                    this.drawFallingEdgeClock(g2, line.getX1(), line.getY1(), viewportWindow, scale);
                    break;
                }

                switch (style) {
                case CLOCK:
                case INVERTED_CLOCK:
                case CLOCK_LOW:
                    this.drawTriState(g2, line.getX1(), line.getY1(), viewportWindow, scale);
                    break;
                case OUTPUT_LOW:
                    subline.setLine(line.getX1() - (pinlength / 6) - viewportWindow.x, line.getY1() - viewportWindow.y,
                                    line.getX1() - viewportWindow.x, line.getY1() + (pinlength / 3) - viewportWindow.y);
                    g2.draw(subline);
                    break;
                case NON_LOGIC:
                    this.drawNonLogic(g2, line.getX1(), line.getY1(), viewportWindow, scale);
                    break;
                }

            } else {
                subline.setLine(line.getX1() - viewportWindow.x, line.getY1() - viewportWindow.y,
                                line.getX2() - viewportWindow.x, line.getY2() - viewportWindow.y);
                g2.draw(subline);
            }

            break;
        }
        //***draw buspin text
        if (type == Type.COMPLEX) {
            this.text.Paint(g2, viewportWindow, scale, layermask);
        }
        //***Beware ->  don't escape!
        lineProvider.reset();
        ellipseProvider.reset();

    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public void setOrientation(Pinable.Orientation orientation) {
        this.orientation = orientation;
    }

    @Override
    public Pinable.Orientation getOrientation() {
        return orientation;
    }

    /**
     *Use set style instead
     * @param invert
     * @deprecated
     */
    @Deprecated
    public void setInvert(boolean invert) {
        if (invert)
            this.style = Style.INVERTED;
    }

    public boolean isInvert() {
        return this.style == Style.INVERTED;
    }

    /**
     *Use setStyle instead
     * @param triState
     * @deprecated
     */
    @Deprecated
    public void setTriState(boolean triState) {
        if (triState)
            this.style = Style.CLOCK;
    }

    public boolean isTriState() {
        return this.style == Style.CLOCK;
    }

    @Override
    public String getDisplayName() {
        return "Pin";
    }

    public String toXML() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("<pin type=\"" + type.ordinal() + "\" style=\"" + style.ordinal() + "\">\r\n");
        buffer.append("<a>" + getX() + "," + getY() + ", + false + ," + getOrientation().ordinal() + ",0</a>\r\n");
        if (!(text.getTextureByTag("pinname").isEmpty()))
            buffer.append("<name>" + text.getTextureByTag("pinname").toXML() + "</name>\r\n");
        if (!(text.getTextureByTag("pinnumber").isEmpty()))
            buffer.append("<number>" + text.getTextureByTag("pinnumber").toXML() + "</number>\r\n");
        buffer.append("</pin>\r\n");
        return buffer.toString();
    }

    public void fromXML(Node node) {
        Element element = (Element) node;
        style =
            Style.values()[element.getAttribute("style") == "" ? 0 : (Byte.parseByte(element.getAttribute("style")))];
        type = Type.values()[(Byte.parseByte(element.getAttribute("type")))];
        Node a = element.getElementsByTagName("a").item(0);
        StringTokenizer st = new StringTokenizer(a.getTextContent(), ",");
        this.setLocation(Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken()));
        setInvert(Boolean.parseBoolean(st.nextToken()));
        orientation = Orientation.values()[Byte.parseByte(st.nextToken())];
        setTriState(Byte.parseByte(st.nextToken()) == 1);

        Node name = element.getElementsByTagName("name").item(0);
        if (name != null) {
            text.getTextureByTag("pinname").fromXML(name);
        } else {
            text.getTextureByTag("pinname").setText("");
            text.getTextureByTag("pinname").Move(getX(), getY());
        }
        Node number = element.getElementsByTagName("number").item(0);

        if (number != null) {
            text.getTextureByTag("pinnumber").fromXML(number);
        } else {
            text.getTextureByTag("pinnumber").setText("");
            text.getTextureByTag("pinnumber").Move(getX() + 8, getY() + 8);
        }
    }

    public AbstractMemento getState(MementoType operationType) {
        Memento memento = new Memento(operationType);
        memento.saveStateFrom(this);
        return memento;
    }

    public void setState(AbstractMemento memento) {
        memento.loadStateTo(this);
    }


    class Memento extends AbstractMemento<Symbol, Pin> {
        private int Ax;

        private int Ay;

        private int orientation;

        private int style;

        private int type;

        private ChipText.Memento text;

        public Memento(MementoType mementoType) {
            super(mementoType);
            text = new ChipText.Memento();
        }

        public void loadStateTo(Pin shape) {
            super.loadStateTo(shape);
            text.loadStateTo(shape.getChipText());
            shape.type = Type.values()[this.type];
            shape.style = Style.values()[this.style];
            shape.orientation = Pinable.Orientation.values()[orientation];
            shape.setLocation(Ax, Ay);
        }

        public void saveStateFrom(Pin shape) {
            super.saveStateFrom(shape);
            this.text.saveStateFrom(shape.getChipText());
            this.type = shape.type.ordinal();
            this.style = shape.style.ordinal();
            this.orientation = shape.orientation.ordinal();
            this.Ax = shape.getX();
            this.Ay = shape.getY();
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
            return (other.getUUID().equals(this.getUUID()) && other.getMementoType() == this.getMementoType() &&
                    other.text.equals(this.text) && other.type == this.type && other.style == this.style &&
                    other.orientation == this.orientation && other.Ax == this.Ax && other.Ay == this.Ay);

        }

        @Override
        public int hashCode() {
            int hash = 1;
            hash =
                hash * 31 + this.getUUID().hashCode() + getMementoType().hashCode() + Ax + Ay + this.text.hashCode() +
                this.type + this.style + +this.orientation;
            return hash;
        }

        @Override
        public String toString() {
            return "Pin";
        }

        public void Clear() {
            super.Clear();
            text.Clear();
        }

        public boolean isSameState(Symbol unit) {
            Pin pin = (Pin) unit.getShape(getUUID());
            return (pin.getState(getMementoType()).equals(this));
        }
    }
}
