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

public class GlyphTexture implements Texture {

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
    
    private boolean isSelected;
    
    public GlyphTexture(String text,String tag, int x, int y, int size) {
        this.anchorPoint = new Point(x, y);
        this.glyphs = new ArrayList<>();
        this.thickness = Grid.MM_TO_COORD(0.2);
        this.alignment = Text.Alignment.LEFT;
        this.selectionRectWidth=4000;        
        this.text = this.resetGlyphText(text);
        this.tag=tag;
        this.size=size;
        this.layermaskId=Layer.SILKSCREEN_LAYER_FRONT;
    }
    public void copy(Texture _copy){
        GlyphTexture copy=(GlyphTexture)_copy;
        this.anchorPoint.setLocation(copy.anchorPoint.x,copy.anchorPoint.y); 
        this.text = copy.text;
        this.tag = copy.tag;   
        this.fillColor=copy.fillColor;
        this.alignment=copy.alignment;
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

    public void Clear() {
        glyphs.clear();
        this.width=0;
        this.height=0;
    }


    private void resetGlyphBox(){
        switch(alignment.getOrientation()){
        case HORIZONTAL:
            for (Glyph glyph : glyphs) {
                this.width += glyph.getGlyphWidth() + glyph.getDelta();
                this.height = Math.max(glyph.getGlyphHeight() + glyph.miny, this.height);
            }        
            break;
        case VERTICAL:
            for (Glyph glyph : glyphs) {
                this.height += glyph.getGlyphHeight() + glyph.getDelta();
                this.width = Math.max(glyph.getGlyphWidth()+glyph.minx, this.width);
            }            
            break;
        }
    }
    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        if (text == null) {
            return;
        }
        this.size=size;
        //reset original text
        this.text = resetGlyphText(this.text);
        //reset size
        for (Glyph glyph : glyphs) {
            glyph.setSize(Grid.COORD_TO_MM(this.size));
        }        
        //reset orientation
        if(this.alignment.getOrientation() == Text.Orientation.VERTICAL){
            AffineTransform rotation = AffineTransform.getRotateInstance(-Math.PI / 2, 0, 0);
            for (Glyph glyph : glyphs) {
                glyph.Rotate(rotation);
            }
        }
        //reset box
        resetGlyphBox();
    }
    
