package com.mynetpcb.core.capi.text.font;


import com.mynetpcb.core.capi.ViewportWindow;
import com.mynetpcb.core.capi.print.PrintContext;
import com.mynetpcb.core.capi.text.CompositeTextMetrics;
import com.mynetpcb.core.capi.text.Text;
import com.mynetpcb.core.capi.text.Texture;
import com.mynetpcb.core.utils.Utilities;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import org.w3c.dom.Node;


public class FontTexture implements Texture {

    private String text;

    private Point anchorPoint; //***baseline!

    private String tag; //reference,name and so on - uniquely identifies Text

    private boolean isTextLayoutVisible;

    private Text.Style style;
    
    private Text.Alignment alignment;
    
    private boolean isSelected;
    
    private int size;
    
    private CompositeTextMetrics compositeTextMetrics; 
    
    //***only ChipText could manipulate it 
    private int id,layermaskId;
    
    private Color fillColor;
    
    private int selectionRectWidth;
    
    public FontTexture(String tag, String text, int x, int y,
                   Text.Alignment alignment,int size) {
        anchorPoint = new Point(x, y);
        this.id=-1; 
        this.text = text;
        this.tag = tag;
        this.isTextLayoutVisible = false;
        this.style = Text.Style.PLAIN;
        this.alignment=alignment;
        this.size=size;
        this.compositeTextMetrics=new CompositeTextMetrics();
        this.fillColor=Color.WHITE;
        this.selectionRectWidth=4;
        this.alignment=alignment;
    }
    public FontTexture clone() throws CloneNotSupportedException {
        FontTexture copy=(FontTexture)super.clone();
        copy.anchorPoint=(Point)anchorPoint.clone();
        copy.compositeTextMetrics=new CompositeTextMetrics();
        copy.fillColor=new Color(this.fillColor.getRGB());
        return copy;
    }
    public void copy(Texture _copy){
        FontTexture copy=(FontTexture)_copy;
        this.getAnchorPoint().setLocation(copy.anchorPoint); 
        this.text = copy.text;
        this.tag = copy.tag;
        this.isTextLayoutVisible =copy.isTextLayoutVisible;
        this.style=copy.style;
        this.alignment=copy.alignment;
        this.size=copy.size;
        this.fillColor=new Color(_copy.getFillColor().getRGB());
        this.selectionRectWidth=copy.selectionRectWidth;           
        compositeTextMetrics.updateMetrics();
    }
    
    public void setSelectionRectWidth(int selectionRectWidth) {
        this.selectionRectWidth = selectionRectWidth;
    }
    public FontTexture(String tag, String text, Point point,int fontSize) {
        this(tag, text, point.x, point.y, Text.Alignment.LEFT,fontSize);
    }

    public int getLayermaskId(){
        return layermaskId;
    }

    public void setLayermaskId(int layermaskId){
        this.layermaskId=layermaskId;
    }

    
    @Override
    public Color getFillColor(){
        return fillColor;
    }
    @Override
    public void setFillColor(Color color){
        this.fillColor=color;
    } 
    public void setOrientation(Text.Orientation orientation) {
        switch (orientation) {
        case HORIZONTAL:
            if (alignment == Text.Alignment.BOTTOM)
                alignment = Text.Alignment.LEFT;
            else
                alignment = Text.Alignment.RIGHT;
            break;
        case VERTICAL:
            if (alignment == Text.Alignment.RIGHT)
                alignment = Text.Alignment.TOP;
            else
                alignment = Text.Alignment.BOTTOM;
            break;
        }

        compositeTextMetrics.updateMetrics();
    }
    
