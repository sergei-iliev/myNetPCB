package com.mynetpcb.core.capi.unit;


import com.mynetpcb.core.capi.Grid;
import com.mynetpcb.core.capi.Moveable;
import com.mynetpcb.core.capi.Ownerable;
import com.mynetpcb.core.capi.pin.PinLineable;
import com.mynetpcb.core.capi.pin.Pinable;
import com.mynetpcb.core.capi.line.LinePoint;
import com.mynetpcb.core.capi.line.Sublineable;
import com.mynetpcb.core.capi.line.Trackable;
import com.mynetpcb.core.capi.pin.CompositePinable;
import com.mynetpcb.core.capi.shape.Label;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.text.Texture;


import com.mynetpcb.d2.shapes.Box;
import com.mynetpcb.d2.shapes.Line;
import com.mynetpcb.d2.shapes.Point;

import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/*
 * LOOKS LIKE A SINGLETON BUT IS NOT
 */
public class UnitMgr<U extends Unit, T extends Shape> {

    private static UnitMgr instance;

    public static synchronized UnitMgr getInstance() {
        if (instance == null) {
            instance = new UnitMgr();
        }
        return instance;
    }

    public void Load(U to, U from) throws CloneNotSupportedException {
        //1.Clear old symbols with notification
        to.clear();

        //2 equalize grid
        to.getGrid().setGridUnits(from.getGrid().getGridValue(), from.getGrid().getGridUnits());
        //3.Resize grid
        to.setSize(from.getWidth() == 1 ? to.getWidth() : from.getWidth(),
                   from.getHeight() == 1 ? to.getHeight() : from.getHeight());

        //4.load new symbols
        Collection<T> shapes = from.getShapes();
        for (T shape : shapes) {
            //***isolate owned children
//            if (shape instanceof Ownerable && ((Ownerable) shape).getOwner() != null) {
//                continue;
//            }

            T copyShape = (T) shape.clone();
//            Collection<T> children = getChildrenByParent(from.getShapes(), shape);
//            for (T child : children) {
//                T childCopy = (T) child.clone();
//                ((Ownerable) childCopy).setOwner(copyShape);
//                to.add(childCopy);
//            }
            to.add(copyShape);
        }
        to.setUnitName(from.getUnitName());
    }

    /**
     *Clone shapes keeping owner reference to the cloned parent
     * @param source
     * @param target
     */
//    public void cloneBlock(U source, U target) {
//        Collection<T> shapes = source.getShapes();
//        for (T shape : shapes) {
//            //***isolate owned children
//            if (shape instanceof Ownerable && ((Ownerable) shape).getOwner() != null) {
//                continue;
//            }
//            try {
//                Shape clonning = shape.clone();
//                Collection<T> children = this.getChildrenByParent(source.getShapes(), shape);
//                for (Shape child : children) {
//                    Shape childCopy = child.clone();
//                    ((Ownerable) childCopy).setOwner(clonning);
//                    target.add(childCopy);
//                }
//                target.add(clonning);
//            } catch (CloneNotSupportedException cne) {
//                cne.printStackTrace(System.out);
//            }
//        }
//    }

    /*
     * Block clone(mind parent child attachments,circuitlabels only)
     */

    public void cloneBlock(U unit, Collection<Shape> selectedShapes) {
        //***disselect old block
        unit.setSelected(false);

        //***clone each element in the block
        for (Shape shape : selectedShapes) {
            try {
                Shape clonning = shape.clone();                
                clonning.setSelected(true);
                //***tweak naming
                //CircuitMgr.getInstance().symbolNaming(circuit,clonning);
                unit.add(clonning);
            } catch (CloneNotSupportedException cne) {
                cne.printStackTrace(System.out);
            }
        }
    }

    public void rotateBlock(Collection<T> shapes, double angle,Point origin) {
        for (T shape : shapes) {
            shape.rotate(angle,origin);
        }
    }

    public void mirrorBlock(Collection<T> shapes, Line line) {
        for (T shape : shapes) {
            shape.mirror(line);
        }
    }
    /*
     * Block mirror(mind parent child attachments,circuitlabels only)
     */
//    public void mirrorBlock(U unit, Line line) {
//        Collection<T> selectedShapes = unit.getSelectedShapes(true);
//        for (T shape : selectedShapes) {
//            shape.mirror(line);
//        }
//
//    }

    public void moveBlock(Collection<T> shapes, double xoffset, double yoffset) {
        for (Shape shape : shapes) {
            shape.move(xoffset, yoffset);
        }
    }

    /*
    * The rectangle based on selected Pinnables in the Unit
    */

    public Box getPinsRect(Collection<T> shapes) {
        double x1 = Integer.MAX_VALUE, y1 = Integer.MAX_VALUE, x2 = Integer.MIN_VALUE, y2 = Integer.MIN_VALUE;
        boolean isPinnable = false;

        for (Moveable symbol : shapes) {
            if (symbol instanceof CompositePinable) { //group of pins
                CompositePinable element = (CompositePinable) symbol;
                Box r = element.getPinsRect();
                x1 = Math.min(x1, r.min.x);
                y1 = Math.min(y1, r.min.y);
                x2 = Math.max(x2, r.max.y);
                y2 = Math.max(y2, r.max.y);
                isPinnable = true;
            }
            if (symbol instanceof Pinable) { //single pin
                Pinable pin = (Pinable) symbol;
                Point point = pin.getPinPoint();
                x1 = Math.min(x1, point.x);
                y1 = Math.min(y1, point.y);
                x2 = Math.max(x2, point.x);
                y2 = Math.max(y2, point.y);
                isPinnable = true;
            }

        
        }
        if (isPinnable)
            return new Box(x1, y1, x2 - x1, y2 - y1);
        else
            return null;
    
}
    public void alignBlock(Grid grid, Collection<T> shapes) {
        //***order chips first
        Box r = getPinsRect(shapes);
        //***no need to align if no pins present
        if (r == null) {
            return;
        }
        Point point = grid.positionOnGrid(r.min);

        for (T shape : shapes) {
            shape.move((point.x - r.min.x), (point.y - r.min.y));
        }
    }

