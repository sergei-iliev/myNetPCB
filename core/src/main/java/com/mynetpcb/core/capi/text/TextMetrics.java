package com.mynetpcb.core.capi.text;


import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

public class TextMetrics implements Metrics {
    private static final int BUG_FONT_SIZE=10000; 
    
    private static final TextMetrics tmHelper=new TextMetrics(); 
     
    private boolean updated;

    private int width, height;

    private int ascent, descent;
    
    private int fontSize;
    
    private final BufferedImage bi;

    
    public TextMetrics() {
        this.updated = false;
        bi = new BufferedImage(1, 1, BufferedImage.TYPE_BYTE_INDEXED);
        fontSize=-1;
    }
    
    public int getDescent(){
       return descent; 
    }
    
    public int getAscent(){
       return ascent; 
    }
    
    @Override
    public int getWidth(){
      return width;    
    }
    
    @Override
    public int getHeight(){
        return height;
    }


    @Override
    public void updateMetrics() {
       this.updated=false;
       fontSize=-1;
    }

//    @Override
//    public void calculateMetrics(Graphics2D g2,Text.Alignment alignment,Text.Style style,int fontSize, String text) {
//        if(this.fontSize!=fontSize){
//            this.fontSize=fontSize;
//            updated = false;
//        }
//        
//        if(updated==true){
//            return;
//        }
//        if(fontSize>24000){
//            //bug in JAVA 2D -> unable to calculate font metrics of font bigger then 27000 pixels
//            fixTextMetrics( alignment, style, fontSize,  text);
//            updated=true;
//            return;
//        }    
//        //there is no graphics context -> use internal one
//        if(g2==null){
//            g2 = (Graphics2D)bi.getGraphics();
//            Font font = new Font(Text.FONT_NAME,style.ordinal(),fontSize);
//            g2.setFont(font);             
//        }
//        
//        switch (alignment) {
//        case RIGHT:
//        case LEFT:
//            {
//                TextLayout layout =
//                    new TextLayout(text, g2.getFont(), g2.getFontRenderContext());
//                FontMetrics fm = g2.getFontMetrics();
//
//                width = Math.round(layout.getVisibleAdvance());
//
//                height = fm.getHeight();
//
//                ascent = fm.getAscent();
//
//                descent = fm.getDescent();
//                break;
//            }
//
//        case TOP:
//        case BOTTOM:
//            {
//                FontMetrics fm =
//                    g2.getFontMetrics(); // LINUX problem:rotated text is
//                // shorter then normal one!!!!
//                AffineTransform saved = g2.getTransform();
//                AffineTransform rotate =
//                    AffineTransform.getRotateInstance(-Math.PI / 2, 0, 0);
//                g2.transform(rotate);
//                TextLayout layout =
//                    new TextLayout(text, g2.getFont(), g2.getFontRenderContext());
//
//                FontMetrics rfm = g2.getFontMetrics();
//
//                width = Math.round(layout.getVisibleAdvance());
//
//                height =
//                        (rfm.getHeight() == 0 ? fm.getHeight() : rfm.getHeight());
//
//                ascent =
//                        (rfm.getAscent() == 0 ? fm.getAscent() : rfm.getAscent());
//
//                descent =
//                        (rfm.getDescent() == 0 ? fm.getDescent() : rfm.getDescent());
//                g2.setTransform(saved);
//                break;
//            }
//        default:
//            throw new IllegalArgumentException("Wrong alignment.");
//        }
//        updated=true;
//    }
//    
//    private void fixTextMetrics(Text.Alignment alignment,Text.Style style,int fontSize, String text){
//        tmHelper.updateMetrics();
//        tmHelper.calculateMetrics(null, alignment, style,BUG_FONT_SIZE, text);
//        
//        
//        double scale=(double)fontSize/tmHelper.fontSize;
//        this.ascent=((int)Math.round(scale))*tmHelper.ascent;
//        this.descent=((int)Math.round(scale))*tmHelper.descent;
//        this.width=((int)Math.round(scale))*tmHelper.width;
//        this.height=((int)Math.round(scale))*tmHelper.height;
//    }
    
    @Override
    public  String toString(){
        StringBuilder sb=new StringBuilder();
        sb.append("w="+this.width+",h"+this.height+",ascend="+this.ascent+",descend="+this.descent);
        return sb.toString();
        }
}

