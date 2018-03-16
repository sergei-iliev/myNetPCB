package com.mynetpcb.pad.shape;


import com.mynetpcb.core.capi.Externalizable;
import com.mynetpcb.core.capi.Resizeable;
import com.mynetpcb.core.capi.ViewportWindow;
import com.mynetpcb.core.capi.flyweight.FlyweightProvider;
import com.mynetpcb.core.capi.flyweight.ShapeFlyweightFactory;
import com.mynetpcb.core.capi.line.Trackable;
import com.mynetpcb.core.capi.print.PrintContext;
import com.mynetpcb.core.capi.shape.Shape;
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
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import org.w3c.dom.Element;
import org.w3c.dom.Node;


/*
 * Line shape is not part of cooper layer.
 * Basic primitive of the footprint.
 */
public class Line extends Shape implements Trackable<Point>, Resizeable, Externalizable {

    public Point floatingStartPoint; //***the last wire point

    public Point floatingMidPoint; //***mid 90 degree forming

    public Point floatingEndPoint;

    private List<Point> points;

    private Point resizingPoint;

    public Line(int thickness,int layermaskId) {
        super(0, 0, 0, 0, thickness, layermaskId);
        this.points = new LinkedList<Point>();
        this.floatingStartPoint = new Point();
        this.floatingMidPoint = new Point();
        this.floatingEndPoint = new Point();
        this.selectionRectWidth = 3000;
    }
    

    public Line() {
        this(0,Layer.SILKSCREEN_LAYER_FRONT);
    }

    @Override
    public Point alignToGrid(boolean isRequired) {
        if (isRequired) {
            for (Point wirePoint : points) {
                Point point = getOwningUnit().getGrid().positionOnGrid(wirePoint.x, wirePoint.y);
                wirePoint.setLocation(point);
            }
        }
        return null;
    }
    
    @Override
    public void alignResizingPointToGrid(Point targetPoint) {
        getOwningUnit().getGrid().snapToGrid(targetPoint);         
    }
    
    @Override
    public List<Point> getLinePoints() {
        return points;
    }

    @Override
    public void insertPoint(int x, int y) {
        if (this.points.size() == 0) {
            return;
        }
        boolean flag = false;
        Point point = getOwningUnit().getGrid().positionOnGrid(x, y);

        Rectangle rect =
            new Rectangle(x - getOwningUnit().getGrid().getGridPointToPoint() / 2,
                          y - getOwningUnit().getGrid().getGridPointToPoint() / 2,
                          getOwningUnit().getGrid().getGridPointToPoint(),
                          getOwningUnit().getGrid().getGridPointToPoint());

        Line2D line = new Line2D.Double();


        Point tmp = new Point(point.x, point.y);
        Point midium = new Point();

        //***add point to the end;
        addPoint(point);

        Point prev = points.get(0);
        for (Point next : points) {

            if (!flag) {
                //***find where the point is - 2 points between the new one
                line.setLine(prev, next);
                if (line.intersects(rect))
                    flag = true;
            } else {
                midium.setLocation(tmp); //midium.setPin(tmp.getPin());
                tmp.setLocation(prev); //tmp.setPin(prev.getPin());
                prev.setLocation(midium); //prev.setPin(midium.getPin());
            }
            prev = next;
        }
        if (flag)
            prev.setLocation(tmp); //prev.setPin(tmp.getPin());
    }

    @Override
    public void deleteLastPoint() {
        if (points.size() == 0)
            return;

        points.remove(points.get(points.size() - 1));

        //***reset floating start point
        if (points.size() > 0)
            floatingStartPoint.setLocation(points.get(points.size() - 1));
    }

    @Override
    public void addPoint(Point point) {
        points.add(point);
    }

    @Override
    public void add(int x, int y) {
        points.add(new Point(x, y));
    }

    @Override
    public Point isBendingPointClicked(int x, int y) {
        FlyweightProvider rectProvider = ShapeFlyweightFactory.getProvider(Rectangle2D.class);
        Rectangle2D rect = (Rectangle2D) rectProvider.getShape();
        rect.setFrame(x - (thickness / 2), y - (thickness / 2), thickness, thickness);

        Point point = null;

        for (Point wirePoint : points) {
            if (rect.contains(wirePoint)) {
                point = wirePoint;
                break;
            }
        }
        rectProvider.reset();
        return point;
    }

