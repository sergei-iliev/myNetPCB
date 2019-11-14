package com.mynetpcb.d2.shapes;


import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class FontText extends Shape {
    private static final BufferedImage bi=new BufferedImage(1, 1, BufferedImage.TYPE_BYTE_INDEXED);  
        
    public Point anchorPoint;
    public String text;
    public int fontSize,fontStyle;
    public double rotate;
    
    private final TextMetrics metrics;
    
    public FontText(double x,double y,String text,int fontSize){
        this.anchorPoint=new Point(x,y);
        this.text=text;
        this.fontSize=fontSize;            
        this.fontStyle=Font.BOLD;
        this.metrics = new TextMetrics();
        this.metrics.calculateMetrics(text, fontStyle,fontSize,rotate);
        
    }
    @Override
    public FontText clone() {        
        FontText copy= new FontText(anchorPoint.x,anchorPoint.y,this.text,this.fontSize);        
        copy.rotate=this.rotate;
        return copy;
    }
    public void scale(double alpha){
       this.anchorPoint.scale(alpha);
       this.fontSize=(int)(this.fontSize*alpha);
       this.metrics.calculateMetrics(text, fontStyle,fontSize,rotate);        
    }
    
    public void rotate(double angle, Point center){
            this.anchorPoint.rotate((angle-this.rotate),center);
            this.rotate=angle;
    }
    public void move(double offsetX,double offsetY){
            this.anchorPoint.move(offsetX,offsetY);
    }
    public void setStyle(int style){
        this.fontStyle=style;
        this.metrics.calculateMetrics(this.text,this.fontStyle,this.fontSize,this.rotate);
    }
    public void setSize(int fontSize){
            this.fontSize=fontSize;
            this.metrics.calculateMetrics(this.text,this.fontStyle,this.fontSize,this.rotate);
    }
    public void setText(String text){
            this.text=text;
            this.metrics.calculateMetrics(this.text,this.fontStyle,this.fontSize,this.rotate);
    }    
    
    /**
    if (x-x1)/(x2-x1) = (y-y1)/(y2-y1) = alpha (a constant), then the point C(x,y) will lie on the line between pts 1 & 2.
    If alpha < 0.0, then C is exterior to point 1.
    If alpha > 1.0, then C is exterior to point 2.
    Finally if alpha = [0,1.0], then C is interior to 1 & 2.
    */
    public boolean contains(Point pt){                      
            
            /*
             * Based on the assumption that anchorPoint is middle normal aligned
             */
            
            Point ps=this.anchorPoint.clone();
            ps.move(-(this.metrics.width/2),0);
            
            Point pe=this.anchorPoint.clone();
            pe.move(this.metrics.width/2,0);
            
            Line l=new Line(ps,pe);
            l.rotate(this.rotate,this.anchorPoint);

            Point projectionPoint=l.projectionPoint(pt);
    
            double a=(projectionPoint.x-ps.x)/((pe.x-ps.x)==0?1:pe.x-ps.x);
            double b=(projectionPoint.y-ps.y)/((pe.y-ps.y)==0?1:pe.y-ps.y);

            double dist=projectionPoint.distanceTo(pt);
        
            if(0<=a&&a<=1&&0<=b&&b<=1){  //is projection between start and end point
                if(dist<=(Math.abs(this.metrics.height/2 ))){
                    return true;
            }    
            
        }
            return false;
    
    }
    public Box  box(){
        return new Box(this.anchorPoint.x-(this.metrics.width/2), this.anchorPoint.y-(this.metrics.height/2),this.anchorPoint.x+(this.metrics.width/2), this.anchorPoint.y+(this.metrics.height/2));
    }
    
    @Override
    public void paint(Graphics2D g2,boolean fill) {
        
        Font font = new Font(Font.MONOSPACED,fontStyle,fontSize);
        g2.setFont(font); 
        Box r=this.box();
        
        
        
        
        AffineTransform saved = g2.getTransform();
        AffineTransform rotate =
            AffineTransform.getRotateInstance(Utils.radians(360-this.rotate), this.anchorPoint.x,this.anchorPoint.y);
        g2.transform(rotate);
 
        // Determine the X coordinate for the text
        double x = r.getX() + (r.getWidth() - this.metrics.width) / 2;
        // Determine the Y coordinate for the text (note we add the ascent, as in java 2d 0 is top of the screen)
        double y = r.getY() + ((r.getHeight() - metrics.height) / 2) + metrics.ascent;
        // Draw the String
        g2.drawString(text, (float)x, (float)y);
               
        g2.setTransform(saved);
    }

    @Override
    public void rotate(double angle) {
        this.rotate(angle,this.anchorPoint);
    }

    private static class TextMetrics{
         //int fontSize;
         double width,height;
         int  descent;
         int ascent;

         
         public void calculateMetrics(String text,int fontStyle,int fontSize,double rotation){
                 Graphics2D g2 = (Graphics2D)bi.getGraphics();
                 AffineTransform saved = g2.getTransform();
                 AffineTransform rotate =
                           AffineTransform.getRotateInstance(Utils.radians(360-rotation), 0,0);
                 
                 g2.transform(rotate);
             
                 Font font = new Font(Font.MONOSPACED,fontStyle,fontSize);
                 g2.setFont(font);                                   
                 FontMetrics metrics = g2.getFontMetrics(font);
                 // Determine the X coordinate for the text
                 this.width=metrics.stringWidth(text);             
                 this.height = metrics.getHeight();
                 this.ascent=  metrics.getAscent();
                 this.descent=metrics.getDescent();
                 g2.setTransform(saved);                 
                 g2.dispose();
         }
    }
}