    public void deleteBlock(U unit, Collection<T> shapes) {
        for (Shape shape : shapes) {
//            Collection<T> children = getChildrenByParent(unit.getShapes(), shape);
//            for (Shape child : children) {
//                unit.delete(child.getUUID());
//            }

            unit.delete(shape.getUUID());
        }
    }

    public void locateBlock(U unit, Collection<T> shapes, int x, int y) {
        int xx = (int) unit.getShapesRect(shapes).getX();
        int yy = (int) unit.getShapesRect(shapes).getY();
        moveBlock(shapes, x - xx, y - yy);
    }

    /*
     * Mind SubWire selection
     */

    public boolean isBlockSelected(U unit) {
        int count = 0;
        Collection<T> shapes = unit.getShapes();
        for (T shape : shapes) {
            if (shape instanceof Ownerable && ((Ownerable) shape).getOwner() != null) {
                continue;
            }
            if (shape.isSelected()) {
                count++;
            } else {
                if ((shape instanceof Sublineable) && ((Sublineable) shape).isSublineSelected()) {
                    count++;
                }
            }
        }
        return count > 1;
    }

//    public Collection<T> getChildrenByParent(Collection<T> childrenSet, Shape parent) {
//        Collection<T> children = new HashSet<T>(50);
//        for (T shape : childrenSet) {
//            if (shape instanceof Ownerable && ((Ownerable) shape).getOwner() == parent)
//                children.add(shape);
//        }
//        return children;
//    }

    /*
     * Normalize the pin text when chip is rotated or mirrored
     * Rule 1:Horizontal Pin-Text alignment:Anchor Point must be above Pin line
     * Rule 2:Horizontal Pin-Text alignment:Anchor Point must be left most to Pin line
     */

//    public Texture getTextureByTag(U unit, String tag) {
//        return this.getTextureByTag(tag, unit.getShapes());
//    }

    public T getLabelByTag(U unit, String tag) {
        Collection<T> shapes = unit.getShapes(Label.class);
        for (T shape : shapes) {            
                Texture text = ((Label) shape).getTexture();
                if (text.getTag().equals(tag))
                    return shape;            
        }
        return null;
    }


//    public Texture getTextureByTag(String tag, Collection<T> symbols) {
//        for (T element : symbols) {
//            if (element instanceof Textable) {
//                Texture text = ((Textable) element).getChipText().getTextureByTag(tag);
//                if (text != null)
//                    return text;
//            }
//        }
//        return null;
//    }


    private void normalizePinText(PinLineable pin) {
//        for (Texture text : pin.getPinText()) {
//            switch (pin.getOrientation()) {
//            case WEST:
//            case EAST:
//                if (Text.Orientation.HORIZONTAL == text.getAlignment().getOrientation()) {
//                    //if (text.getAnchorPoint().y > pin.getPinPoints().getA().y)
//                        //text.Mirror(pin.getPinPoints().getA(),pin.getPinPoints().getB());
//                }
//                break;
//            case NORTH:
//            case SOUTH:
//                if (Text.Orientation.VERTICAL == text.getAlignment().getOrientation()) {
//                    //if (text.getAnchorPoint().x > pin.getPinPoints().getA().x)
//                        //text.Mirror(pin.getPinPoints().getA(),pin.getPinPoints().getB());
//                }
//                break;
//            }
//        }
    }

//    public void normalizePinText(Shape shape) {
//        if (shape instanceof PinLineable) {
//            normalizePinText((PinLineable) shape);
//        }
//        if (shape instanceof Pinaware) {
//            normalizePinText(((Pinaware) shape).getPins());
//        }
//    }

    public void normalizePinText(Collection<? extends Shape> shapes) {
//        for (Shape shape : shapes) {
//            if (shape instanceof PCBShape) {
//                continue;
//            }
//            if (shape instanceof PinLineable) { //single pin
//                normalizePinText((PinLineable) shape);
//            } else if (shape instanceof Pinaware) { //single chip
//                Collection<PinLineable> pins = ((Pinaware) shape).getPins();
//                for (PinLineable pin : pins) {
//                    normalizePinText(pin);
//                }
//            }
//        }
    }


    public Trackable getClickedLine(Unit unit, int x, int y, Trackable track) {
        Collection<Trackable> lines = unit.<Trackable>getShapes(Trackable.class);
        for (Trackable shape : lines) {
            if (track != null && shape == track)
                continue;
            if (shape.isClicked(x, y))
                return shape;
        }
        return null;
    }

    public Collection<LinePoint> getSublinePoints(Unit unit) {
        Collection<LinePoint> points = new ArrayList<LinePoint>(100);
        //***select all subwires
        Collection<Shape> sublines = unit.<Shape>getShapes(Sublineable.class);
        for (Shape subline : sublines) {
            Set<LinePoint> subwire = ((Sublineable) subline).getSublinePoints();
            if (subwire != null)
                points.addAll(subwire);
        }
        return points;
    }

    public Collection<Sublineable> getSublineWires(Unit unit) {
        Collection<Sublineable> wires = new ArrayList<>(100);
        Collection<Shape> sublines = unit.<Shape>getShapes(Sublineable.class);
        for (Shape subline : sublines) {
            if (((Sublineable) subline).isSublineSelected()) {
                wires.add((Sublineable) subline);
            }
        }
        return wires;
    }
}