    public Point isControlRectClicked(int x, int y) {
        FlyweightProvider rectProvider = ShapeFlyweightFactory.getProvider(Rectangle2D.class);
        Rectangle2D rect = (Rectangle2D) rectProvider.getShape();
        rect.setFrame(x - (thickness / 2), y - (thickness / 2), thickness, thickness);

        Point point = null;
        Point click = new Point(x, y);
        int distance = Integer.MAX_VALUE;

        for (Point wirePoint : points) {
            if (rect.contains(wirePoint)) {
                int min = (int) click.distance(wirePoint);
                if (distance > min) {
                    distance = min;
                    point = wirePoint;
                }
            }
        }

        rectProvider.reset();
        return point;
    }

    @Override
    public Point getResizingPoint() {
        return resizingPoint;
    }

    @Override
    public void setResizingPoint(Point point) {
        this.resizingPoint = point;
    }

    @Override
    public void Resize(int xOffset, int yOffset, Point clickedPoint) {
        clickedPoint.setLocation(clickedPoint.x + xOffset, clickedPoint.y + yOffset);
    }

    @Override
    public Point getFloatingStartPoint() {
        return floatingStartPoint;
    }

    @Override
    public Point getFloatingMidPoint() {
        return floatingMidPoint;
    }

    @Override
    public Point getFloatingEndPoint() {
        return floatingEndPoint;
    }

    @Override
    public void shiftFloatingPoints() {

    }

    @Override
    public void Reset(Point point) {
        this.Reset(point.x, point.y);
    }

    @Override
    public void Reset() {
        this.Reset(floatingStartPoint);
    }

    @Override
    public void Reset(int x, int y) {
        Point p = isBendingPointClicked(x, y);
        floatingStartPoint.setLocation(p == null ? x : p.x, p == null ? y : p.y);
        floatingMidPoint.setLocation(p == null ? x : p.x, p == null ? y : p.y);
        floatingEndPoint.setLocation(p == null ? x : p.x, p == null ? y : p.y);
    }

    @Override
    public boolean isFloating() {
        return (!(floatingStartPoint.equals(floatingEndPoint) && floatingStartPoint.equals(floatingMidPoint)));
    }

    @Override
    public void setSelected(boolean selection) {
        super.setSelected(selection);
        if (!selection) {
            resizingPoint = null;
        }
    }

    @Override
    public void Clear() {
        points.clear();
    }


    @Override
    public long getOrderWeight() {
        return 2;
    }

    @Override
    public void Move(int xoffset, int yoffset) {
        for (Point wirePoint : points) {
            wirePoint.setLocation(wirePoint.x + xoffset, wirePoint.y + yoffset);
        }
    }

    @Override
    public void Mirror(Point A,Point B) {
        for (Point wirePoint : points) {
            wirePoint.setLocation(Utilities.mirrorPoint(A,B, wirePoint));
        }
    }

    @Override
    public void Translate(AffineTransform translate) {
        for (Point wirePoint : points) {
            translate.transform(wirePoint, wirePoint);
        }
    }

    @Override
    public void Rotate(AffineTransform rotation) {
        for (Point wirePoint : points) {
            rotation.transform(wirePoint, wirePoint);
        }
    }

    @Override
    public void setLocation(int x, int y) {
    }

    @Override
    public boolean isInRect(Rectangle r) {
        for (Point wirePoint : points) {
            if (!r.contains(wirePoint))
                return false;
        }
        return true;
    }

