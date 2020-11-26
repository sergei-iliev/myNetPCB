package com.mynetpcb.core.capi.shape;

import com.mynetpcb.core.capi.Resizeable;
import com.mynetpcb.core.capi.layer.Layer;
import com.mynetpcb.d2.shapes.Box;
import com.mynetpcb.d2.shapes.Line;
import com.mynetpcb.d2.shapes.Point;
import com.mynetpcb.d2.shapes.Utils;


/*
 * Needed for Rect only - it allows close figure for gradual rendering
 * d2 lib rect is based on points and arcs
 */
public abstract class ResizableShape extends Shape implements Resizeable {

    protected Point upperLeft, upperRight, bottomLeft, bottomRight;

    public ResizableShape() {
        super(1,Layer.LAYER_ALL);
        upperLeft = new Point();
        upperRight = new Point();
        bottomLeft = new Point();
        bottomRight = new Point();
        init(0, 0, 50,20);
    }

    @Override
    public ResizableShape clone() throws CloneNotSupportedException {
        ResizableShape copy = (ResizableShape)super.clone();
        copy.upperLeft = new Point(upperLeft.x,upperLeft.y);
        copy.upperRight = new Point(upperRight.x,upperRight.y);
        copy.bottomLeft = new Point(bottomLeft.x,bottomLeft.y);
        copy.bottomRight = new Point(bottomRight.x,bottomRight.y);
        return copy;
    }

