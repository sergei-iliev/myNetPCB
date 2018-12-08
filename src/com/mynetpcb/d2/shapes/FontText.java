package com.mynetpcb.d2.shapes;

import com.mynetpcb.d2.Utilities;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import org.w3c.dom.css.Rect;

public class FontText extends Shape{
    private Point anchorPoint;
    private String text;
    private int fontSize;
    private double rotation;
    private final TextMetrics metrics;
    
    public FontText(Point anchorPoint,String text,int fontSize){
        this.anchorPoint=anchorPoint;
        this.text=text;
        this.fontSize=fontSize;            
        this.metrics = new TextMetrics();
        this.metrics.calculateMetrics(text, fontSize,rotation);
    }

    public void rotate(double angle,Point center){
            this.anchorPoint.rotate((angle-this.rotation),center==null?this.anchorPoint:center);
            this.rotation=angle;
            this.metrics.calculateMetrics(this.text,this.fontSize,this.rotation);
    }
    
    public void setSize(int fontSize){
            this.fontSize=fontSize;
            this.metrics.calculateMetrics(this.text,this.fontSize,this.rotation);
    }
    @Override
    public FontText clone() {
        return new FontText(this.anchorPoint,this.text,this.fontSize);        
    }
    
    
    public Rectangle2D  getBoundingRect(){
        return new Rectangle2D.Double(this.anchorPoint.getX()-(this.metrics.width/2), this.anchorPoint.getY()-(this.metrics.height/2),this.metrics.width,this.metrics.height);
    }
    
    @Override
    public void paint(Graphics2D g2) {
        g2.setColor(Color.BLACK);
        Font font = new Font(Font.MONOSPACED,Font.BOLD,fontSize);
        g2.setFont(font); 
        Rectangle2D r=this.getBoundingRect();
        
        
        this.anchorPoint.paint(g2);
        
        AffineTransform saved = g2.getTransform();
        AffineTransform rotate =
            AffineTransform.getRotateInstance(Utilities.radians(360-this.rotation), this.anchorPoint.getX(),this.anchorPoint.getY());
        g2.transform(rotate);
 
        // Determine the X coordinate for the text
        double x = r.getX() + (r.getWidth() - this.metrics.width) / 2;
        // Determine the Y coordinate for the text (note we add the ascent, as in java 2d 0 is top of the screen)
        double y = r.getY() + ((r.getHeight() - metrics.height) / 2) + metrics.ascent;
        // Set the font
        g2.setColor(Color.BLACK);
        // Draw the String
        g2.drawString(text, (float)x, (float)y);
        
        g2.setColor(Color.BLUE);
        g2.draw(r);        
        g2.setTransform(saved);
    }
    
    private static class TextMetrics{
         boolean updated;
         //int fontSize;
         double width,height;
         int  descent;
         int ascent;
         Graphics2D g2;
         
         TextMetrics(){
             BufferedImage bi = new BufferedImage(1, 1, BufferedImage.TYPE_BYTE_INDEXED);  
             g2 = (Graphics2D)bi.getGraphics();
         }
         
         public void calculateMetrics(String text,int fontSize,double rotation){
                 AffineTransform saved = g2.getTransform();
                 AffineTransform rotate =
                           AffineTransform.getRotateInstance(Utilities.radians(360-rotation), 0,0);
                 
                 g2.transform(rotate);
             
                 Font font = new Font(Font.MONOSPACED,Font.BOLD,fontSize);
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