    @Override
    public void Paint(Graphics2D g2, ViewportWindow viewportWindow, AffineTransform scale, int layermask) {
        if ((this.getCopper().getLayerMaskID() & layermask) == 0) {
            return;
        }
        //FIX caching for the lines isCasheEnabled=false;
        Rectangle2D scaledBoundingRect = Utilities.getScaleRect(getBoundingShape().getBounds(), scale);

        if (!this.isFloating() && !scaledBoundingRect.intersects(viewportWindow)) {
            return;
        }

        double lineThickness = thickness * scale.getScaleX();

        FlyweightProvider provider =ShapeFlyweightFactory.getProvider(GeneralPath.class);
        GeneralPath temporal=(GeneralPath)provider.getShape(); 
        

        temporal.moveTo(points.get(0).getX(),points.get(0).getY());
        for(int i=1;i<points.size();i++){            
              temporal.lineTo(points.get(i).getX(),points.get(i).getY());       
        } 
        
        AffineTransform translate= AffineTransform.getTranslateInstance(-viewportWindow.x,-viewportWindow.y);
        
        temporal.transform(scale);
        temporal.transform(translate);

        g2.setStroke(new BasicStroke((float) lineThickness, JoinType.JOIN_ROUND.ordinal(), EndType.CAP_ROUND.ordinal()));
        g2.setColor(isSelected() ? Color.GRAY : copper.getColor());

        AlphaComposite composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f);
        Composite originalComposite = g2.getComposite();
        g2.setComposite(composite);
        g2.draw(temporal);
        g2.setComposite(originalComposite);

        if(this.isFloating()) {
            temporal.reset();
            temporal.moveTo(floatingStartPoint.getX(), floatingStartPoint.getY());
            temporal.lineTo(floatingMidPoint.getX(),floatingMidPoint.getY());
            temporal.lineTo(floatingEndPoint.getX(),floatingEndPoint.getY());
                        
            temporal.transform(scale);
            temporal.transform(translate);
            g2.draw(temporal);
        }
        
        provider.reset();