    public void setText(String text) {
        //read original text
        this.text = resetGlyphText(text);
        //reset size
        for (Glyph glyph : glyphs) {
            glyph.setSize(Grid.COORD_TO_MM(this.size));
        }
        //reset orientation
        if(this.alignment.getOrientation() == Text.Orientation.VERTICAL){
            AffineTransform rotation = AffineTransform.getRotateInstance(-Math.PI / 2, 0, 0);
            for (Glyph glyph : glyphs) {
                glyph.Rotate(rotation);
            }
        }
        //reset box
        resetGlyphBox();
        
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


    public boolean isEmpty() {
        return text == null || text.length() == 0;
    }

    @Override
    public Rectangle getBoundingShape() {
        if (text == null || text.length() == 0) {
            return null;
        }
        
        Rectangle r = new Rectangle();
        switch (this.alignment) {
        case LEFT:
            //left bottom
            r.setRect(anchorPoint.x, anchorPoint.y - height, width, height);
            break;
        case RIGHT:
            //right bottom
            r.setRect(anchorPoint.x-width, anchorPoint.y - height, width, height);                        
            break;
        case BOTTOM:
            r.setRect(anchorPoint.x-width, anchorPoint.y-height, width, height);
            break;
        case TOP:
            r.setRect(anchorPoint.x-width, anchorPoint.y, width, height);            
            break;
        }
        return r;
    }
    @Override
    public Point getAnchorPoint() {
        return anchorPoint;
    }

    //@Override
    public void Move(int xoffset, int yoffset) {
        anchorPoint.setLocation(anchorPoint.x + xoffset, anchorPoint.y + yoffset);
    }

    //@Override
    public void Mirror(Point A, Point B) {
        Text.Alignment newAlignment;
        Utilities.mirrorPoint(A,B, anchorPoint);
                
        if (A.x ==B.x) { //right-left mirroring
            this.alignment = this.alignment.Mirror(true);            
        } else { //***top-botom mirroring
            this.alignment = this.alignment.Mirror(false);          
        }                
    }

    //@Override
    public void Translate(AffineTransform translate) {
        // TODO Implement this method
    }

    //@Override
    public void Rotate(AffineTransform rotation) {
        
   
        rotation.transform(anchorPoint, anchorPoint);
        this.alignment =
                this.alignment.Rotate(rotation.getShearY() > 0 ? true :
                                         false);        
        int w=this.height;
        int h=this.width;
        
        switch(alignment){
        case LEFT:
                //read original text
                this.text = resetGlyphText(text);
                        //reset height
                for (Glyph glyph : glyphs) {
                     glyph.setSize(Grid.COORD_TO_MM(this.size));                    
                }             
              break;
        case RIGHT:
            //read original text
            this.text = resetGlyphText(text);
            //reset height
            for (Glyph glyph : glyphs) {
                 glyph.setSize(Grid.COORD_TO_MM(this.size));
            }                           
            break;
        case BOTTOM:
            //read original text
            this.text = resetGlyphText(text);
            //reset height
            for (Glyph glyph : glyphs) {
                 glyph.setSize(Grid.COORD_TO_MM(this.size));
            } 
            //rotate
            AffineTransform rotate = AffineTransform.getRotateInstance(-Math.PI / 2, 0, 0);
                 for (Glyph glyph : glyphs) {
                        glyph.Rotate(rotate);
                 }
              break;
        case TOP:
            //read original text
            this.text = resetGlyphText(text);
            //reset height
            for (Glyph glyph : glyphs) {
                 glyph.setSize(Grid.COORD_TO_MM(this.size));
            } 
            //rotate
            rotate = AffineTransform.getRotateInstance(-Math.PI / 2, 0, 0);
            for (Glyph glyph : glyphs) {
                   glyph.Rotate(rotate);
            }            
            break;
        }
        this.width=w;
        this.height=h;
      
        //***compencate the baseline!
        if (rotation.getShearY() > 0) { //***clockwise            
            if(this.alignment.getOrientation()==Text.Orientation.VERTICAL){               
               anchorPoint.x+=this.width;
            }            
        }else{
            if(this.alignment.getOrientation()==Text.Orientation.HORIZONTAL){               
               anchorPoint.y+=this.height;
            }
        }

    }


    @Override
    public void Paint(Graphics2D g2, ViewportWindow viewportWindow, AffineTransform scale, int layermask) {
        if (this.isEmpty()) {
            return;
        }
        
        Layer.Side side= (Layer.Side.resolve(layermask));
        
        if (this.isSelected)
            g2.setColor(Color.GRAY);
        else
            g2.setColor(fillColor);

        double lineThickness = thickness * scale.getScaleX();

        FlyweightProvider provider = ShapeFlyweightFactory.getProvider(GeneralPath.class);
        GeneralPath temporal = (GeneralPath) provider.getShape();


        Rectangle r = getBoundingShape();
        //Rectangle2D scaledRect = Utilities.getScaleRect(r, scale);
        //scaledRect.setRect(scaledRect.getX() - viewportWindow.x, scaledRect.getY() - viewportWindow.y,
        //                   scaledRect.getWidth(), scaledRect.getHeight());
        //g2.setStroke(new BasicStroke());
        //g2.draw(scaledRect);
        g2.setStroke(new BasicStroke((float) lineThickness, Trackable.JoinType
                                                                     .JOIN_ROUND
                                                                     .ordinal(), Trackable.EndType
                                                                                          .CAP_ROUND
                                                                                          .ordinal()));

        switch (this.alignment) {
        case LEFT:
            int xoffset = 0;
            for (Glyph glyph : glyphs) {
                if(glyph.getChar()==' '){
                    xoffset += glyph.getDelta();
                    continue;
                }
                Point A=new Point(r.x,r.y);
                Point B=new Point(r.x,r.y+r.height);
                int j = 0;
                for (int i = 0; i < glyph.getLinesNumber(); i++, j = (j + 2)) {
                    if(side==Layer.Side.BOTTOM){
                     Point source=new Point(glyph.points[j].x + anchorPoint.x + xoffset,
                                    glyph.points[j].y + anchorPoint.y - r.height);
                    
                    
                     Utilities.mirrorPoint(A, B, source);
                     temporal.moveTo(source.getX()+r.width, source.getY());
                    
                     source=new Point(glyph.points[j + 1].x + anchorPoint.x + xoffset,
                                    glyph.points[j + 1].y + anchorPoint.y - r.height);
                    
                    
                     Utilities.mirrorPoint(A, B, source);
                     temporal.lineTo(source.getX()+r.width, source.getY());
                    }else{
                    temporal.moveTo(glyph.points[j].x + anchorPoint.x + xoffset,
                                    glyph.points[j].y + anchorPoint.y - r.height);
                    temporal.lineTo(glyph.points[j + 1].x + anchorPoint.x + xoffset,
                                    glyph.points[j + 1].y + anchorPoint.y - r.height);
                    }
                }
                xoffset += glyph.getGlyphWidth() + glyph.getDelta();
            }    
            
             
             break;
        case RIGHT:
            xoffset = 0;
            for (Glyph glyph : glyphs) {
                if(glyph.getChar()==' '){
                    xoffset += glyph.getDelta();
                    continue;
                }
                Point A=new Point(r.x+r.width,r.y);
                Point B=new Point(r.x+r.width,r.y+r.height);
                int j = 0;
                for (int i = 0; i < glyph.getLinesNumber(); i++, j = (j + 2)) {
                    if(side==Layer.Side.BOTTOM){
                     Point source=new Point(glyph.points[j].x + anchorPoint.x + xoffset - r.width,
                                    glyph.points[j].y + anchorPoint.y - r.height);
                    
                    
                     Utilities.mirrorPoint(A, B, source);
                     temporal.moveTo(source.getX()-r.width, source.getY());
                    
                     source=new Point(glyph.points[j + 1].x + anchorPoint.x + xoffset -r.width,
                                    glyph.points[j + 1].y + anchorPoint.y - r.height);
                    
                    
                     Utilities.mirrorPoint(A, B, source);
                     temporal.lineTo(source.getX()-r.width, source.getY());
                    }else{
                    temporal.moveTo(glyph.points[j].x + anchorPoint.x + xoffset - r.width,
                                    glyph.points[j].y + anchorPoint.y - r.height);
                    temporal.lineTo(glyph.points[j + 1].x + anchorPoint.x + xoffset -r.width,
                                    glyph.points[j + 1].y + anchorPoint.y - r.height);
                    }
                }
                xoffset += glyph.getGlyphWidth() + glyph.getDelta();
            }    
        
             break;
        case BOTTOM:
            int yoffset = 0;
            for (Glyph glyph : glyphs) {
                if(glyph.getChar()==' '){
                    yoffset += glyph.getDelta();
                    continue;
                }
                Point A=new Point(r.x,r.y+r.height);
                Point B=new Point(r.x+r.width,r.y+r.height);
                int j = 0;
                for (int i = 0; i < glyph.getLinesNumber(); i++, j = (j + 2)) {
                    if(side==Layer.Side.BOTTOM){
                     Point source=new Point(glyph.points[j].x + anchorPoint.x  - r.width,
                                    glyph.points[j].y + anchorPoint.y-yoffset);
                    
                    
                     Utilities.mirrorPoint(A, B, source);
                     temporal.moveTo(source.getX(), source.getY()-r.height);
                    
                     source=new Point(glyph.points[j + 1].x + anchorPoint.x  -r.width,
                                    glyph.points[j + 1].y + anchorPoint.y-yoffset);
                    
                    
                     Utilities.mirrorPoint(A, B, source);
                     temporal.lineTo(source.getX(), source.getY()-r.height);
                    }else{
                    temporal.moveTo(glyph.points[j].x + anchorPoint.x  - r.width,
                                    glyph.points[j].y + anchorPoint.y-yoffset);
                    temporal.lineTo(glyph.points[j + 1].x + anchorPoint.x  - r.width,
                                    glyph.points[j + 1].y + anchorPoint.y-yoffset);
                    }
                }
                yoffset += glyph.getGlyphHeight() + glyph.getDelta();
            }
            break;
        case TOP:
            yoffset = 0;
            for (Glyph glyph : glyphs) {
                if(glyph.getChar()==' '){
                    yoffset += glyph.getDelta();
                    continue;
                }
                Point A=new Point(r.x,r.y);
                Point B=new Point(r.x+r.width,r.y);
                int j = 0;
                for (int i = 0; i < glyph.getLinesNumber(); i++, j = (j + 2)) {
                   if(side==Layer.Side.BOTTOM){
                    Point source=new Point(glyph.points[j].x + anchorPoint.x  - r.width,
                                    glyph.points[j].y + anchorPoint.y-yoffset+r.height);
                    
                    
                    Utilities.mirrorPoint(A, B, source);
                    temporal.moveTo(source.getX(), source.getY()+r.height);
                    
                    source=new Point(glyph.points[j + 1].x + anchorPoint.x  - r.width,
                                    glyph.points[j + 1].y + anchorPoint.y-yoffset+r.height);
                    
                    
                    Utilities.mirrorPoint(A, B, source);
                    temporal.lineTo(source.getX(), source.getY()+r.height);                    
                   }else{ 
                    temporal.moveTo(glyph.points[j].x + anchorPoint.x  - r.width,
                                    glyph.points[j].y + anchorPoint.y-yoffset+r.height);
                    temporal.lineTo(glyph.points[j + 1].x + anchorPoint.x  - r.width,
                                    glyph.points[j + 1].y + anchorPoint.y-yoffset+r.height);
                   }
                }
                yoffset += glyph.getGlyphHeight() + glyph.getDelta();
            }            
            break;
        }

        AffineTransform translate = AffineTransform.getTranslateInstance(-viewportWindow.x, -viewportWindow.y);

        temporal.transform(scale);
        temporal.transform(translate);
        g2.draw(temporal);
        
        provider.reset();
        
        if (this.isSelected){
            this.drawControlShape(g2,viewportWindow,scale);
        }
    }
    public void Print(Graphics2D g2,PrintContext printContext,int layermask){
        if (this.isEmpty()) {
            return;
        }

        g2.setColor(fillColor);

        Rectangle r = getBoundingShape();
        
        FlyweightProvider provider = ShapeFlyweightFactory.getProvider(GeneralPath.class);
        GeneralPath temporal = (GeneralPath) provider.getShape();


        g2.setStroke(new BasicStroke(thickness, Trackable.JoinType
                                                                     .JOIN_ROUND
                                                                     .ordinal(), Trackable.EndType
                                                                                          .CAP_ROUND
                                                                                          .ordinal()));

        switch (this.alignment) {
        case LEFT:
            int xoffset = 0;
            for (Glyph glyph : glyphs) {
                if(glyph.getChar()==' '){
                    xoffset += glyph.getDelta();
                    continue;
                }
                int j = 0;
                for (int i = 0; i < glyph.getLinesNumber(); i++, j = (j + 2)) {

                    temporal.moveTo(glyph.points[j].x + anchorPoint.x + xoffset,
                                    glyph.points[j].y + anchorPoint.y - r.height);
                    temporal.lineTo(glyph.points[j + 1].x + anchorPoint.x + xoffset,
                                    glyph.points[j + 1].y + anchorPoint.y - r.height);
                    
                }
                xoffset += glyph.getGlyphWidth() + glyph.getDelta();
            }    
            
             
             break;
        case RIGHT:
            xoffset = 0;
            for (Glyph glyph : glyphs) {
                if(glyph.getChar()==' '){
                    xoffset += glyph.getDelta();
                    continue;
                }
                int j = 0;
                for (int i = 0; i < glyph.getLinesNumber(); i++, j = (j + 2)) {

                    temporal.moveTo(glyph.points[j].x + anchorPoint.x + xoffset - r.width,
                                    glyph.points[j].y + anchorPoint.y - r.height);
                    temporal.lineTo(glyph.points[j + 1].x + anchorPoint.x + xoffset -r.width,
                                    glyph.points[j + 1].y + anchorPoint.y - r.height);
                    
                }
                xoffset += glyph.getGlyphWidth() + glyph.getDelta();
            }    
        
             break;
        case BOTTOM:
            int yoffset = 0;
            for (Glyph glyph : glyphs) {
                if(glyph.getChar()==' '){
                    yoffset += glyph.getDelta();
                    continue;
                }
                int j = 0;
                for (int i = 0; i < glyph.getLinesNumber(); i++, j = (j + 2)) {
                  
                    temporal.moveTo(glyph.points[j].x + anchorPoint.x  - r.width,
                                    glyph.points[j].y + anchorPoint.y-yoffset);
                    temporal.lineTo(glyph.points[j + 1].x + anchorPoint.x  - r.width,
                                    glyph.points[j + 1].y + anchorPoint.y-yoffset);
                    
                }
                yoffset += glyph.getGlyphHeight() + glyph.getDelta();
            }
            break;
        case TOP:
            yoffset = 0;
            for (Glyph glyph : glyphs) {
                if(glyph.getChar()==' '){
                    yoffset += glyph.getDelta();
                    continue;
                }

                int j = 0;
                for (int i = 0; i < glyph.getLinesNumber(); i++, j = (j + 2)) {
                    temporal.moveTo(glyph.points[j].x + anchorPoint.x  - r.width,
                                    glyph.points[j].y + anchorPoint.y-yoffset+r.height);
                    temporal.lineTo(glyph.points[j + 1].x + anchorPoint.x  - r.width,
                                    glyph.points[j + 1].y + anchorPoint.y-yoffset+r.height);
                   }
                
                yoffset += glyph.getGlyphHeight() + glyph.getDelta();
            }            
            break;
        }
        g2.draw(temporal);        
        provider.reset();        
    } 
    
    private void drawControlShape(Graphics2D g2, ViewportWindow viewportWindow, AffineTransform scale){
        Utilities.drawCrosshair(g2, viewportWindow, scale, null, selectionRectWidth, anchorPoint);
    }
    @Override
    public boolean isClicked(int x, int y) {
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
    
    public void setAlignment(Text.Alignment alignment) {
        if(this.alignment.getOrientation()==alignment.getOrientation()){
            if (alignment == Text.Alignment.LEFT)
                  anchorPoint.setLocation(anchorPoint.x - this.width,anchorPoint.y);
            else if(alignment == Text.Alignment.RIGHT){
                            anchorPoint.setLocation(anchorPoint.x + this.width,
                                                    anchorPoint.y);
            }else if (alignment == Text.Alignment.TOP){
                anchorPoint.setLocation(anchorPoint.x,
                                        anchorPoint.y - this.height);
            }else{
                anchorPoint.setLocation(anchorPoint.x,
                                         anchorPoint.y + this.height);
            }
            this.alignment = alignment; 
        }                
        
    }
    
    public void setOrientation(Text.Orientation orientation) {        
      if(orientation==this.alignment.getOrientation()){
            return;
      }
      Rectangle r=getBoundingShape();
      AffineTransform rotation;
      if(orientation==Text.Orientation.VERTICAL){ //from horizontal to vertical
        rotation = AffineTransform.getRotateInstance(-Math.PI / 2, r.getCenterX(), r.getCenterY());        
      }else{
        rotation = AffineTransform.getRotateInstance(Math.PI / 2, r.getCenterX(), r.getCenterY());                       
      }
      this.Rotate(rotation); 
    }
    
    public Text.Alignment getAlignment() {
        return this.alignment;
    }
    
    public String toXML() {
        return (text.equals("") ? "" :
                text + "," + anchorPoint.x + "," + anchorPoint.y +
                "," + this.alignment+","+this.thickness+","+this.size);
    }
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
        this.alignment = Text.Alignment.valueOf(st.nextToken().toUpperCase());                
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
        this.setSize(size);
        
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

        private int Ax;

        private int Ay;

        private int id;
        
        private List<Glyph> glyphs;

        private Text.Alignment alignment;

        private int size,width,height;

        private int thickness;
        @Override
        public void loadStateTo(Texture _symbol) {
            GlyphTexture symbol=(GlyphTexture)_symbol;
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
            GlyphTexture symbol=(GlyphTexture)_symbol;
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
            GlyphTexture.Memento other = (GlyphTexture.Memento) obj;
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
