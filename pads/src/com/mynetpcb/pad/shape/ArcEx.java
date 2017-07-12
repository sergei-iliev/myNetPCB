package com.mynetpcb.pad.shape;

import com.mynetpcb.core.capi.Grid;
import com.mynetpcb.core.capi.ViewportWindow;
import com.mynetpcb.core.capi.flyweight.FlyweightProvider;
import com.mynetpcb.core.capi.flyweight.ShapeFlyweightFactory;
import com.mynetpcb.core.capi.gerber.ArcGerberable;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.pad.Layer;
import com.mynetpcb.core.utils.Utilities;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Rectangle2D;

@Deprecated
public class ArcEx extends Shape implements ArcGerberable{
    
    private  Arc2D arc;
    private  Point p1,p2,center,mid;
    
    public ArcEx( int x1,int y1,int x2,int y2,int centerx,int centery, int thickness,int layermaskid) {
        super(0,0,0,0, thickness,layermaskid);
        this.arc=new Arc2D.Double();
        p1=new Point(x1,y1);
        p2=new Point(x2,y2);
        mid=new Point();
        center=new Point(centerx,centery);
        toArc2D();
        this.selectionRectWidth=3000;
    }
    
    @Override
    public ArcEx clone() throws CloneNotSupportedException{
        ArcEx copy= (ArcEx)super.clone();
        copy.arc=new Arc2D.Double(arc.getX(),arc.getY(),arc.getWidth(),arc.getWidth(),arc.getAngleStart(),arc.getAngleExtent(),arc.getArcType());
        copy.p1=new Point(this.p1);
        copy.p2=new Point(this.p2);
        copy.mid=new Point(this.mid);
        copy.center=new Point(this.center);
        return copy;
    }
    
    //convert points to Arc2D
    private void toArc2D(){
        double centerx=Grid.COORD_TO_MM(center.x);
        double centery=Grid.COORD_TO_MM(center.y);
 
       double x1=Grid.COORD_TO_MM(p1.x);
       double y1=Grid.COORD_TO_MM(p1.y);

       double x2=Grid.COORD_TO_MM(p2.x);
       double y2=Grid.COORD_TO_MM(p2.y);
        
       double r=Math.sqrt((x1-centerx)*(x1-centerx)+(y1-centery)*(y1-centery));
       double x=centerx-r;
       double y=centery-r;
       
       double width=2*r;
       double startAngle=Math.abs(((180/Math.PI)*Math.atan2(y1-centery,x1-centerx)));
       double endAngle=Math.abs(((180/Math.PI)*Math.atan2(y2-centery, x2-centerx)))-startAngle;
       
       //calculate mid point
       
       
       arc.setArc(Grid.MM_TO_COORD(x),Grid.MM_TO_COORD(y), Grid.MM_TO_COORD(width), Grid.MM_TO_COORD(width), startAngle, endAngle,  Arc2D.OPEN);
       
    }
    
    public ArcEx(){
        this(0,0,0,0,0,0,0,Layer.SILKSCREEN_LAYER_FRONT);
    }
    
    
    @Override
    public Rectangle calculateShape() {
      return new Rectangle((int)arc.getMinX() ,(int)arc.getMinY(),(int)arc.getWidth(),(int)arc.getWidth());
    }
    
    @Override
    public boolean isClicked(int x, int y) {
        if(arc.contains(x,y))
         return true;
        else
         return false;   
    }
    
    @Override
    public void Move(int xoffset, int yoffset) {
        p1.x=(p1.x + xoffset);
        p1.y=(p1.y + yoffset);
        
        p2.x=(p2.x + xoffset);
        p2.y=(p2.y + yoffset);

        center.x=(center.x + xoffset);
        center.y=(center.y + yoffset);
        
        toArc2D();
    }
    
    @Override
    public void Paint(Graphics2D g2, ViewportWindow viewportWindow, AffineTransform scale,int layermask) {
        //is this my layer mask
        if((this.getCopper().getLayerMaskID()&layermask)==0){
            return;
        }
        Rectangle2D scaledRect = Utilities.getScaleRect(arc.getBounds() ,scale); 
        if(!scaledRect.intersects(viewportWindow)){
          return;   
        }
        
        FlyweightProvider provider =ShapeFlyweightFactory.getProvider(Arc2D.class);
        Arc2D temporal=(Arc2D)provider.getShape();
       
        temporal.setFrame(scaledRect.getX()-viewportWindow.x ,scaledRect.getY()-viewportWindow.y,scaledRect.getWidth(),scaledRect.getWidth());
        temporal.setAngleStart(arc.getAngleStart());
        temporal.setAngleExtent(arc.getAngleExtent());
        
        AlphaComposite composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f);   
        Composite originalComposite = g2.getComposite();                     
        g2.setComposite(composite ); 
        
        g2.setColor(isSelected()?Color.GRAY:copper.getColor()); 
        //if(fill==Fill.EMPTY){    //framed   
          double wireWidth=thickness*scale.getScaleX();       
          g2.setStroke(new BasicStroke((float)wireWidth,1,1));          
          g2.draw(temporal);
          System.out.println(temporal.getX()+":"+temporal.getY()+";"+temporal.getWidth()+":"+temporal.getHeight()+"-"+temporal.getAngleStart()+"-"+temporal.getAngleExtent());
        //}else{               //filled    
        //  g2.fill(temporal);  
        //} 
        g2.setComposite(originalComposite);
        //if(isSelected()){         
            this.drawControlShape(g2,viewportWindow,scale);            
        //}
        
        provider.reset();
    }

    public void drawControlShape(Graphics2D g2, ViewportWindow viewportWindow, AffineTransform scale) {
        Utilities.drawCrosshair(g2, viewportWindow, scale, null, selectionRectWidth, new Point((int)arc.getMinX(),(int)arc.getMinY()) ,new Point((int)arc.getMaxX(),(int)arc.getMaxY()));
    }
    
    @Override
    public Point getCenterPoint() {
        return p1;
    }
    
    @Override
    public Point getStartPoint() {
        return p1;
    }

    @Override
    public Point getEndPoint() {
        return p2;
    }
    
    public int getI(){
        return 0;
    }
    
    public int getJ(){
        return 0;
    }
    
    @Override
    public boolean isSingleQuadrant() {
        // TODO Implement this method
        return false;
    }

    @Override
    public boolean isClockwise() {
        // TODO Implement this method
        return false;
    }
}