    public void setAlignment(Text.Alignment alignment) {
        if (this.alignment == alignment) {
            return;
        }
            if (alignment == Text.Alignment.LEFT)
                anchorPoint.setLocation(anchorPoint.x - compositeTextMetrics.getBaseTextMetrics().getWidth(),
                                        anchorPoint.y);
            else if (alignment == Text.Alignment.RIGHT)
                anchorPoint.setLocation(anchorPoint.x + compositeTextMetrics.getBaseTextMetrics().getWidth(),
                                        anchorPoint.y);
            else if (alignment == Text.Alignment.TOP)
                anchorPoint.setLocation(anchorPoint.x,
                                        anchorPoint.y - compositeTextMetrics.getBaseTextMetrics().getWidth());
            else
                anchorPoint.setLocation(anchorPoint.x,
                                        anchorPoint.y + compositeTextMetrics.getBaseTextMetrics().getWidth());
        this.alignment = alignment;
        compositeTextMetrics.updateMetrics();
    }


    public Text.Alignment getAlignment() {
        return this.alignment;
    }

    public Point getAnchorPoint() {
        return anchorPoint;
    }
    
   
    public void setSize(int size){
     this.size=size;  
    }

    public int getSize(){
      return this.size;  
    }
    
    public void Move(int xOffset, int yOffset) {
        anchorPoint.setLocation(anchorPoint.x + xOffset,
                                anchorPoint.y + yOffset);
    }

    public int getID() {
        return id;
    }

    public void setID(int id) {
        this.id = id;
    } 
    
    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
    