        if (this.isSelected()) {
            this.drawControlShape(g2, viewportWindow, scale);
        }
    }

    @Override
    public void Print(Graphics2D g2,PrintContext printContext, int layermask) {
        GeneralPath line = null;
        line = new GeneralPath(GeneralPath.WIND_EVEN_ODD, points.size());
        line.moveTo((float) points.get(0).getX(), (float) points.get(0).getY());
        for (int i = 1; i < points.size(); i++) {
            line.lineTo((float) points.get(i).getX(), (float) points.get(i).getY());
        }

        g2.setStroke(new BasicStroke(thickness, JoinType.JOIN_ROUND.ordinal(), EndType.CAP_ROUND.ordinal()));
        g2.setColor(printContext.isBlackAndWhite()?Color.BLACK:this.copper.getColor());  
        g2.draw(line);

    }

    @Override
    public void drawControlShape(Graphics2D g2, ViewportWindow viewportWindow, AffineTransform scale) {
        Utilities.drawCrosshair(g2, viewportWindow, scale, points, resizingPoint, selectionRectWidth);
    }

    @Override
    public boolean isClicked(int x, int y) {
        boolean result = false;
        //build testing rect
        FlyweightProvider rectProvider = ShapeFlyweightFactory.getProvider(Rectangle2D.class);
        Rectangle2D rect = (Rectangle2D) rectProvider.getShape();
        rect.setFrame(x - (thickness / 2), y - (thickness / 2), thickness, thickness);
        //inspect line by line
        FlyweightProvider lineProvider = ShapeFlyweightFactory.getProvider(Line2D.class);
        Line2D line = (Line2D) lineProvider.getShape();

        //***make lines and iterate one by one
        Point prevPoint = points.iterator().next();
        Iterator<Point> i = points.iterator();
        while (i.hasNext()) {
            Point nextPoint = i.next();
            line.setLine(prevPoint, nextPoint);
            if (line.intersects(rect)) {
                result = true;
                break;
            }
            prevPoint = nextPoint;
        }

        lineProvider.reset();
        rectProvider.reset();
        return result;
    }

    @Override
    public Rectangle calculateShape() {
        int x1 = Integer.MAX_VALUE, y1 = Integer.MAX_VALUE, x2 = Integer.MIN_VALUE, y2 = Integer.MIN_VALUE;

        for (Point point : points) {
            x1 = Math.min(x1, point.x);
            y1 = Math.min(y1, point.y);
            x2 = Math.max(x2, point.x);
            y2 = Math.max(y2, point.y);
        }
        //add bending points

        return new Rectangle(x1, y1, (x2 - x1) == 0 ? 1 : x2 - x1, y2 - y1 == 0 ? 1 : y2 - y1);
    }

    @Override
    public void Reverse(int x, int y) {
        Point p = isBendingPointClicked(x, y);
        if (points.get(0).x == p.x && points.get(0).y == p.y) {
            Collections.reverse(points);
        }
    }

    @Override
    public void removePoint(int x, int y) {
        Point point = isBendingPointClicked(x, y);
        if (point != null) {
            points.remove(point);
            point = null;
        }
    }

    @Override
    public boolean isEndPoint(int x, int y) {
        if (points.size() < 2) {
            return false;
        }

        Point point = isBendingPointClicked(x, y);
        if (point == null) {
            return false;
        }
        //***head point
        if (points.get(0).x == point.x && points.get(0).y == point.y) {
            return true;
        }
        //***tail point
        if ((points.get(points.size() - 1)).x == point.x && (points.get(points.size() - 1)).y == point.y) {
            return true;
        }
        return false;
    }

    @Override
    public String toXML() {
        StringBuffer sb = new StringBuffer();
        sb.append("<line copper=\"" + getCopper().getName() + "\" thickness=\"" + this.getThickness() + "\">");
        for (Point point : points) {
            sb.append(point.x + "," + point.y + ",");
        }
        sb.append("</line>\r\n");
        return sb.toString();
    }

    @Override
    public void fromXML(Node node) {
        Element element = (Element) node;
        if (element.hasAttribute("copper")) {
            this.setCopper(Layer.Copper.valueOf(element.getAttribute("copper")));
        }
        StringTokenizer st = new StringTokenizer(element.getTextContent(), ",");
        this.setThickness(Integer.parseInt(element.getAttribute("thickness")));

        while (st.hasMoreTokens()) {
            this.addPoint(new Point(Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken())));
        }
    }

    public Line clone() throws CloneNotSupportedException {
        Line copy = (Line) super.clone();
        copy.floatingStartPoint = new Point();
        copy.floatingMidPoint = new Point();
        copy.floatingEndPoint = new Point();
        copy.points = new LinkedList<Point>();
        for (Point point : points) {
            copy.points.add(new Point(point.x, point.y));
        }
        return copy;
    }

    @Override
    public String getDisplayName() {
        return "Line";
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


    public static class Memento extends AbstractMemento<Footprint, Line> {

        private int Ax[];

        private int Ay[];

        public Memento(MementoType mementoType) {
            super(mementoType);

        }

        @Override
        public void loadStateTo(Line shape) {
            super.loadStateTo(shape);
            shape.points.clear();
            for (int i = 0; i < Ax.length; i++) {
                shape.addPoint(new Point(Ax[i], Ay[i]));
            }
            //***reset floating start point
            if (shape.points.size() > 0) {
                shape.floatingStartPoint.setLocation(shape.points.get(shape.points.size() - 1));
                shape.Reset();
            }
        }

        @Override
        public void saveStateFrom(Line shape) {
            super.saveStateFrom(shape);
            Ax = new int[shape.points.size()];
            Ay = new int[shape.points.size()];
            for (int i = 0; i < shape.points.size(); i++) {
                Ax[i] = shape.points.get(i).x;
                Ay[i] = shape.points.get(i).y;
            }
        }

        @Override
        public void Clear() {
            super.Clear();
            Ax = null;
            Ay = null;
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
            return (getUUID().equals(other.getUUID()) && getMementoType().equals(other.getMementoType()) &&
                    thickness == other.thickness && layerindex == other.layerindex &&
                    Arrays.equals(Ax, other.Ax) && Arrays.equals(Ay, other.Ay));

        }

        @Override
        public int hashCode() {
            int hash = getUUID().hashCode();
            hash += this.getMementoType().hashCode();
            hash += thickness + layerindex;
            hash += Arrays.hashCode(Ax);
            hash += Arrays.hashCode(Ay);
            return hash;
        }

        public boolean isSameState(Footprint unit) {
            Line line = (Line) unit.getShape(getUUID());
            return (line.getState(getMementoType()).equals(this));
        }
    }
}
