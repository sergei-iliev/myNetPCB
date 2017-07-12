package com.mynetpcb.core.capi;


import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;


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
            this.gridPointToPoint=Grid.MM_TO_COORD(value);
            break;
        case INCH:
            throw new IllegalStateException("BG is in EU -> stick to mm for now.");
        case PIXEL:
            this.gridPointToPoint=(int)value;
        }  
    }
    
    public int UNIT_TO_COORD(double mm){
      switch(this.units){ 
        case MM:
            return MM_TO_COORD(mm);
        case INCH:
            throw new IllegalStateException("BG is in EU -> stick to mm for now.");
        case PIXEL:
            return (int)mm;
      default:
          throw new IllegalStateException("Unknown unit.");
      }            
    }
    
    public double COORD_TO_UNIT(int coord){
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
        g2.setPaint(pointsColor);
        g2.setStroke(new BasicStroke());  
        Point point=new Point();  
        Point2D scaledPoint=new Point2D.Double();
        //scale out the visible static rectangle to the real schema size to see which point fall in to be rendered.
        //scale back to origine
        Rectangle r=new Rectangle((int)(viewportWindow.x/scale.getScaleX()),(int)(viewportWindow.y/scale.getScaleY()),(int)(viewportWindow.getWidth()/scale.getScaleX()),(int)(viewportWindow.getHeight()/scale.getScaleY()));
        
        Point position=positionOnGrid(r.x,r.y);
        
        if(!isGridDrawable(position,scale)){
            return;
        }
        
        Line2D line=new Line2D.Double();

        for (h =position.y; 
             h <= position.y+r.getHeight(); 
             h += gridPointToPoint) {
            for (w =position.x; 
                 w <=position.x+r.getWidth(); 
                 w += gridPointToPoint) {
                
                 point.setLocation(w, h); 
                 scale.transform(point,scaledPoint);                 
                 scaledPoint.setLocation(scaledPoint.getX()-viewportWindow.x,scaledPoint.getY()-viewportWindow.y);
                 //***no need to draw outside of visible rectangle
                 if(scaledPoint.getX()>viewportWindow.getWidth()||scaledPoint.getY()>viewportWindow.getHeight()){                   
                   continue;  
                 }   
                 line.setLine(scaledPoint,scaledPoint);
                 g2.draw(line);                                        
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
        Point2D scaledPoint=new Point2D.Double();
        scale.transform(point,scaledPoint); 
        double x=scaledPoint.getX();
        point.x=point.x+gridPointToPoint;
        scale.transform(point,scaledPoint);
        return  ((int) Math.round(scaledPoint.getX()-x))>pixelToPixelLimit;    
    }
    private void drawLines(Graphics2D g2,Rectangle clipRect,AffineTransform scale){
        int w = 0, h = 0;
        
        g2.setPaint(Color.GRAY);

        Point position=positionOnGrid(clipRect.x,clipRect.y);
        Point scaledPoint=new Point();
        Line2D line=new Line2D.Double();
 
        for (w =position.x; 
                 w <=position.x+clipRect.getWidth(); 
                 w += gridPointToPoint) {
                
                 scaledPoint.setLocation(w-clipRect.x, position.y-clipRect.y); 
                 scale.transform(scaledPoint,scaledPoint);
                 
                 line.setLine(scaledPoint.getX(),scaledPoint.getY(),scaledPoint.getX(),scaledPoint.getY()+clipRect.getHeight())  ;
                 g2.draw(line);                        
               
        }
            
        for (h =position.y; 
             h <= position.y+clipRect.getHeight(); 
             h += gridPointToPoint) {
         
          scaledPoint.setLocation(position.x-clipRect.x, h-clipRect.y); 
          scale.transform(scaledPoint,scaledPoint);
          
          line.setLine(scaledPoint.getX(),scaledPoint.getY(),scaledPoint.getX()+clipRect.getWidth(),scaledPoint.getY())  ;
          g2.draw(line);                        
        
     }
    }
    public Point positionOnGrid(int x, int y)throws ArrayIndexOutOfBoundsException {        
        double  ftmp     = (double) x / gridPointToPoint;
        int xx = ( (int) Math.round( ftmp ) ) * gridPointToPoint;

        ftmp     = (double) y / gridPointToPoint;
        int yy = ( (int) Math.round( ftmp ) ) * gridPointToPoint;
        return new Point(xx,yy);        
    }   
    
    public int positionOnGrid(int length)throws ArrayIndexOutOfBoundsException {        
        double  ftmp     = (double) length / gridPointToPoint;
        int xx = ( (int) Math.round( ftmp ) ) * gridPointToPoint;        
        return xx;        
    }
    
    public Point positionOnGrid(Point p)throws ArrayIndexOutOfBoundsException {        
         return positionOnGrid(p.x,p.y);      
    } 
    
    public void snapToGrid(Point p)throws ArrayIndexOutOfBoundsException {        
        p.setLocation(positionOnGrid(p.x), positionOnGrid(p.y));
    } 
    
    //must be 10000 for printing
    public static int MM_TO_COORD(double mm){
      return (int)Math.floor(mm*10000);
    }
 
    public static double COORD_TO_MM(int coord){
      return ((double)coord/10000);    
    }
    
    public Object clone() throws CloneNotSupportedException { 
      return super.clone();
    }
}


