package com.mynetpcb.core.capi;


import com.mynetpcb.d2.shapes.Box;
import com.mynetpcb.d2.shapes.Line;
import com.mynetpcb.d2.shapes.Point;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;


public class Grid implements Cloneable{     
    public enum Units{
        MM("MM"),
        INCH("IN"),
        PIXEL("");
        private String pcbunit;
        Units(String pcbunit){
            this.pcbunit=pcbunit;
        }
        
        public String getPcbUnit(){
            return pcbunit;
        }
    }
    private int gridPointToPoint;
    
    //mm/inch
    private double value;
    
    //pixels limit
    private final int pixelToPixelLimit=4;
    
    private Color pointsColor;
    
    private Units units;
    
    public Grid(double value,Units units) {
       this.pointsColor=Color.WHITE;
       this.setGridUnits(value,units);
    }
    
    public void setPointsColor(Color pointsColor){
        this.pointsColor=pointsColor;
    }
    
    public int getGridPointToPoint(){
        return gridPointToPoint;
    }
    
    public Units getGridUnits(){
      return units;
    }
    
    public void setGridUnits(double value,Units units){
        this.value=value;
        this.units=units;
        switch(units){
        case MM:
            this.gridPointToPoint=(int)Grid.MM_TO_COORD(value);
            break;
        case INCH:
            throw new IllegalStateException("BG is in EU -> stick to mm for now.");
        case PIXEL:
            this.gridPointToPoint=(int)value;
        }  
    }
    
    public double UNIT_TO_COORD(double mm){
      switch(this.units){ 
        case MM:
            return MM_TO_COORD(mm);
        case INCH:
            throw new IllegalStateException("BG is in EU -> stick to mm for now.");
        case PIXEL:
            return mm;
      default:
          throw new IllegalStateException("Unknown unit.");
      }            
    }
    
    public double COORD_TO_UNIT(double coord){
      switch(this.units){ 
        case MM:
            return COORD_TO_MM(coord);
        case INCH:
            throw new IllegalStateException("BG is in EU -> stick to mm for now.");
        case PIXEL:
            return coord;
      default:
          throw new IllegalStateException("Unknown unit.");
      }            
    }
    
    public void setGridValue(double value){
        this.setGridUnits(value, units);
    }
    
    public double getGridValue(){
       return value; 
    }
    
    public void Paint(Graphics2D g2,ViewportWindow viewportWindow,AffineTransform scale){
      drawPoints(g2, viewportWindow, scale);         
    }    
    
    //***viewport window is the visible scaled window rectangle. Must scale back to 1 to find the initial grid point.
    //Take advantage of the scale factor of 2.
    private void drawPoints(Graphics2D g2,ViewportWindow viewportWindow,AffineTransform scale){
         int w = 0, h = 0;
        
             //scale out the visible static rectangle to the real schema size to see which point fall in to be rendered.
             //scale back to origine
         Box r=Box.fromRect((viewportWindow.getX()/scale.getScaleX()),(viewportWindow.getY()/scale.getScaleX()),(viewportWindow.getWidth()/scale.getScaleX()),(viewportWindow.getHeight()/scale.getScaleX()));
         
         Point position=this.positionOnGrid(r.min.x,r.min.y);
             
             
         if(!this.isGridDrawable(position,scale)){
             return;
         }                     
         Point point=new Point(0,0);  
         g2.setPaint(pointsColor);
         g2.setStroke(new BasicStroke());  
        
         Line line=new Line(null,null);
         for (h =(int)position.y; h <= position.y+(int)r.getHeight(); h += this.gridPointToPoint) {
                 for (w =(int)position.x; w <=position.x+(int)r.getWidth(); w += this.gridPointToPoint) {
                      point.set(w, h); 
                      
                      point.scale(scale.getScaleX());
                              
                      point.set(point.x-viewportWindow.getX(),point.y-viewportWindow.getY());
                         
                      if(point.x>viewportWindow.getWidth()||point.y>viewportWindow.getHeight()){                   
                              continue;  
                      }   
                     
                      line.setLine(point,point);
                      line.paint(g2, false);                                      
                                      
                 }
             }   

    }
    
    //set a limit to point to point distance if < 3 px then don't draw
    /**
     *
     * @param point to calculate the distance from
     * @param scale current scale
     * @return is grid drawable
     */
    private boolean isGridDrawable(Point point,AffineTransform scale){
            double x=point.x*scale.getScaleX();
            double xx=(point.x+this.gridPointToPoint)*scale.getScaleX();
            return  ((int)(Math.round(xx-x)))>this.pixelToPixelLimit;  
    }
//    private void drawLines(Graphics2D g2,Rectangle clipRect,AffineTransform scale){
//        int w = 0, h = 0;
//        
//        g2.setPaint(Color.GRAY);
//
//        Point position=positionOnGrid(clipRect.x,clipRect.y);
//        Point scaledPoint=new Point();
//        Line2D line=new Line2D.Double();
// 
//        for (w =position.x; 
//                 w <=position.x+clipRect.getWidth(); 
//                 w += gridPointToPoint) {
//                
//                 scaledPoint.setLocation(w-clipRect.x, position.y-clipRect.y); 
//                 scale.transform(scaledPoint,scaledPoint);
//                 
//                 line.setLine(scaledPoint.getX(),scaledPoint.getY(),scaledPoint.getX(),scaledPoint.getY()+clipRect.getHeight())  ;
//                 g2.draw(line);                        
//               
//        }
//            
//        for (h =position.y; 
//             h <= position.y+clipRect.getHeight(); 
//             h += gridPointToPoint) {
//         
//          scaledPoint.setLocation(position.x-clipRect.x, h-clipRect.y); 
//          scale.transform(scaledPoint,scaledPoint);
//          
//          line.setLine(scaledPoint.getX(),scaledPoint.getY(),scaledPoint.getX()+clipRect.getWidth(),scaledPoint.getY())  ;
//          g2.draw(line);                        
//        
//     }
//    }
    public Point positionOnGrid(double x, double y) {        
        double  ftmp     = x / gridPointToPoint;
        int xx = ( (int) Math.round( ftmp ) ) * gridPointToPoint;

        ftmp     = (double) y / gridPointToPoint;
        int yy = ( (int) Math.round( ftmp ) ) * gridPointToPoint;
        return new Point(xx,yy);        
    }   
    
    public int lengthOnGrid(double length) {        
        double  ftmp     = length / gridPointToPoint;
        int xx = ( (int) Math.round( ftmp ) ) * gridPointToPoint;        
        return xx;        
    }
    
    public Point positionOnGrid(Point p) {        
         return positionOnGrid(p.x,p.y);      
    } 
    
    public void snapToGrid(Point p) {                
        p.set(lengthOnGrid(p.x), lengthOnGrid(p.y));
    } 
    
    //must be 10000 for printing
    public static double MM_TO_COORD(double mm){
      //return Math.floor(mm*10000);
      return (mm*10000);
    }
 
    public static double COORD_TO_MM(double coord){
      return (coord/10000);    
    }
    @Override
    public Grid clone() throws CloneNotSupportedException {
        
        return (Grid)super.clone();
    }
}