    protected void init(double x, double y, double width, double height) {
        upperLeft.set(x, y);
        upperRight.set(x + width, y);
        bottomLeft.set(x, y + height);
        bottomRight.set(x + width, y + height);
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
    public void alignResizingPointToGrid(Point targetPoint){
        Point point=getOwningUnit().getGrid().positionOnGrid(targetPoint.x,targetPoint.y);  
        Point pt=getOwningUnit().getGrid().positionOnGrid(point.x -targetPoint.x,point.y-targetPoint.y);  
        resize((int)pt.x,(int)pt.y,targetPoint);     
    }
    @Override
    public Point getCenter() {        
        return new Point(getX()+(getWidth()/2), getY()+ (getHeight()/2));
    }    
    
    @Override
    public Box getBoundingShape() {
        return Box.fromRect(getX(), getY(), getWidth(), getHeight());
    }
    
    @Override
    public Point isControlRectClicked(int x, int y) {        
        Point p = new Point(x, y);
  
        if(Utils.LE(upperLeft.distanceTo(p),this.selectionRectWidth/2)){                                  
                     return upperLeft;
        }  
        
        if(Utils.LE(upperRight.distanceTo(p),this.selectionRectWidth/2)){                                  
                     return upperRight;
        }  
        
        if(Utils.LE(bottomLeft.distanceTo(p),this.selectionRectWidth/2)){                                  
                     return bottomLeft;
        }  
        
        if(Utils.LE(bottomRight.distanceTo(p),this.selectionRectWidth/2)){                                  
                    return bottomRight;
        }


        return null;
    }
    @Override
    public void move(double xoffset, double yoffset) {
        upperLeft.move(xoffset, yoffset);
        upperRight.move(xoffset, yoffset);
        bottomLeft.move(xoffset, yoffset);
        bottomRight.move(xoffset, yoffset);            
    }

    @Override
    public void resize(int xOffset, int yOffset, Point clickedPoint) {
        if (clickedPoint.equals(upperLeft)) {
            upperLeft.set(upperLeft.x + xOffset, upperLeft.y + yOffset);
            bottomLeft.set(bottomLeft.x + xOffset, bottomLeft.y);
            upperRight.set(upperRight.x, upperRight.y + yOffset);
        } else if (clickedPoint.equals(upperRight)) {
            upperRight.set(upperRight.x + xOffset, upperRight.y + yOffset);
            bottomRight.set(bottomRight.x + xOffset, bottomRight.y);
            upperLeft.set(upperLeft.x, upperLeft.y + yOffset);
        } else if (clickedPoint.equals(bottomLeft)) {
            bottomLeft.set(bottomLeft.x + xOffset, bottomLeft.y + yOffset);
            upperLeft.set(upperLeft.x + xOffset, upperLeft.y);
            bottomRight.set(bottomRight.x, bottomRight.y + yOffset);
        } else if (clickedPoint.equals(bottomRight)) {
            bottomRight.set(bottomRight.x + xOffset, bottomRight.y + yOffset);
            upperRight.set(upperRight.x + xOffset, upperRight.y);
            bottomLeft.set(bottomLeft.x, bottomLeft.y + yOffset);
        }
    }

    @Override
    public void rotate(double angle,Point center) {
        Point tmp = new Point();
        upperLeft.rotate(angle, center);
        upperRight.rotate(angle, center);
        bottomRight.rotate(angle, center);
        bottomLeft.rotate(angle, center);
        
        if (angle < 0) { //right            
            tmp.set(upperLeft);
            upperLeft.set(bottomLeft);
            bottomLeft.set(bottomRight);
            bottomRight.set(upperRight);
            upperRight.set(tmp);
                        
        } else { //left
           tmp.set(upperLeft);
           upperLeft.set(upperRight);
           upperRight.set(bottomRight);
           bottomRight.set(bottomLeft);
           bottomLeft.set(tmp);                                   
        }
    }



    @Override
    public void mirror(Line line) {
        Point p = new Point();
        //***is this right-left mirroring
        if (line.isHorizontal()) {
            //***which place in regard to x origine
            p.set(bottomLeft);
            upperLeft.mirror(line);
            bottomLeft.set(upperLeft);
            p.mirror(line);
            upperLeft.set(p);

            p.set(bottomRight);
            upperRight.mirror(line);
            bottomRight.set(upperRight);
            p.mirror(line);
            upperRight.set(p);
            
        } else { 
            p.set(upperRight);
            upperLeft.mirror(line);
            upperRight.set(upperLeft);
            p.mirror(line);
            upperLeft.set(p);
            
            p.set(bottomRight);
            bottomLeft.mirror(line);
            bottomRight.set(bottomLeft);
            p.mirror(line);
            bottomLeft.set(p);
                        
        }
    }

    public double getY() {
        return upperLeft.y;
    }

    public double getX() {
        return upperLeft.x;
    }

    

    
    public double getWidth() {
        return upperRight.x - upperLeft.x;
    }

    
    public double getHeight() {
        return bottomLeft.y - upperLeft.y;
    }

    public void setWidth(int width) {
        upperRight.set(upperLeft.x + width, upperRight.y);
        bottomRight.set(bottomLeft.x + width, bottomRight.y);
    }


    public void setHeight(int height) {
        bottomLeft.set(bottomLeft.x, upperLeft.y + height);
        bottomRight.set(bottomRight.x, upperRight.y + height);
    }
    


    
//    public static class Memento extends AbstractMemento<Unit,ResizableShape>{
//        private int Ax[];
//        
//        private int Ay[];
//        
//        public Memento(MementoType mementoType) {
//            super(mementoType);
//            Ax=new int[4];
//            Ay=new int[4];
//        }
//        @Override
//        public void saveStateFrom(ResizableShape shape) {
//            super.saveStateFrom(shape);
//            Ax[0]=shape.upperLeft.x;            
//            Ay[0]=shape.upperLeft.y;
//            Ax[1]=shape.upperRight.x;            
//            Ay[1]=shape.upperRight.y;
//            Ax[2]=shape.bottomLeft.x;            
//            Ay[2]=shape.bottomLeft.y;
//            Ax[3]=shape.bottomRight.x;            
//            Ay[3]=shape.bottomRight.y;   
//        }
//        
//        public void loadStateTo(ResizableShape shape) {
//            super.loadStateTo(shape);
//            shape.upperLeft.setLocation(Ax[0],Ay[0]);
//            shape.upperRight.setLocation(Ax[1],Ay[1]);
//            shape.bottomLeft.setLocation(Ax[2],Ay[2]);
//            shape.bottomRight.setLocation(Ax[3],Ay[3]);            
//        }
//        public void Clear() {
//            super.Clear();
//            Ax=null;
//            Ay=null;
//        }
//        
//        @Override
//        public boolean isSameState(Unit unit) {
//            ResizableShape other=(ResizableShape)unit.getShape(getUUID());
//            return (other.getThickness()==this.thickness&&other.getFill().ordinal()==this.fill&&other.copper.ordinal()==this.layerindex&&
//                    (other.upperLeft.x==this.Ax[0])&&(other.upperLeft.y==this.Ay[0])&&
//                    (other.upperRight.x==this.Ax[1])&&(other.upperRight.y==this.Ay[1])&&
//                    (other.bottomLeft.x==this.Ax[2])&&(other.bottomLeft.y==this.Ay[2])&&
//                    (other.bottomRight.x==this.Ax[3])&&(other.bottomRight.y==this.Ay[3])                
//                   );
//            
//        }
//        
//        @Override
//        public boolean equals(Object obj){
//            if(this==obj){
//              return true;  
//            }
//            if(!(obj instanceof Memento)){
//              return false;  
//            }
//            
//            Memento other=(Memento)obj;
//            return (other.getMementoType().equals(this.getMementoType())&&
//                    other.getUUID().equals(this.getUUID())&&
//                    other.thickness==this.thickness&&
//                    other.fill==this.fill&&
//                    other.layerindex==this.layerindex&&
//                    Arrays.equals(Ax,other.Ax)&&
//                    Arrays.equals(Ay,other.Ay)                                  
//                   );
//              
//        }
//        
//        @Override
//        public int hashCode(){
//           int hash=1; 
//           hash=hash*31+getUUID().hashCode()+this.getMementoType().hashCode()+this.fill+this.thickness+this.layerindex;
//           hash+=Arrays.hashCode(Ax);
//           hash+=Arrays.hashCode(Ay);                                
//           return hash;  
//        }
//
//    }

}

