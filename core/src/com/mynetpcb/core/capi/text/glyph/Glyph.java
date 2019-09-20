package com.mynetpcb.core.capi.text.glyph;

import com.mynetpcb.core.capi.Externalizable;
import com.mynetpcb.core.utils.Utilities;

import com.mynetpcb.d2.shapes.Box;
import com.mynetpcb.d2.shapes.Line;
import com.mynetpcb.d2.shapes.Point;

import java.awt.geom.AffineTransform;
import com.mynetpcb.d2.shapes.Segment;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/*
 * Represents pcb font symbol. Very basic representation of a font symbol that is drawn by straight lines
 */
public class Glyph implements Cloneable{
    
    char character;
    //distance to next symbol
    int delta=8;

    Segment[] segments;
    
    double minx,miny;
    
    double maxx,maxy;

    public Glyph(){

    }
    
    @Override
    protected Glyph clone() throws CloneNotSupportedException {
        Glyph copy=(Glyph)super.clone();
        copy.segments=new Segment[this.segments.length];
        for(int i=0;i<this.segments.length;i++){
           copy.segments[i]=this.segments[i].clone();           
        }        
        return copy;
        
    }
    /*
     * Enlarge to real size
     * 200 1 mm 
     */

    /*Height comes in mm!!!!!!!!!!!!!!!!!!!!
     * assume that the step is 0.1(20)
     */
    protected void scale(double size){  
        double ratio=20*((size*10));
        for(int i=0;i<this.segments.length;i++){
         this.segments[i].scale(ratio);
        }             
        this.delta*=ratio;
        this.resize();
        
    }
    //protected void 
    public void resize(){
        minx=Integer.MAX_VALUE;
        miny=Integer.MAX_VALUE;
        maxx=Integer.MIN_VALUE;
        maxy=Integer.MIN_VALUE;
        
        for(int i=0;i<this.segments.length;i++){
            Box box=this.segments[i].box();               
                
            if(this.minx>box.min.x){
                this.minx=box.min.x;
            }
            if(this.miny>box.min.y){
                this.miny=box.min.y;
            }
            if(this.maxx<box.max.x){
              this.maxx=box.max.x;
            }
            if(this.maxy<box.max.y){
              this.maxy=box.max.y;
            }            
        }
    }
    public void setDelta(int delta) {
        this.delta = delta;
    }

    public int getDelta() {
        return delta;
    }

    public char getChar(){
        return character;
    }
    public void mirror(Line line){
    for(int i=0;i<this.segments.length;i++){
        this.segments[i].mirror(line);                                          
    }
    this.resize();
    }    
    public void move(double xoffset,double yoffset){
        for(Segment segment:this.segments){            
           segment.move(xoffset,yoffset);
        };
        this.resize();
    }
    
    public int getWidth(){
       return (int)(maxx-minx); 
    }
    
    public int getHeight(){
        return (int)(maxy-miny);  
    }

    public void setSize(double size){
          this.resetGlyph(size); 
    }
    public void rotate(double angle,Point pt) {
        for(int i=0;i<this.segments.length;i++){
                this.segments[i].rotate(angle,pt);                                              
        }
        this.resize();
    }
    private void resetGlyph(double size){
        Glyph glyph = GlyphManager.INSTANCE.getGlyph(this.character);    
        for(int i=0;i<this.segments.length;i++){
          this.segments[i].ps.set(glyph.segments[i].ps);
          this.segments[i].pe.set(glyph.segments[i].pe);
        }
        this.scale(size);              
    }

    public void fromXML(Node node) throws XPathExpressionException, ParserConfigurationException {
                
        Element e = (Element) node;
        character=e.getAttribute("char").charAt(0);
        delta=Integer.parseInt(e.getAttribute("delta"));
        NodeList lines=e.getElementsByTagName("line");
        
        for(int i=0;i<lines.getLength();i++){
            Node n=lines.item(i);
            String line=n.getTextContent();
            String[] array=line.split(",");
            this.segments[i]=new Segment(new Point(Double.parseDouble(array[0]),Double.parseDouble(array[1])), new Point(Double.parseDouble(array[0]),Double.parseDouble(array[1])));                           
        }
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Glyph)) {
            return false;
        }
        Glyph other = (Glyph)obj;
        return other.character==this.character;
    }
    
    @Override
    public int hashCode() {    
        return Character.hashCode(character);
    }
}
