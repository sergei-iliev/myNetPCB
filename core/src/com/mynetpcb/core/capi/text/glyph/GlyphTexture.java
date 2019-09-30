package com.mynetpcb.core.capi.text.glyph;

import com.mynetpcb.core.capi.Grid;
import com.mynetpcb.core.capi.ViewportWindow;
import com.mynetpcb.core.capi.flyweight.FlyweightProvider;
import com.mynetpcb.core.capi.flyweight.ShapeFlyweightFactory;
import com.mynetpcb.core.capi.layer.Layer;
import com.mynetpcb.core.capi.line.Trackable;
import com.mynetpcb.core.capi.print.PrintContext;
import com.mynetpcb.core.capi.text.Text;
import com.mynetpcb.core.capi.text.Texture;
import com.mynetpcb.core.utils.Utilities;

import com.mynetpcb.d2.shapes.Box;

import com.mynetpcb.d2.shapes.Line;
import com.mynetpcb.d2.shapes.Point;

import com.mynetpcb.d2.shapes.Rectangle;

import com.mynetpcb.d2.shapes.Segment;

import com.mynetpcb.d2.shapes.Utils;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;

import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;


import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class GlyphTexture implements Texture {

    private String text,tag;

    private int id,layermaskId;
    
    private Point anchorPoint;

    private List<Glyph> glyphs;

    private int width,height;

    private int size;
    
    private int thickness;

    private Color fillColor;

    private int selectionRectWidth;
    
    private boolean isSelected;
    
    private double rotate;
    private boolean mirrored;
    
    public GlyphTexture(String text,String tag, int x, int y, int size) {
        this.tag=tag;
        this.id=1;
        this.anchorPoint = new Point(x, y);
        this.glyphs = new ArrayList<>();
        this.thickness = (int)Grid.MM_TO_COORD(0.2);
        
        this.selectionRectWidth=3000;
        this.text = text;
        this.height=0;
        this.width=0;
        
        this.layermaskId=Layer.SILKSCREEN_LAYER_FRONT;
        this.isSelected=false;
        this.rotate=0;
        this.mirrored=false;        
        
        this.setSize(size);
    }
    public void copy(Texture _copy){
        GlyphTexture copy=(GlyphTexture)_copy;
        this.anchorPoint.set(copy.anchorPoint.x,copy.anchorPoint.y); 
        this.text = copy.text;
        this.tag = copy.tag; 
        this.rotate=copy.rotate;
        this.mirrored=copy.mirrored;        
        this.fillColor=copy.fillColor;
        this.thickness=copy.thickness;
        setSize(copy.size);         
    }
    
    public GlyphTexture clone() throws CloneNotSupportedException {
        GlyphTexture copy = (GlyphTexture) super.clone();
        copy.anchorPoint = (Point) anchorPoint.clone();
        copy.glyphs = new ArrayList<>(text.length());
        for (Glyph glyph : this.glyphs) {
            copy.glyphs.add(glyph.clone());
        }
        return copy;
    }

    private String resetGlyphText(String text) {
        clear();
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

    public void clear() {
        glyphs.clear();
        this.width=0;
        this.height=0;
    }

    public boolean isEmpty() {
        return this.text == null || this.text.length() == 0;
    }
    private void resetGlyphsLine(){
        int xoffset = 0,yoffset=0;
        for(Glyph glyph:this.glyphs){
            if(glyph.character==' '){
                xoffset += glyph.delta;
                this.width += glyph.delta;
                return;
            }
            //calculate its width
            glyph.resize();
            yoffset=glyph.getHeight();
            for (int i = 0; i < glyph.segments.length ; i++) {              
                glyph.segments[i].move(this.anchorPoint.x + xoffset,this.anchorPoint.y);                                          
            }        
            xoffset += glyph.getWidth() + glyph.delta;
            this.height = Math.max(glyph.getHeight()+ (int)glyph.miny, this.height);
            this.width += glyph.getWidth() + glyph.delta;
        }
        
        this.glyphs.forEach(glyph-> {
            for (int i = 0; i < glyph.segments.length ; i++) {              
                    glyph.segments[i].move(0,-this.height);                                           
            }        
        });
        
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
        this.glyphs.forEach(glyph->{
            glyph.rotate(this.rotate,this.anchorPoint);                  
        });
    }
    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        
        this.size=size;
        if(this.mirrored){
           Line line=new Line(this.anchorPoint,new Point(this.anchorPoint.x,this.anchorPoint.y+100));
           this.mirror(true,line);
        }else{
           this.reset();
        }        
    }
    
    public void setText(String text) {
        //read original text
        this.text = text;
        if(this.mirrored){
          Line line=new Line(this.anchorPoint,new Point(this.anchorPoint.x,this.anchorPoint.y+100));
          this.mirror(true,line);
        }else{
          this.reset();
        }
        
    }

    public List<Glyph> getGlyphs(){
        return this.glyphs;
    }
    public String getText() {
        return text;
    }

    public long getOrderWeight() {
        return 0;
    }
    public void mirror(boolean mirrored,Line line){
            this.mirrored=mirrored;
            
        //reset original text
        this.text = this.resetGlyphText(this.text);
        //reset size
        this.glyphs.forEach(glyph->{
            glyph.setSize(Grid.COORD_TO_MM(this.size));
        });        
        
        //arrange it according to anchor point
        this.resetGlyphsLine();
        
        this.anchorPoint.mirror(line);
        this.glyphs.forEach(glyph->{
           if(this.mirrored){
            glyph.mirror(line);             
           } 
           glyph.rotate(this.rotate,this.anchorPoint);
            
        });
            
    }

    @Override
    public Box getBoundingShape() {
       if (this.text == null || this.text.length() == 0) {
         return null;
       }
         return this.getBoundingRect().box();
    }
    
    public Rectangle getBoundingRect(){
        if(this.mirrored){
            Rectangle rect= new Rectangle(this.anchorPoint.x-this.width,this.anchorPoint.y-this.height,this.width,this.height);
            rect.rotate(this.rotate,this.anchorPoint);
            return rect;
         }else{     
            Rectangle rect= new Rectangle(this.anchorPoint.x,this.anchorPoint.y-this.height,this.width,this.height);
            rect.rotate(this.rotate,this.anchorPoint);
            return rect;
         }  
    }    
    
    @Override
    public Point getAnchorPoint() {
        return anchorPoint;
    }
    public void setLocation(double x,double y){
            double xx=x-this.anchorPoint.x;
            double yy=y-this.anchorPoint.y;
            this.move(xx,yy);
    }
    @Override
    public void move(double xoffset, double yoffset) {        
            this.anchorPoint.move(xoffset,yoffset);
            this.glyphs.forEach(glyph->{
                glyph.move(xoffset,yoffset);
            });              
    }
    public double getRotation(){
        return this.rotate;
    }
    public void setRotation(double rotate,Point pt){
            double alpha=rotate-this.rotate;
            this.anchorPoint.rotate(alpha,pt);
            this.glyphs.forEach(glyph->{
               glyph.rotate(alpha,pt);   
            });  
            this.rotate=rotate;     
    }
    
    public void rotate(double rotate,Point pt){
            //fix angle
            double alpha=this.rotate+rotate;
            if(alpha>=360){
                    alpha-=360;
            }
            if(alpha<0){
             alpha+=360; 
            }       
            this.rotate=alpha;
            //rotate anchor point
            this.anchorPoint.rotate(rotate,pt);
            //rotate glyphs
            this.glyphs.forEach(glyph->{
               glyph.rotate(rotate,pt);   
            });  
                    
    }
    //@Override
    public void mirror(Point A, Point B) {
//        Utilities.mirrorPoint(A,B, anchorPoint);
//                
//        if (A.x ==B.x) { //right-left mirroring
//            this.alignment = this.alignment.Mirror(true);            
//        } else { //***top-botom mirroring
//            this.alignment = this.alignment.Mirror(false);          
//        }                
    }

    //@Override
    public void translate(AffineTransform translate) {
        // TODO Implement this method
    }

    //@Override
    public void rotate(AffineTransform rotation) {
        
//   
//        rotation.transform(anchorPoint, anchorPoint);
//        this.alignment =
//                this.alignment.Rotate(rotation.getShearY() > 0 ? true :
//                                         false);        
//        int w=this.height;
//        int h=this.width;
//        
//        switch(alignment){
//        case LEFT:
//                //read original text
//                this.text = resetGlyphText(text);
//                        //reset height
//                for (Glyph glyph : glyphs) {
//                     glyph.setSize(Grid.COORD_TO_MM(this.size));                    
//                }             
//              break;
//        case RIGHT:
//            //read original text
//            this.text = resetGlyphText(text);
//            //reset height
//            for (Glyph glyph : glyphs) {
//                 glyph.setSize(Grid.COORD_TO_MM(this.size));
//            }                           
//            break;
//        case BOTTOM:
//            //read original text
//            this.text = resetGlyphText(text);
//            //reset height
//            for (Glyph glyph : glyphs) {
//                 glyph.setSize(Grid.COORD_TO_MM(this.size));
//            } 
//            //rotate
//            AffineTransform rotate = AffineTransform.getRotateInstance(-Math.PI / 2, 0, 0);
//                 for (Glyph glyph : glyphs) {
//                        glyph.Rotate(rotate);
//                 }
//              break;
//        case TOP:
//            //read original text
//            this.text = resetGlyphText(text);
//            //reset height
//            for (Glyph glyph : glyphs) {
//                 glyph.setSize(Grid.COORD_TO_MM(this.size));
//            } 
//            //rotate
//            rotate = AffineTransform.getRotateInstance(-Math.PI / 2, 0, 0);
//            for (Glyph glyph : glyphs) {
//                   glyph.Rotate(rotate);
//            }            
//            break;
//        }
//        this.width=w;
//        this.height=h;
//      
//        //***compencate the baseline!
//        if (rotation.getShearY() > 0) { //***clockwise            
//            if(this.alignment.getOrientation()==Text.Orientation.VERTICAL){               
//               anchorPoint.x+=this.width;
//            }            
//        }else{
//            if(this.alignment.getOrientation()==Text.Orientation.HORIZONTAL){               
//               anchorPoint.y+=this.height;
//            }
//        }

    }


    @Override
    public void paint(Graphics2D g2, ViewportWindow viewportWindow, AffineTransform scale, int layermask) {
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
                for(int i=0;i<glyph.segments.length;i++){    
                     if(glyph.character==' '){
                         continue;
                     }             
                     temporal.moveTo(glyph.segments[i].ps.x,glyph.segments[i].ps.y);
                     temporal.lineTo(glyph.segments[i].pe.x,glyph.segments[i].pe.y);                     
                }
        });
        AffineTransform translate = AffineTransform.getTranslateInstance(-viewportWindow.getX(), -viewportWindow.getY());
       
        temporal.transform(scale);
        temporal.transform(translate);
        g2.draw(temporal);
        
        provider.reset();
        
        if (this.isSelected){
            this.drawControlShape(g2,viewportWindow,scale);
        }  
    }
    public void print(Graphics2D g2,PrintContext printContext,int layermask){
//        if (this.isEmpty()) {
//            return;
//        }
//
//        g2.setColor(fillColor);
//
//        Rectangle r = getBoundingShape();
//        
//        FlyweightProvider provider = ShapeFlyweightFactory.getProvider(GeneralPath.class);
//        GeneralPath temporal = (GeneralPath) provider.getShape();
//
//
//        g2.setStroke(new BasicStroke(thickness, Trackable.JoinType
//                                                                     .JOIN_ROUND
//                                                                     .ordinal(), Trackable.EndType
//                                                                                          .CAP_ROUND
//                                                                                          .ordinal()));
//
//        switch (this.alignment) {
//        case LEFT:
//            int xoffset = 0;
//            for (Glyph glyph : glyphs) {
//                if(glyph.getChar()==' '){
//                    xoffset += glyph.getDelta();
//                    continue;
//                }
//                int j = 0;
//                for (int i = 0; i < glyph.getLinesNumber(); i++, j = (j + 2)) {
//
//                    temporal.moveTo(glyph.points[j].x + anchorPoint.x + xoffset,
//                                    glyph.points[j].y + anchorPoint.y - r.height);
//                    temporal.lineTo(glyph.points[j + 1].x + anchorPoint.x + xoffset,
//                                    glyph.points[j + 1].y + anchorPoint.y - r.height);
//                    
//                }
//                xoffset += glyph.getGlyphWidth() + glyph.getDelta();
//            }    
//            
//             
//             break;
//        case RIGHT:
//            xoffset = 0;
//            for (Glyph glyph : glyphs) {
//                if(glyph.getChar()==' '){
//                    xoffset += glyph.getDelta();
//                    continue;
//                }
//                int j = 0;
//                for (int i = 0; i < glyph.getLinesNumber(); i++, j = (j + 2)) {
//
//                    temporal.moveTo(glyph.points[j].x + anchorPoint.x + xoffset - r.width,
//                                    glyph.points[j].y + anchorPoint.y - r.height);
//                    temporal.lineTo(glyph.points[j + 1].x + anchorPoint.x + xoffset -r.width,
//                                    glyph.points[j + 1].y + anchorPoint.y - r.height);
//                    
//                }
//                xoffset += glyph.getGlyphWidth() + glyph.getDelta();
//            }    
//        
//             break;
//        case BOTTOM:
//            int yoffset = 0;
//            for (Glyph glyph : glyphs) {
//                if(glyph.getChar()==' '){
//                    yoffset += glyph.getDelta();
//                    continue;
//                }
//                int j = 0;
//                for (int i = 0; i < glyph.getLinesNumber(); i++, j = (j + 2)) {
//                  
//                    temporal.moveTo(glyph.points[j].x + anchorPoint.x  - r.width,
//                                    glyph.points[j].y + anchorPoint.y-yoffset);
//                    temporal.lineTo(glyph.points[j + 1].x + anchorPoint.x  - r.width,
//                                    glyph.points[j + 1].y + anchorPoint.y-yoffset);
//                    
//                }
//                yoffset += glyph.getGlyphHeight() + glyph.getDelta();
//            }
//            break;
//        case TOP:
//            yoffset = 0;
//            for (Glyph glyph : glyphs) {
//                if(glyph.getChar()==' '){
//                    yoffset += glyph.getDelta();
//                    continue;
//                }
//
//                int j = 0;
//                for (int i = 0; i < glyph.getLinesNumber(); i++, j = (j + 2)) {
//                    temporal.moveTo(glyph.points[j].x + anchorPoint.x  - r.width,
//                                    glyph.points[j].y + anchorPoint.y-yoffset+r.height);
//                    temporal.lineTo(glyph.points[j + 1].x + anchorPoint.x  - r.width,
//                                    glyph.points[j + 1].y + anchorPoint.y-yoffset+r.height);
//                   }
//                
//                yoffset += glyph.getGlyphHeight() + glyph.getDelta();
//            }            
//            break;
//        }
//        g2.draw(temporal);        
//        provider.reset();        
    } 
    
    private void drawControlShape(Graphics2D g2, ViewportWindow viewportWindow, AffineTransform scale){
        Utilities.drawCrosshair(g2, viewportWindow, scale, null, selectionRectWidth, anchorPoint);
    }
    @Override
    public boolean isClicked(int x, int y) {
        if (this.text == null || this.text.length() == 0){
            return false;
        } 
        return this.getBoundingRect().contains(x,y);   

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
        return fillColor;
    }

    @Override
    public void setFillColor(Color fillColor) {
        this.fillColor = fillColor;
    }

    public void setThickness(int thickness) {
        this.thickness = thickness;
    }

    public int getThickness() {
        return thickness;
    }
    
    
    public String toXML() {
        return (text.equals("") ? "" :
                text + "," + anchorPoint.x + "," + anchorPoint.y +
                ",,"+this.thickness+","+this.size);
    }
    public void fromXML(Node node) {
//        if (node == null || node.getTextContent().length()==0) {
//            this.text = "";
//            return;
//        }
//        Element  element= (Element)node;
//        if(element.getAttribute("layer")!=null&&!element.getAttribute("layer").isEmpty()){
//           this.layermaskId =Layer.Copper.valueOf(element.getAttribute("layer")).getLayerMaskID();
//        }else{
//            this.layermaskId=Layer.Copper.FSilkS.getLayerMaskID();
//        }
//        StringTokenizer st=new StringTokenizer(node.getTextContent(),",");  
//        this.text=st.nextToken();
//        anchorPoint.setLocation(Integer.parseInt(st.nextToken()),
//                                Integer.parseInt(st.nextToken()));        
//        this.alignment = Text.Alignment.valueOf(st.nextToken().toUpperCase());                
//        try{
//          this.thickness=Integer.parseInt(st.nextToken());        
//        }catch(Exception e){
//            this.thickness=2000;
//        }
//        int size=20000;
//        try{
//           size=(Integer.parseInt(st.nextToken()));
//        }catch(NoSuchElementException e){        
//            
//        }
//        //invalidate
//        this.setSize(size);
        
    }
    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    @Override
    public int getID() {        
        return this.id;
    }

    @Override
    public void setID(int id) {
       this.id=id;
    }
    public boolean isTextLayoutVisible() {
        return false;
    }

    public void setTextLayoutVisible(boolean visible) {
        
    }
    
    @Override
    public int getLayermaskId(){
        return layermaskId;
    }
    
    @Override
    public void setLayermaskId(int layermaskId){
        this.layermaskId=layermaskId;
    }
    
    @Override
    public Texture.Memento createMemento() {
        return new Memento();
    }

    public static class Memento implements Texture.Memento{
        private String text;

        private double Ax;

        private double Ay;

        private int id;
        
        private List<Glyph> glyphs;

        private int size,width,height;

        private int thickness;
                
        private double rotate;
        
        private boolean mirrored;
        
        @Override
        public void loadStateTo(Texture _symbol) {
            GlyphTexture symbol=(GlyphTexture)_symbol;
            symbol.anchorPoint.set(Ax, Ay);
            symbol.id=this.id;
            symbol.text = this.text;           
            symbol.size = this.size;
            symbol.width=this.width;
            symbol.height=this.height;
            symbol.thickness = this.thickness;
            symbol.glyphs.clear();
            symbol.rotate=this.rotate;
            symbol.mirrored=this.mirrored;
            
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
            GlyphTexture symbol=(GlyphTexture)_symbol;
            Ax = symbol.anchorPoint.x;
            Ay = symbol.anchorPoint.y;
            this.id=symbol.id;
            this.text = symbol.text;        
            this.size = symbol.size;
            this.thickness = symbol.thickness;
            this.width=symbol.width;
            this.height=symbol.height;
            this.rotate=symbol.rotate;
            this.mirrored=symbol.mirrored;

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
            GlyphTexture.Memento other = (GlyphTexture.Memento) obj;
            return (other.id==this.id&&other.size == this.size &&other.width == this.width&&other.height == this.height &&
                    other.thickness == this.thickness && other.text.equals(this.text) && Utils.EQ(other.Ax,this.Ax) &&
                    Utils.EQ(other.Ay,this.Ay)&&Utils.EQ(other.rotate,this.rotate)&&this.mirrored==other.mirrored) && other.glyphs.equals(this.glyphs);
        }

        @Override
        public int hashCode() {
            int hash =
                31 +this.id+ this.size +this.width+this.height + this.thickness + this.text.hashCode() + Double.hashCode(Ax) + Double.hashCode(Ay) +Double.hashCode(rotate) +
                Boolean.hashCode(this.mirrored)+
                this.glyphs.hashCode();

            return hash;
        }
    }
}
