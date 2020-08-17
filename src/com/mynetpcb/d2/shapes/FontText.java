package com.mynetpcb.d2.shapes;


import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class FontText extends GeometricFigure {

    public static final String  FONT_NAME=Font.MONOSPACED;
    
    public Point anchorPoint;
    public String text;
    public int fontSize,fontStyle;
    public double rotate;
    public final TextMetrics metrics;
    private Font font;
    
    public FontText(double x,double y,String text,int fontSize,double rotate){
        this(x,y,text,fontSize,Font.PLAIN,rotate);         
    }
    public FontText(double x,double y,String text,int fontSize,int fontStyle,double rotate){
        this.anchorPoint=new Point(x,y);
        this.text=text;
        this.rotate=rotate;
        this.fontSize=fontSize;            
        this.fontStyle=fontStyle;
        this.font = new Font(FONT_NAME,fontStyle,fontSize);
        this.metrics = new TextMetrics();
        this.metrics.calculateMetrics(this.font,text);        
    }
    
    @Override
    public FontText clone() {        
        FontText copy= new FontText(anchorPoint.x,anchorPoint.y,this.text,this.fontSize,this.fontStyle,this.rotate);                        
        return copy;
    }
    public void scale(double alpha){
       this.anchorPoint.scale(alpha);
       this.fontSize=(int)(this.fontSize*alpha);
       this.font = new Font(FONT_NAME,fontStyle,fontSize);
       this.metrics.calculateMetrics(font,text);        
    }
    
    public void rotate(double angle, Point center){
            this.anchorPoint.rotate((angle-this.rotate),center);
            this.rotate=angle;
    }
    public void mirror(Line line){
       this.anchorPoint.mirror(line); 
    }
    public void move(double offsetX,double offsetY){
            this.anchorPoint.move(offsetX,offsetY);
    }
    public void setStyle(int style){
        this.fontStyle=style;
        this.font = new Font(FONT_NAME,fontStyle,fontSize);
        this.metrics.calculateMetrics(font,text);         
    }
    public void setSize(int fontSize){
        this.fontSize=fontSize;
        this.font = new Font(FONT_NAME,fontStyle,fontSize);
        this.metrics.calculateMetrics(font,text); 
    }
    public void setText(String text){
        this.text=text;
        this.font = new Font(FONT_NAME,fontStyle,fontSize);
        this.metrics.calculateMetrics(font,text); 
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

    public static class TextMetrics{
         //int fontSize;
        double width,height;
        int  descent;
        int ascent;
        
        private static final BufferedImage bi=new BufferedImage(1, 1, BufferedImage.TYPE_BYTE_INDEXED);  
        
        public void calculateMetrics(Font font,String text){
                Graphics2D g2 = (Graphics2D)bi.getGraphics();                            
                g2.setFont(font);                                   
                FontMetrics metrics = g2.getFontMetrics(font);
                // Determine the X coordinate for the text
                this.width=metrics.stringWidth(text);  
                this.height = metrics.getHeight();
                this.ascent=  metrics.getAscent();
                this.descent=metrics.getDescent();                
                g2.dispose();
        }         
    }
}
