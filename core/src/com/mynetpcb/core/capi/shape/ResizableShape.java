package com.mynetpcb.core.capi.shape;


import com.mynetpcb.core.capi.Resizeable;
import com.mynetpcb.core.capi.ViewportWindow;
import com.mynetpcb.core.capi.flyweight.FlyweightProvider;
import com.mynetpcb.core.capi.flyweight.ShapeFlyweightFactory;
import com.mynetpcb.core.capi.undo.AbstractMemento;
import com.mynetpcb.core.capi.undo.MementoType;
import com.mynetpcb.core.capi.unit.Unit;
import com.mynetpcb.core.utils.Utilities;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import java.util.Arrays;


public abstract class ResizableShape extends Shape implements Resizeable {

    protected Point upperLeft, upperRight, bottomLeft, bottomRight;

    private Point resizingPoint;


    public ResizableShape(int x, int y, int width, int height, int thickness,int layermask) {
        super(x, y, width, height, thickness,layermask);
        this.setFillColor(Color.BLACK);
        upperLeft = new Point();
        upperRight = new Point();
        bottomLeft = new Point();
        bottomRight = new Point();
        Initialize(x, y, width, height);
    }

    @Override
    public ResizableShape clone() throws CloneNotSupportedException {
        ResizableShape copy = (ResizableShape)super.clone();
        copy.upperLeft = new Point(upperLeft);
        copy.upperRight = new Point(upperRight);
        copy.bottomLeft = new Point(bottomLeft);
        copy.bottomRight = new Point(bottomRight);
        return copy;
    }

    protected void Initialize(int x, int y, int width, int height) {
        upperLeft.setLocation(x, y);
        upperRight.setLocation(x + width, y);
        bottomLeft.setLocation(x, y + height);
        bottomRight.setLocation(x + width, y + height);
    }
    @Override
    public Point alignToGrid(boolean isRequired) {
        if(isRequired){
          return super.alignToGrid(isRequired);
        }else{
            return null;
        }
    }
    
    public void alignResizingPointToGrid(Point targetPoint){
        Point point=getOwningUnit().getGrid().positionOnGrid(targetPoint.x,targetPoint.y);  
        Resize(point.x -targetPoint.x,point.y-targetPoint.y,targetPoint);     
    }
    
    @Override
    public java.awt.Shape calculateShape() {
      return new Rectangle(getX(), getY(), getWidth(), getHeight());
    }
    @Override
    public Point isControlRectClicked(int x, int y) {
        FlyweightProvider rectFlyweightProvider = ShapeFlyweightFactory.getProvider(Rectangle2D.class);
        Rectangle2D rect = (Rectangle2D)rectFlyweightProvider.getShape();

        Point p = new Point(x, y);
        try {
            rect.setRect(upperLeft.x - selectionRectWidth / 2, upperLeft.y - selectionRectWidth / 2,
                         selectionRectWidth, selectionRectWidth);
            if (rect.contains(p)) {
                return upperLeft;
            }
            rect.setRect(upperRight.x - selectionRectWidth / 2, upperRight.y - selectionRectWidth / 2,
                         selectionRectWidth, selectionRectWidth);
            if (rect.contains(p))
                return upperRight;

            rect.setRect(bottomLeft.x - selectionRectWidth / 2, bottomLeft.y - selectionRectWidth / 2,
                         selectionRectWidth, selectionRectWidth);
            if (rect.contains(p))
                return bottomLeft;

            rect.setRect(bottomRight.x - selectionRectWidth / 2, bottomRight.y - selectionRectWidth / 2,
                         selectionRectWidth, selectionRectWidth);
            if (rect.contains(p))
                return bottomRight;

        } finally {
            rectFlyweightProvider.reset();
        }
        return null;
    }

