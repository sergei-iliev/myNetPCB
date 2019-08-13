package com.mynetpcb.core.capi.text.glyph;

import com.mynetpcb.core.capi.Grid;
import com.mynetpcb.core.capi.ViewportWindow;
import com.mynetpcb.core.capi.flyweight.FlyweightProvider;
import com.mynetpcb.core.capi.flyweight.ShapeFlyweightFactory;
import com.mynetpcb.core.capi.line.Trackable;
import com.mynetpcb.core.capi.print.PrintContext;
import com.mynetpcb.core.capi.text.Text;
import com.mynetpcb.core.capi.text.Texture;
import com.mynetpcb.core.pad.Layer;

import com.mynetpcb.core.utils.Utilities;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;

import java.awt.Rectangle;
import java.awt.geom.AffineTransform;

import java.awt.geom.GeneralPath;

import java.util.ArrayList;
import java.util.List;

import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class GlyphTextureExt implements Texture{
    private String text,tag;

    private int id,layermaskId;
    
    private Point anchorPoint; //***baseline!

    private List<Glyph> glyphs;

    private Text.Alignment alignment;

    private int width,height;

    private int size;
    
    private int thickness;

    private Color fillColor;

    private int selectionRectWidth;
    
    private boolean isSelected,mirrored;
    
    public GlyphTextureExt(String text,String tag, int x, int y, int size) {
        this.anchorPoint = new Point(x, y);
        this.glyphs = new ArrayList<>();
        this.thickness = Grid.MM_TO_COORD(0.2);
        this.alignment = Text.Alignment.LEFT;
        this.selectionRectWidth=4000;        
        this.text = this.resetGlyphText(text);
        this.tag=tag;
        this.size=size;
        this.layermaskId=Layer.SILKSCREEN_LAYER_FRONT;
        this.mirrored=false;
    }
    public void copy(Texture _copy){
        GlyphTextureExt copy=(GlyphTextureExt)_copy;
        this.anchorPoint.setLocation(copy.anchorPoint.x,copy.anchorPoint.y); 
        this.text = copy.text;
        this.tag = copy.tag;   
        this.fillColor=copy.fillColor;
        this.alignment=copy.alignment;
        this.thickness=copy.thickness;
        setSize(copy.size);                
    }
    
    public GlyphTextureExt clone() throws CloneNotSupportedException {
        GlyphTextureExt copy = (GlyphTextureExt) super.clone();
        copy.anchorPoint = (Point) anchorPoint.clone();
        copy.glyphs = new ArrayList<>(text.length());
        for (Glyph glyph : this.glyphs) {
            copy.glyphs.add(glyph.clone());
        }
        return copy;
    }
    @Override
    public void Clear() {
        glyphs.clear();
        this.width=0;
        this.height=0;
    }
    public boolean isEmpty() {
        return text == null || text.length() == 0;
    }    
    private String resetGlyphText(String text) {
        Clear();
        if (text == null) {
            return null;
        }
        StringBuilder result = new StringBuilder();

        for (Character c : text.toCharArray()) {
            Glyph glyph = GlyphManager.INSTANCE.getGlyph(c);
            if (glyph != null) {
                glyphs.add(glyph);
                result.append(c);
            } else {
                glyphs.add(GlyphManager.INSTANCE.getGlyph('!'));
                result.append('!');
            }
        }

        return result.toString();
    }
    public void setSize(int size) {

        this.size=size;
        
        if(this.mirrored){           
           this.mirror(true,this.anchorPoint,new Point(this.anchorPoint.x,this.anchorPoint.y+100));
        }else{
           this.reset();
        }
        
    }   

    private void resetGlyphsLine(){
        int xoffset = 0,yoffset=0;
        for(Glyph glyph:this.glyphs){
            if(glyph.getChar()==' '){
                xoffset += glyph.getDelta();
                this.width += glyph.getDelta();
                continue;
            }
        //calculate its width
            glyph.resize();
            yoffset=glyph.getHeight();
            int j=0;
            for (int i = 0; i < glyph.getLinesNumber(); i++, j = (j + 2)) {                
                glyph.points[j].x += anchorPoint.x + xoffset;
                glyph.points[j].y +=anchorPoint.y;                

                glyph.points[j + 1].x += anchorPoint.x + xoffset;
                glyph.points[j + 1].y +=anchorPoint.y;                
            }
            
            
                
         xoffset += glyph.getWidth() + glyph.getDelta();
         this.height = Math.max(glyph.getHeight()+ glyph.miny, this.height);
         this.width += glyph.getWidth() + glyph.getDelta();
        };
        
        for(Glyph glyph:this.glyphs){

          int j=0;
          for (int i = 0; i < glyph.getLinesNumber(); i++, j = (j + 2)) {                              
              glyph.points[j].x +=0;
              glyph.points[j].y +=-this.height;                

              glyph.points[j + 1].x += 0;
              glyph.points[j + 1].y +=-this.height; 
          }            
        }        
        
    }
    private void reset(){
        if (this.text == null) {
            return;
        }
        //reset original text
        this.text = this.resetGlyphText(this.text);
        //reset size
        this.glyphs.forEach(glyph->{
            glyph.setSize(Grid.COORD_TO_MM(this.size));
        });        
        
        //arrange it according to anchor point
        this.resetGlyphsLine();
        //rotate
        //this.glyphs.forEach(function(glyph){              
        //            glyph.rotate(this.rotate,this.anchorPoint);                  
        //}.bind(this));
    }
    public void mirror(boolean mirrored,Point A,Point B){
        this.mirrored=mirrored;
            
        //reset original text
        this.text = this.resetGlyphText(this.text);
        
        //reset size
        this.glyphs.forEach(glyph->{
            glyph.setSize(Grid.COORD_TO_MM(this.size));
        });         
        
        //arrange it according to anchor point
        this.resetGlyphsLine();
        
        Utilities.mirrorPoint(A,B, anchorPoint);
        AffineTransform rotation = AffineTransform.getRotateInstance(-Math.PI / 2, 0, 0);
        this.glyphs.forEach(glyph->{
          if(this.mirrored){
            glyph.mirror(A,B);             
          } 
                    
          glyph.rotate(rotation);
                                    
        });
            
    }   
    private Rectangle getBoundingRect(){
        if(this.mirrored){
            Rectangle rect= new Rectangle(this.anchorPoint.x-this.width,this.anchorPoint.y-this.height,this.width,this.height);
            //rect.rotate(this.rotate,this.anchorPoint);
            return rect;
         }else{     
            Rectangle rect= new Rectangle(this.anchorPoint.x,this.anchorPoint.y-this.height,this.width,this.height);
            //rect.rotate(this.rotate,this.anchorPoint);
            return rect;
         }  
    }
    public Rectangle getBoundingShape() {
        if (this.text == null || this.text.length() == 0) {
              return null;
        }
        
         return this.getBoundingRect();
    }   
    @Override
    public void Move(int xoffset,int yoffset) {
        anchorPoint.setLocation(anchorPoint.x + xoffset, anchorPoint.y + yoffset);
        this.glyphs.forEach(glyph->{
          glyph.move(xoffset,yoffset);
        });      
    }    
    
    @Override
    public void setFillColor(Color fillColor) {
        this.fillColor = fillColor;
    }

    @Override
    public Text.Alignment getAlignment() {
        // TODO Implement this method
        return null;
    }

    @Override
    public void setAlignment(Text.Alignment alignment) {
        // TODO Implement this method
    }

    @Override
    public void setOrientation(Text.Orientation orientation) {
        // TODO Implement this method
    }

    @Override
    public Point getAnchorPoint() {        
        return this.anchorPoint;
    }

    @Override
    public String getTag() {        
        return this.tag;
    }

    @Override
    public void setTag(String tag) {
        this.tag=tag;
    }

    @Override
    public int getID() {        
        return this.id;
    }

    @Override
    public void setID(int id) {
        this.id=id;
    }
    public void setThickness(int thickness) {
        this.thickness = thickness;
    }

    public int getThickness() {
        return thickness;
    }
    @Override
    public String getText() {        
        return this.text;
    }

    @Override
    public void setText(String text) {
            //read original text
            this.text = text;
            if(this.mirrored){              
              this.mirror(true,this.anchorPoint,new Point(this.anchorPoint.x,this.anchorPoint.y+100));
            }else{
              this.reset();
            }        
    }

    @Override
    public void Rotate(AffineTransform rotation) {
        // TODO Implement this methodni
    }

    @Override
    public void Mirror(Point A, Point B) {
        
    }

    @Override
    public void Translate(AffineTransform transform) {
        // TODO Implement this method
    }

    @Override
    public int getSize() {        
        return this.size;
    }


    @Override
    public void fromXML(Node node) {
        if (node == null || node.getTextContent().length()==0) {
            this.text = "";
            return;
        }
        Element  element= (Element)node;
        if(element.getAttribute("layer")!=null&&!element.getAttribute("layer").isEmpty()){
           this.layermaskId =Layer.Copper.valueOf(element.getAttribute("layer")).getLayerMaskID();
        }else{
            this.layermaskId=Layer.Copper.FSilkS.getLayerMaskID();
        }
        StringTokenizer st=new StringTokenizer(node.getTextContent(),",");  
        this.text=st.nextToken();
        anchorPoint.setLocation(Integer.parseInt(st.nextToken()),
                                Integer.parseInt(st.nextToken()));        
        //this.alignment = Text.Alignment.valueOf(st.nextToken().toUpperCase());                
        try{
          this.thickness=Integer.parseInt(st.nextToken());        
        }catch(Exception e){
            this.thickness=2000;
        }
        int size=20000;
        try{
           size=(Integer.parseInt(st.nextToken()));
        }catch(NoSuchElementException e){        
            
        }
        //invalidate
        this.setText(this.text);
    }

    @Override
    public String toXML() {
        // TODO Implement this method
        return null;
    }

    @Override
    public int getLayermaskId() {
        return this.layermaskId;
    }

    @Override
    public void setLayermaskId(int layermaskId) {
        this.layermaskId=layermaskId;
    }


    @Override
    public boolean isClicked(int x, int y) {
        Rectangle r=getBoundingShape();
        if ((r != null) && (r.contains(x, y)))
            return true;
        else
            return false;
    
    }

    @Override
    public boolean isInRect(Rectangle r) {
        return false;
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
    public Color getFillColor() {
        // TODO Implement this method
        return null;
    }
    @Override
    public void Paint(Graphics2D g2, ViewportWindow viewportWindow, AffineTransform scale, int layermask) {
        if (this.isEmpty()) {
             return;
        }
 
        
        if (this.isSelected)
            g2.setColor(Color.GRAY);
        else
            g2.setColor(fillColor);

        double lineThickness = thickness * scale.getScaleX();
        g2.setStroke(new BasicStroke((float) lineThickness, Trackable.JoinType
                                                                     .JOIN_ROUND
                                                                     .ordinal(), Trackable.EndType
                                                                                          .CAP_ROUND
                                                                                          .ordinal()));

        FlyweightProvider provider = ShapeFlyweightFactory.getProvider(GeneralPath.class);
        GeneralPath temporal = (GeneralPath) provider.getShape();

        
        this.glyphs.forEach(glyph->{
                    int j = 0;
                    for (int i = 0; i < glyph.getLinesNumber(); i++, j = (j + 2)) {    
                        if(glyph.getChar()==' '){
                                continue;
                        }
                        
                        Point cA=new Point(glyph.points[j]);
                        Point cB=new Point(glyph.points[j+1]);
                       
                        temporal.moveTo(glyph.points[j].x ,
                                    glyph.points[j].y);
                        temporal.lineTo(glyph.points[j + 1].x ,
                                    glyph.points[j + 1].y ); 
 
                }
        });
        AffineTransform translate = AffineTransform.getTranslateInstance(-viewportWindow.x, -viewportWindow.y);
        
        temporal.transform(scale);
        temporal.transform(translate);
        g2.draw(temporal);
        
        provider.reset();
        
    }
    @Override
    public void Print(Graphics2D g2, PrintContext printContext, int layermask) {
        // TODO Implement this method

    }
    @Override
    public Texture.Memento createMemento() {
        return new Memento();
    }

    public static class Memento implements Texture.Memento{
        private String text;

        private int Ax;

        private int Ay;

        private int id;
        
        private List<Glyph> glyphs;

        private Text.Alignment alignment;

        private int size,width,height;

        private int thickness;
        @Override
        public void loadStateTo(Texture _symbol) {
            GlyphTextureExt symbol=(GlyphTextureExt)_symbol;
            symbol.anchorPoint.setLocation(Ax, Ay);
            symbol.id=this.id;
            symbol.text = this.text;
            symbol.alignment = this.alignment;
            symbol.size = this.size;
            symbol.width=this.width;
            symbol.height=this.height;
            symbol.thickness = this.thickness;
            symbol.glyphs.clear();
            for (Glyph glyph : this.glyphs) {
                try {
                    symbol.glyphs.add(glyph.clone());
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace(System.out);
                }
            }
        }
        @Override
        public void saveStateFrom(Texture _symbol) {
            GlyphTextureExt symbol=(GlyphTextureExt)_symbol;
            Ax = symbol.anchorPoint.x;
            Ay = symbol.anchorPoint.y;
            this.id=symbol.id;
            this.text = symbol.text;
            this.alignment = symbol.alignment;
            this.size = symbol.size;
            this.thickness = symbol.thickness;
            this.width=symbol.width;
            this.height=symbol.height;
            this.glyphs = new ArrayList<>(symbol.glyphs.size());
            for (Glyph glyph : symbol.glyphs) {
                try {
                    this.glyphs.add(glyph.clone());
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace(System.out);
                }
            }
        }
        
        @Override
        public int getID() {            
            return id;
        }
        
        public void Clear() {
            this.glyphs.clear();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof GlyphTexture.Memento)) {
                return false;
            }
            GlyphTextureExt.Memento other = (GlyphTextureExt.Memento) obj;
            return (other.id==this.id&&other.size == this.size &&other.width == this.width&&other.height == this.height&& other.alignment == this.alignment &&
                    other.thickness == this.thickness && other.text.equals(this.text) && other.Ax == this.Ax &&
                    other.Ay == this.Ay) && other.glyphs.equals(this.glyphs);
        }

        @Override
        public int hashCode() {
            int hash =
                31 +this.id+ this.size +this.width+this.height+ this.alignment.hashCode() + this.thickness + this.text.hashCode() + Ax + Ay +
                this.glyphs.hashCode();

            return hash;
        }
    }
    
}
