package com.mynetpcb.d2.shapes;

import com.mynetpcb.d2.shapes.FontText.TextMetrics;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
/*
 * Alignment/Justified based texture
 */
public class BaseFontText  extends GeometricFigure {
    public int alignment;
    public String text;    
    public int fontSize,fontStyle;
    public Point anchorPoint;
    public final TextMetrics metrics;
    private Font font;
    
    public BaseFontText(double x,double y,String text,int alignment,int fontSize){                   
        this(x,y,text,alignment,fontSize,Font.PLAIN); 
    }
    public BaseFontText(double x,double y,String text,int alignment,int fontSize,int fontStyle){        
        this.alignment=alignment;                   
        this.anchorPoint=new Point(x,y);
        this.text=text;                
        this.fontStyle=fontStyle;
        this.fontSize=fontSize;            
        this.font = new Font(FontText.FONT_NAME,fontStyle,fontSize);
        this.metrics = new TextMetrics();
        this.metrics.calculateMetrics(this.font,text);   
        
    }
    
    @Override
    public BaseFontText clone() {        
        BaseFontText copy= new BaseFontText(anchorPoint.x,anchorPoint.y,this.text,this.alignment,this.fontSize,this.fontStyle);                        
        return copy;
    }
    public void scale(double alpha){
       this.anchorPoint.scale(alpha);
       this.fontSize=(int)(this.fontSize*alpha);
       this.font = new Font(FontText.FONT_NAME,fontStyle,fontSize);
       this.metrics.calculateMetrics(font,text);        
    }
    
    public void mirror(Line line){
       this.anchorPoint.mirror(line); 
    }
    public void move(double offsetX,double offsetY){
            this.anchorPoint.move(offsetX,offsetY);
    }
    public void setStyle(int style){
        this.fontStyle=style;
        this.font = new Font(FontText.FONT_NAME,fontStyle,fontSize);
        this.metrics.calculateMetrics(font,text);         
    }
    public void setSize(int fontSize){
        this.fontSize=fontSize;
        this.font = new Font(FontText.FONT_NAME,fontStyle,fontSize);
        this.metrics.calculateMetrics(font,text); 
    }
    public void setText(String text){
        this.text=text;
        this.font = new Font(FontText.FONT_NAME,fontStyle,fontSize);
        this.metrics.calculateMetrics(font,text); 
    }      
    public Box  box(){
        if (this.text == null || this.text.length() == 0){
            return null;
        }   

        Box box=null;
             switch(this.alignment){
               case 2:
                   box= Box.fromRect(this.anchorPoint.x,this.anchorPoint.y-this.metrics.ascent,this.metrics.width,this.metrics.height);     
                break;
               case 0:
                   box= Box.fromRect(this.anchorPoint.x-this.metrics.width,this.anchorPoint.y-this.metrics.ascent,this.metrics.width,this.metrics.height);
               break;
               case 1:
                   box=Box.fromRect(this.anchorPoint.x - this.metrics.ascent,
                                      this.anchorPoint.y, this.metrics.height,this.metrics.width);
               break;          
               case 3:
                   box= Box.fromRect(this.anchorPoint.x - this.metrics.ascent,
                                      this.anchorPoint.y - this.metrics.width,
                                      this.metrics.height, this.metrics.width);
               break;                
             }
             
             return box;
             
    }
//    public Box  box(){
//        return new Box(this.anchorPoint.x-(this.metrics.width/2), this.anchorPoint.y-(this.metrics.height/2),this.anchorPoint.x+(this.metrics.width/2), this.anchorPoint.y+(this.metrics.height/2));
//    }

    @Override
    public void paint(Graphics2D g2, boolean fill) {
        g2.setFont(font); 
        switch (this.alignment) {
          case 0:   //RIGHT            
            g2.drawString(text, (float)(this.anchorPoint.x-metrics.width), (float)this.anchorPoint.y);            
            break;
          case 1:
            AffineTransform saved = g2.getTransform();
            AffineTransform rotate =
                AffineTransform.getRotateInstance(-Math.PI / 2,
                                                  this.anchorPoint.x,
                                                  this.anchorPoint.y);
            g2.transform(rotate);
            TextLayout layout =
                new TextLayout(text, g2.getFont(), g2.getFontRenderContext());
            g2.drawString(text,(float)(this.anchorPoint.x  - metrics.width),(float)this.anchorPoint.y);
            g2.setTransform(saved);            
            break;
          case 2:   //LEFT
            g2.drawString(text, (float)this.anchorPoint.x, (float)this.anchorPoint.y);            
            break;
          case 3:
            saved = g2.getTransform();
            rotate =
                AffineTransform.getRotateInstance(-Math.PI / 2,
                                                  this.anchorPoint.x,
                                                  this.anchorPoint.y);
            g2.transform(rotate);
            g2.drawString(text, (float)this.anchorPoint.x, (float)this.anchorPoint.y);   
            g2.setTransform(saved);             
            break;        
        }
        //Box b=this.box();
        //b.paint(g2, fill);
        //Utils.drawCrosshair(g2,4,this.anchorPoint);
    }
    
    @Override
    public void rotate(double angle, Point center) {
      

    }

    @Override
    public void rotate(double angle) {
     
    }
}