    public void drawControlShape(Graphics2D g2, ViewportWindow viewportWindow, AffineTransform scale) {
        Utilities.drawCrosshair(g2, viewportWindow, scale, resizingPoint, selectionRectWidth, upperLeft,upperRight,bottomLeft,bottomRight);
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
        if (clickedPoint.equals(upperLeft)) {
            upperLeft.setLocation(upperLeft.x + xOffset, upperLeft.y + yOffset);
            bottomLeft.setLocation(bottomLeft.x + xOffset, bottomLeft.y);
            upperRight.setLocation(upperRight.x, upperRight.y + yOffset);
        } else if (clickedPoint.equals(upperRight)) {
            upperRight.setLocation(upperRight.x + xOffset, upperRight.y + yOffset);
            bottomRight.setLocation(bottomRight.x + xOffset, bottomRight.y);
            upperLeft.setLocation(upperLeft.x, upperLeft.y + yOffset);
        } else if (clickedPoint.equals(bottomLeft)) {
            bottomLeft.setLocation(bottomLeft.x + xOffset, bottomLeft.y + yOffset);
            upperLeft.setLocation(upperLeft.x + xOffset, upperLeft.y);
            bottomRight.setLocation(bottomRight.x, bottomRight.y + yOffset);
        } else if (clickedPoint.equals(bottomRight)) {
            bottomRight.setLocation(bottomRight.x + xOffset, bottomRight.y + yOffset);
            upperRight.setLocation(upperRight.x + xOffset, upperRight.y);
            bottomLeft.setLocation(bottomLeft.x, bottomLeft.y + yOffset);
        }
    }

    @Override
    public void Rotate(AffineTransform rotation) {
        Point2D a = new Point2D.Double();
        Point2D b = new Point2D.Double();
        if (rotation.getShearY() > 0) { //right
            a.setLocation(upperLeft);
            rotation.transform(bottomLeft, upperLeft);
            b.setLocation(upperRight);
            rotation.transform(a, upperRight);
            a.setLocation(bottomRight);
            rotation.transform(b, bottomRight);
            rotation.transform(a, bottomLeft);
        } else { //left
            a.setLocation(upperLeft);
            rotation.transform(upperRight, upperLeft);
            b.setLocation(bottomLeft);
            rotation.transform(a, bottomLeft);
            a.setLocation(bottomRight);
            rotation.transform(b, bottomRight);
            rotation.transform(a, upperRight);
        }
    }

    @Override
    public void Translate(AffineTransform translate) {
        translate.transform(upperLeft, upperLeft);
        translate.transform(upperRight, upperRight);
        translate.transform(bottomLeft, bottomLeft);
        translate.transform(bottomRight, bottomRight);
    }

    @Override
    public void Mirror(Point A,Point B) {
        Point p = new Point();
        //***is this right-left mirroring
        if (A.x == B.x) {
            //***which place in regard to x origine
            p.setLocation(upperRight);
            upperRight.setLocation(Utilities.mirrorPoint(A,B, upperLeft));
            upperLeft.setLocation(Utilities.mirrorPoint(A,B, p));
            p.setLocation(bottomRight);
            bottomRight.setLocation(Utilities.mirrorPoint(A,B, bottomLeft));
            bottomLeft.setLocation(Utilities.mirrorPoint(A,B, p));
        } else { //***top-botom mirroring
            //***which place in regard to y origine
            p.setLocation(bottomLeft);
            bottomLeft.setLocation(Utilities.mirrorPoint(A,B, upperLeft));
            upperLeft.setLocation(Utilities.mirrorPoint(A,B, p));
            p.setLocation(bottomRight);
            bottomRight.setLocation(Utilities.mirrorPoint(A,B, upperRight));
            upperRight.setLocation(Utilities.mirrorPoint(A,B, p));
        }
    }

    @Override
    public void setSelected(boolean selection) {
        super.setSelected(selection);
        if (!selection) {
            resizingPoint = null;
        }
    }

    @Override
    public int getY() {
        return upperLeft.y;
    }

    @Override
    public int getX() {
        return upperLeft.x;
    }

    public void setY(int y) {
        Initialize(upperLeft.x, y, getWidth(), getHeight());
    }