    public void Paint(Graphics2D g2, ViewportWindow viewportWindow, AffineTransform scale,int layermask) {
        if(this.isEmpty()){
           return; 
        }
        if(size*scale.getScaleX()<7){
           return;
        }
        if (this.isSelected())
            g2.setColor(Color.GRAY);
        else
            g2.setColor(fillColor);
        
        //change on zoom out/in   
        Font font=new Font(Text.FONT_NAME,style.ordinal(),(int)Math.round(size* scale.getScaleX()));
        g2.setFont(font);
        
        //***INITIALIZE UNSCALED METRICS.do this only once after load.

        compositeTextMetrics.getBaseTextMetrics().calculateMetrics(null,alignment,style,size,text);
        compositeTextMetrics.getScaledTextMetrics().calculateMetrics(g2,alignment,style,font.getSize(),text);
        

        Point2D A = new Point2D.Double();
        scale.transform(anchorPoint, A);
        A.setLocation(A.getX()-viewportWindow.x, A.getY()-viewportWindow.y);
        
        switch (this.alignment) {
        case RIGHT:
            {
                
                AffineTransform saved=g2.getTransform();
                TextLayout layout =
                    new TextLayout(text, g2.getFont(), g2.getFontRenderContext());
                layout.draw(g2, (int)A.getX() - compositeTextMetrics.getScaledTextMetrics().getWidth(),
                            (int)A.getY());
                if (isTextLayoutVisible) {
                    //***draw bounding rect
                    int rectX = (int)A.getX() - compositeTextMetrics.getScaledTextMetrics().getWidth();
                    int rectY = (int)A.getY() - compositeTextMetrics.getScaledTextMetrics().getAscent();
                    g2.setColor(Color.BLUE);
                    g2.drawRect(rectX, rectY, compositeTextMetrics.getScaledTextMetrics().getWidth(),
                                compositeTextMetrics.getScaledTextMetrics().getHeight());

                    //***draw alignment line
                    g2.setColor(Color.LIGHT_GRAY);
                    g2.drawLine(rectX + compositeTextMetrics.getScaledTextMetrics().getWidth(), rectY,
                                rectX + compositeTextMetrics.getScaledTextMetrics().getWidth(),
                                rectY + compositeTextMetrics.getScaledTextMetrics().getHeight());
                }
                g2.setTransform(saved);
                break;
            }
        case LEFT:
            {
                
                AffineTransform saved=g2.getTransform();
                TextLayout layout =
                    new TextLayout(text, g2.getFont(), g2.getFontRenderContext());
                layout.draw(g2, (int)A.getX(), (int)A.getY());
                
                if (isTextLayoutVisible) {
                    //***draw bounding rect
                    int rectX = (int)A.getX();
                    int rectY = (int)A.getY() - compositeTextMetrics.getScaledTextMetrics().getAscent();
                    g2.setColor(Color.BLUE);
                    g2.drawRect(rectX, rectY, compositeTextMetrics.getScaledTextMetrics().getWidth(),
                                compositeTextMetrics.getScaledTextMetrics().getHeight());
                    //***draw alignment line
                    g2.setColor(Color.LIGHT_GRAY);
                    g2.drawLine(rectX, rectY, rectX,
                                rectY + compositeTextMetrics.getScaledTextMetrics().getHeight());
                }
                g2.setTransform(saved);
                break;
            }
        case TOP:
            {
                AffineTransform saved = g2.getTransform();
                AffineTransform rotate =
                    AffineTransform.getRotateInstance(-Math.PI / 2,
                                                      A.getX(),
                                                      A.getY());
                g2.transform(rotate);
                TextLayout layout =
                    new TextLayout(text, g2.getFont(), g2.getFontRenderContext());
                layout.draw(g2, (int)A.getX() - compositeTextMetrics.getScaledTextMetrics().getWidth(),
                            (int)A.getY());
                //***draw bounding rect
                if (isTextLayoutVisible) {
                    g2.setColor(Color.BLUE);
                    g2.drawRect((int)(A.getX() - compositeTextMetrics.getScaledTextMetrics().getWidth()),
                                (int)(A.getY() - compositeTextMetrics.getScaledTextMetrics().getAscent()),
                                compositeTextMetrics.getScaledTextMetrics().getWidth(), compositeTextMetrics.getScaledTextMetrics().getHeight());
                    //***draw alignment line
                    g2.setColor(Color.LIGHT_GRAY);
                    g2.drawLine((int)A.getX(),
                                (int)(A.getY() - compositeTextMetrics.getScaledTextMetrics().getAscent()),
                                (int)A.getX(),
                                (int)(A.getY() - compositeTextMetrics.getScaledTextMetrics().getAscent() +
                                      compositeTextMetrics.getScaledTextMetrics().getHeight()));
                }
                g2.setTransform(saved);
                break;
            }
        case BOTTOM:
            {
                AffineTransform saved = g2.getTransform();
                AffineTransform rotate =
                    AffineTransform.getRotateInstance(-Math.PI / 2,
                                                      A.getX(),
                                                      A.getY());
                g2.transform(rotate);                
                TextLayout layout =
                    new TextLayout(text, g2.getFont(), g2.getFontRenderContext());
                layout.draw(g2, (int)A.getX(), (int)A.getY());
                //***draw bounding rect
                if (isTextLayoutVisible) {
                    g2.setColor(Color.BLUE);
                    g2.drawRect((int)(A.getX()),
                                (int)(A.getY() - compositeTextMetrics.getScaledTextMetrics().getAscent()),
                                compositeTextMetrics.getScaledTextMetrics().getWidth(), compositeTextMetrics.getScaledTextMetrics().getHeight());

                    //***draw alignment line
                    g2.setColor(Color.LIGHT_GRAY);
                    g2.drawLine((int)A.getX(),
                                (int)(A.getY() - compositeTextMetrics.getScaledTextMetrics().getAscent()),
                                (int)A.getX(),
                                (int)(A.getY() - compositeTextMetrics.getScaledTextMetrics().getAscent() +
                                      compositeTextMetrics.getScaledTextMetrics().getHeight()));
                }
                g2.setTransform(saved);
                break;
            }
        default:
            throw new IllegalArgumentException("Wrong alignment.");
        }

        if (this.isSelected()){
            this.drawControlShape(g2,viewportWindow,scale);
        }
    }
    
