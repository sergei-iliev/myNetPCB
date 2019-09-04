package com.mynetpcb.core.capi.text.glyph;

import com.mynetpcb.core.capi.Externalizable;
import com.mynetpcb.core.utils.Utilities;

import java.awt.Point;
import java.awt.geom.AffineTransform;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/*
 * Represents pcb font symbol. Very basic representation of a font symbol that is drawn by straight lines
 */
public class Glyph implements Externalizable,Cloneable{
    
    private char character;
    //distance to next symbol
    private int delta;

    public Point[] points;
    
    protected int minx,miny;
    
    protected int maxx,maxy;

    public Glyph(){

    }
    
    @Override
    protected Glyph clone() throws CloneNotSupportedException {
        
        Glyph copy=(Glyph)super.clone();
        copy.points=new Point[points.length];
        for(int i=0;i<points.length;i++){
           copy.points[i]=new Point(points[i].x,points[i].y);           
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
        int ratio=20*((int)(size*10));
        for(int i=0;i<points.length;i++){
          points[i].x=ratio*points[i].x;                
          points[i].y=ratio*points[i].y;  
        }             
        delta*=ratio;
        this.resize();
    }
    //protected void 
    private void resize(){
        minx=Integer.MAX_VALUE;
        miny=Integer.MAX_VALUE;
        maxx=Integer.MIN_VALUE;
        maxy=Integer.MIN_VALUE;
        
        for(int i=0;i<points.length;i++){
            if(minx>points[i].x){
                minx=points[i].x;
            }
            if(miny>points[i].y){
                miny=points[i].y;
            }
            if(maxx<points[i].x){
              maxx=points[i].x;
            }
            if(maxy<points[i].y){
              maxy=points[i].y;
            }            
        }
    }
    public void setDelta(int delta) {
        this.delta = delta;
    }

    public int getDelta() {
        return delta;
    }

    public int getLinesNumber() {
        return points.length/2;
    }

    public char getChar(){
        return character;
    }
    
    public int getGlyphWidth(){
       return maxx-minx; 
    }
    
    public int getGlyphHeight(){
        return maxy-miny;  
    }

    public void setSize(double size){
          this.resetGlyph(size); 
    }
    public void Rotate(AffineTransform rotation) {
        for(int i=0;i<points.length;i++){
            rotation.transform(points[i], points[i]); 
        }
        this.resize();
    }
    /*
     * True mirroring - like viewing bottom from above
     */
    public void Invert(Point A,Point B){
        for(int i=0;i<points.length;i++){
            //points[i].setLocation(Utilities.mirrorPoint(A,B, points[i]));
        }
        this.resize();        
    }
    private void resetGlyph(double size){
      Glyph glyph = GlyphManager.INSTANCE.getGlyph(this.character);    
        for(int i=0;i<points.length;i++){
          points[i].x=glyph.points[i].x;
          points[i].y=glyph.points[i].y;  
        }
        scale(size);
    }
    
    @Override
    public String toXML() {
        // TODO Implement this method
        return null;
    }

    @Override
    public void fromXML(Node node) throws XPathExpressionException, ParserConfigurationException {
        Element e = (Element) node;
        character=e.getAttribute("char").charAt(0);
        delta=Integer.parseInt(e.getAttribute("delta"));
        NodeList lines=e.getElementsByTagName("line");
        GlyphParser parser=new GlyphParser();
        points=new Point[lines.getLength()*2];
        
        int pos=0;

        for(int i=0;i<lines.getLength();i++){
            Node n=lines.item(i);
            parser.setLine(n.getTextContent());           
            while(parser.more()) {
                points[pos]=new Point();
                points[pos].x=parser.eatInt();
                parser.eatChar(',');
                points[pos].y=parser.eatInt(); 
                if(parser.more()){
                  parser.eatChar(',');
                }
                pos++;
            }               
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