    public void setX(int x) {
        Initialize(x, upperLeft.y, getWidth(), getHeight());
    }

    @Override
    public int getWidth() {
        return upperRight.x - upperLeft.x;
    }

    @Override
    public int getHeight() {
        return bottomLeft.y - upperLeft.y;
    }

    public void setWidth(int width) {
        upperRight.setLocation(upperLeft.x + width, upperRight.y);
        bottomRight.setLocation(bottomLeft.x + width, bottomRight.y);
    }


    public void setHeight(int height) {
        bottomLeft.setLocation(bottomLeft.x, upperLeft.y + height);
        bottomRight.setLocation(bottomRight.x, upperRight.y + height);
    }
    
    public int getCenterX(){
        return (int)(upperLeft.getX()+((upperRight.getX()-upperLeft.getX())/2));
    }
    
    public int getCenterY(){
        return (int)(upperRight.getY()+((bottomRight.getY()-upperRight.getY())/2));        
    }
    
    @Override
    public AbstractMemento getState(MementoType operationType) {
        Memento memento=new Memento(operationType);
        memento.saveStateFrom(this);        
        return memento;
    }

    @Override
    public void setState(AbstractMemento memento) {
        memento.loadStateTo(this);   
    }
    
    public static class Memento extends AbstractMemento<Unit,ResizableShape>{
        private int Ax[];
        
        private int Ay[];
        
        public Memento(MementoType mementoType) {
            super(mementoType);
            Ax=new int[4];
            Ay=new int[4];
        }
        @Override
        public void saveStateFrom(ResizableShape shape) {
            super.saveStateFrom(shape);
            Ax[0]=shape.upperLeft.x;            
            Ay[0]=shape.upperLeft.y;
            Ax[1]=shape.upperRight.x;            
            Ay[1]=shape.upperRight.y;
            Ax[2]=shape.bottomLeft.x;            
            Ay[2]=shape.bottomLeft.y;
            Ax[3]=shape.bottomRight.x;            
            Ay[3]=shape.bottomRight.y;   
        }
        
        public void loadStateTo(ResizableShape shape) {
            super.loadStateTo(shape);
            shape.upperLeft.setLocation(Ax[0],Ay[0]);
            shape.upperRight.setLocation(Ax[1],Ay[1]);
            shape.bottomLeft.setLocation(Ax[2],Ay[2]);
            shape.bottomRight.setLocation(Ax[3],Ay[3]);            
        }
        public void Clear() {
            super.Clear();
            Ax=null;
            Ay=null;
        }
        
        @Override
        public boolean isSameState(Unit unit) {
            ResizableShape other=(ResizableShape)unit.getShape(getUUID());
            return (other.getThickness()==this.thickness&&other.getFill().ordinal()==this.fill&&other.copper.ordinal()==this.layerindex&&
                    (other.upperLeft.x==this.Ax[0])&&(other.upperLeft.y==this.Ay[0])&&
                    (other.upperRight.x==this.Ax[1])&&(other.upperRight.y==this.Ay[1])&&
                    (other.bottomLeft.x==this.Ax[2])&&(other.bottomLeft.y==this.Ay[2])&&
                    (other.bottomRight.x==this.Ax[3])&&(other.bottomRight.y==this.Ay[3])                
                   );
            
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
            return (other.getMementoType().equals(this.getMementoType())&&
                    other.getUUID().equals(this.getUUID())&&
                    other.thickness==this.thickness&&
                    other.fill==this.fill&&
                    other.layerindex==this.layerindex&&
                    Arrays.equals(Ax,other.Ax)&&
                    Arrays.equals(Ay,other.Ay)                                  
                   );
              
        }
        
        @Override
        public int hashCode(){
           int hash=1; 
           hash=hash*31+getUUID().hashCode()+this.getMementoType().hashCode()+this.fill+this.thickness+this.layerindex;
           hash+=Arrays.hashCode(Ax);
           hash+=Arrays.hashCode(Ay);                                
           return hash;  
        }

    }
}