    public void Print(Graphics2D g2,PrintContext printContext,int layermask){
        if(this.isEmpty()){
           return; 
        }                        
        g2.setColor(fillColor);
        
        //change on zoom out/in   
        Font font=new Font(Text.FONT_NAME,style.ordinal(),size);
        g2.setFont(font);
        
        //***INITIALIZE UNSCALED METRICS.do this only once after load.

        compositeTextMetrics.getBaseTextMetrics().calculateMetrics(null,alignment,style,size,text);
        
        switch (this.alignment) {
        case RIGHT:
            {
                
                AffineTransform saved=g2.getTransform();
                TextLayout layout =
                    new TextLayout(text, g2.getFont(), g2.getFontRenderContext());
                layout.draw(g2, anchorPoint.x - compositeTextMetrics.getBaseTextMetrics().getWidth(),
                            anchorPoint.y);

                g2.setTransform(saved);
                break;
            }
        case LEFT:
            {
                
                AffineTransform saved=g2.getTransform();
                TextLayout layout =
                    new TextLayout(text, g2.getFont(), g2.getFontRenderContext());
                layout.draw(g2, anchorPoint.x, anchorPoint.y);
                
                g2.setTransform(saved);
                break;
            }
        case TOP:
            {
                AffineTransform saved = g2.getTransform();
                AffineTransform rotate =
                    AffineTransform.getRotateInstance(-Math.PI / 2,
                                                     anchorPoint.x,
                                                    anchorPoint.y);
                g2.transform(rotate);
                TextLayout layout =
                    new TextLayout(text, g2.getFont(), g2.getFontRenderContext());
                layout.draw(g2, anchorPoint.x - compositeTextMetrics.getBaseTextMetrics().getWidth(),
                            anchorPoint.y);

                g2.setTransform(saved);
                break;
            }
        case BOTTOM:
            {
                AffineTransform saved = g2.getTransform();
                AffineTransform rotate =
                    AffineTransform.getRotateInstance(-Math.PI / 2,
                                                    anchorPoint.x,
                                                    anchorPoint.y);
                g2.transform(rotate);
                
                TextLayout layout =
                    new TextLayout(text, g2.getFont(), g2.getFontRenderContext());
                layout.draw(g2, anchorPoint.x, anchorPoint.y);
                g2.setTransform(saved);
                break;
            }
        default:
            throw new IllegalArgumentException("Wrong alignment.");
        }
    }

    private void drawControlShape(Graphics2D g2, ViewportWindow viewportWindow, AffineTransform scale){
        Utilities.drawCrosshair(g2, viewportWindow, scale, null, selectionRectWidth, anchorPoint);
    }
    public void Mirror(Point A,Point B) {
        Text.Alignment oldAlignment = this.alignment;
        Utilities.mirrorPoint(A,B, anchorPoint);
        if (A.x ==
            B.x) { //right-left mirroring
            this.alignment = this.alignment.Mirror(true);
            if (this.alignment == oldAlignment) {
                anchorPoint.setLocation(anchorPoint.x +
                                        (compositeTextMetrics.getBaseTextMetrics().getAscent() - compositeTextMetrics.getBaseTextMetrics().getDescent()),
                                        anchorPoint.y);
            }
        } else { //***top-botom mirroring
            this.alignment = this.alignment.Mirror(false);
            if (this.alignment == oldAlignment) {
                anchorPoint.setLocation(anchorPoint.x,
                                        anchorPoint.y +
                                        (compositeTextMetrics.getBaseTextMetrics().getAscent() - compositeTextMetrics.getBaseTextMetrics().getDescent()));
            }
        }
        
        compositeTextMetrics.updateMetrics();

    }
    
    /*
     * save text origine point horizontal->left-bottom; vertical->right-bottom
     */

    public String toXML() {
        return (text.equals("") ? "" :
                text + "," + anchorPoint.x + "," + anchorPoint.y +
                "," + this.alignment+","+this.getStyle()+","+this.size);
    }

    public void fromXML(Node node) {
        if (node == null || node.getTextContent().length()==0) {
            this.text = "";
            return;
        }
        StringTokenizer st=new StringTokenizer(node.getTextContent(),",");  
        this.setText(st.nextToken());
        anchorPoint.setLocation(Integer.parseInt(st.nextToken()),
                                Integer.parseInt(st.nextToken()));        
        this.alignment = Text.Alignment.valueOf(st.nextToken().toUpperCase());
        compositeTextMetrics.updateMetrics();
        try{
          this.setStyle(Text.Style.valueOf(st.nextToken().toUpperCase()));
        }catch(NoSuchElementException e){
            /*
             * Font Style was introduced in version 5.5
             */
            }
        try{
         this.setSize(Integer.parseInt(st.nextToken()));
        }catch(NoSuchElementException e){
            //old symbol label has constant font size of 8
            this.setSize(8);
            }
    }

    public void Translate(AffineTransform translate) {
        translate.transform(anchorPoint, anchorPoint);
    }

    public void Rotate(AffineTransform rotation) {
        Text.Alignment oldAlignment = this.alignment;
        rotation.transform(anchorPoint, anchorPoint);
        this.alignment =
                this.alignment.Rotate(rotation.getShearY() > 0 ? true :
                                         false);
        //***compencate the baseline!
        if (rotation.getShearY() > 0) { //***clockwise
            //fix 2,4 kvadrant
            if (oldAlignment.getOrientation() == Text.Orientation.HORIZONTAL) {
                anchorPoint.setLocation(anchorPoint.x +
                                        (compositeTextMetrics.getBaseTextMetrics().getAscent() - compositeTextMetrics.getBaseTextMetrics().getDescent()),
                                        anchorPoint.y);
            }
        } else {
            //fix 2,4 kvadrant
            if (oldAlignment.getOrientation() == Text.Orientation.VERTICAL) {
                anchorPoint.setLocation(anchorPoint.x,
                                        anchorPoint.y +
                                        (compositeTextMetrics.getBaseTextMetrics().getAscent() - compositeTextMetrics.getBaseTextMetrics().getDescent()));
            }
        }
        compositeTextMetrics.updateMetrics();
    }
    
    @Override
    public String getText() {
        return text;
    }


    public void setLocation(int x, int y) {
        anchorPoint.setLocation(x, y);
    }

    public void Clear() {

    }

    public boolean isInRect(Rectangle r) {
        if ((getBoundingShape() != null) && (r.contains(getBoundingShape())))
            return true;
        else
            return false;
    }
    
    public boolean isClicked(int x, int y) {
        Rectangle r=getBoundingShape();
        if ((r != null) && (r.contains(x, y)))
            return true;
        else
            return false;
    }

    public Rectangle getBoundingShape() {
        //****no text
        if (text == null || text.length() == 0){
            return null;
        }  
        Rectangle r = new Rectangle();  
        /*
         * getBoundingRect() is called before any drawing(no points calculated yet)
         */
        compositeTextMetrics.getBaseTextMetrics().calculateMetrics(null, alignment, style, size, text);
        //metrics.calculateMetrics(text);

        //***WIDTH and HEIGHT swaps places!!!!!
        switch (this.alignment) {
        case LEFT:
            {
                int rectX = anchorPoint.x;
                int rectY = anchorPoint.y - compositeTextMetrics.getBaseTextMetrics().getAscent();
                r.setRect(rectX, rectY, compositeTextMetrics.getBaseTextMetrics().getWidth(),
                          compositeTextMetrics.getBaseTextMetrics().getHeight());
            }
            break;
        case RIGHT:
            {
                int rectX = anchorPoint.x - compositeTextMetrics.getBaseTextMetrics().getWidth();
                int rectY = anchorPoint.y - compositeTextMetrics.getBaseTextMetrics().getAscent();
                r.setRect(rectX, rectY, compositeTextMetrics.getBaseTextMetrics().getWidth(),
                          compositeTextMetrics.getBaseTextMetrics().getHeight());
            }
            break;
        case TOP:
            r.setRect((anchorPoint.x - compositeTextMetrics.getBaseTextMetrics().getAscent()),
                      (anchorPoint.y), compositeTextMetrics.getBaseTextMetrics().getHeight(),
                      compositeTextMetrics.getBaseTextMetrics().getWidth());
            break;
        case BOTTOM:

            r.setRect((anchorPoint.x - compositeTextMetrics.getBaseTextMetrics().getAscent()),
                      (anchorPoint.y - compositeTextMetrics.getBaseTextMetrics().getWidth()),
                      compositeTextMetrics.getBaseTextMetrics().getHeight(), compositeTextMetrics.getBaseTextMetrics().getWidth());
        }

        if ((r.getWidth() == 0) && (r.getHeight() == 0))
            return null;
        else
            return r;
    }

    @Override
    public void setText(String text) {
        this.text = text;
        compositeTextMetrics.updateMetrics();
    }

    public boolean isTextLayoutVisible() {
        return this.isTextLayoutVisible;
    }

    public void setTextLayoutVisible(boolean visible) {
        this.isTextLayoutVisible = visible;
    }



    public long getOrderWeight() {
        return 0;
    }

   
    public boolean isEmpty() {
        return text==null||text.length()==0;
    }


    public void setStyle(Text.Style style) {
      this.style=style;
      compositeTextMetrics.updateMetrics(); 
    }

    public Text.Style getStyle() {
        return style;
    }

    @Override
    public void setSelected(boolean isSelected) {
        this.isSelected=isSelected;
    }

    @Override
    public boolean isSelected() {
        return isSelected;
    }

    @Override
    public Texture.Memento createMemento() {        
        return new Memento();
    }

    public static class Memento  implements Texture.Memento{
         private String text;

         private int Ax;

         private int Ay;

         private String tag;

         private Text.Alignment alignment;

         private int id,layermaskId;
         
         private int size;
         
         private int sRGB;
         
         private Text.Style style;

         public void loadStateTo(Texture _symbol) {
             FontTexture symbol=(FontTexture)_symbol;
             symbol.id=this.id;
             symbol.layermaskId=this.layermaskId;
             symbol.size=this.size;
             symbol.anchorPoint.setLocation(Ax, Ay);
             symbol.tag = this.tag;
             symbol.text = this.text;
             symbol.alignment = alignment;
             symbol.setStyle(this.style);
             symbol.fillColor=new Color(sRGB);
             symbol.compositeTextMetrics.updateMetrics();
         }
        @Override
         public void saveStateFrom(Texture _symbol) {
             FontTexture symbol=(FontTexture)_symbol;
             this.id=symbol.id;
             this.layermaskId=symbol.layermaskId;
             Ax = symbol.anchorPoint.x;
             Ay = symbol.anchorPoint.y;
             this.tag = symbol.tag;
             this.text = symbol.text;
             this.size=symbol.size;
             this.alignment = symbol.alignment;
             this.style=symbol.style;
             this.sRGB=symbol.fillColor.getRGB();
             
         }
         
         @Override
         public int getID() {
             return id;
         }

         @Override
         public boolean equals(Object obj) {
             if (this == obj) {
                 return true;
             }
             if (!(obj instanceof FontTexture.Memento)) {
                 return false;
             }
             FontTexture.Memento other = (FontTexture.Memento)obj;
             return (other.id==this.id&&
                     other.layermaskId==this.layermaskId&&
                     other.alignment == this.alignment &&
                     other.tag.equals(this.tag) &&
                     other.text.equals(this.text) &&
                     other.Ax==this.Ax  &&
                     other.Ay==this.Ay &&
                     other.size==this.size &&
                     other.style==this.style)&&
                     other.sRGB==this.sRGB;
         }

         @Override
         public String toString() {
             StringBuilder sb = new StringBuilder();
             sb.append(text+"("+Ax + "," + Ay+")");
             return sb.toString();
         }

         @Override
         public int hashCode() {
             int hash = 1;
             hash =
             hash * 31 +this.id+ this.layermaskId+this.alignment.hashCode() + this.size+
             this.tag.hashCode() + this.text.hashCode() + Ax +Ay+this.style.hashCode()+sRGB;
             return hash;
         }
    }
}


