package com.mynetpcb.symbol.shape;

import com.mynetpcb.core.capi.Externalizable;
import com.mynetpcb.core.capi.ViewportWindow;
import com.mynetpcb.core.capi.layer.Layer;
import com.mynetpcb.core.capi.pin.Pinable;
import com.mynetpcb.core.capi.print.PrintContext;
import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.text.CompositeTextable;
import com.mynetpcb.core.capi.text.Texture;
import com.mynetpcb.core.capi.text.font.SymbolFontTexture;
import com.mynetpcb.core.capi.undo.AbstractMemento;
import com.mynetpcb.core.capi.undo.MementoType;
import com.mynetpcb.core.capi.unit.Unit;
import com.mynetpcb.core.utils.Utilities;
import com.mynetpcb.d2.shapes.Box;
import com.mynetpcb.d2.shapes.Circle;
import com.mynetpcb.d2.shapes.Line;
import com.mynetpcb.d2.shapes.Point;
import com.mynetpcb.d2.shapes.Segment;
import com.mynetpcb.d2.shapes.Utils;
import com.mynetpcb.symbol.unit.Symbol;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import java.util.StringTokenizer;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class Pin extends Shape implements Pinable,CompositeTextable,Externalizable{

    public enum PinType {
        SIMPLE,
        COMPLEX;
    }
    public enum Style {
        LINE, /*default*/
        INVERTED,
        CLOCK,
        INVERTED_CLOCK,
        INPUT_LOW,
        CLOCK_LOW,
        OUTPUT_LOW,
        FALLING_EDGE_CLOCK,
        NON_LOGIC                
    }
    
    public enum Position{            
        TOP_LEFT,
        BOTTOM_RIGHT;        
        
        
        public static Position findPositionToLine(double x,double y,Point l1,Point l2){
                    if(l1.y==l2.y){ //horizontal line
                       if(y<l1.y){
                               return TOP_LEFT;
                       }else{
                               return BOTTOM_RIGHT;
                       }    
                    }else{  //vertical line
                       if(x<l1.x){
                               return TOP_LEFT;
                       }else{
                               return BOTTOM_RIGHT;
                       }                            
                    }
       }
            
    };

    private Segment segment;
    private PinType type;
    private Style style;
    private SymbolFontTexture name,number;
    private Pinable.Orientation orientation;
    
    public Pin() {
        super(1,Layer.LAYER_ALL);
        this.setDisplayName("Pin");             
        this.selectionRectWidth=2;
        
        
        this.segment=new Segment();
        this.type = PinType.COMPLEX;
        this.style = Style.FALLING_EDGE_CLOCK;
        this.fillColor=Color.BLACK;
        this.name=new SymbolFontTexture("XXX","name",-8,0,Texture.Alignment.RIGHT.ordinal(),8,Font.PLAIN);
        this.number=new SymbolFontTexture("1","number",10,-4,Texture.Alignment.LEFT.ordinal(),8,Font.PLAIN);
        this.init(Orientation.EAST);        
    }
    @Override
    public Pin clone() throws CloneNotSupportedException {    
        Pin copy=(Pin)super.clone();
        copy.segment=this.segment.clone();
        copy.number = this.number.clone();
        copy.name = this.name.clone();
        return copy;
    }
    @Override
    public long getClickableOrder() {        
        return 1;
    }
    public PinType getPinType(){
        return type;
    }
    public Orientation getOrientation(){
        return orientation;
    }
    
    public Style getStyle(){
        return style;
    }
    public void setStyle(Style style){
        this.style=style;
    }
    
    @Override
    public Point alignToGrid(boolean isRequired) {
        Point center=this.segment.ps;
        Point point=this.getOwningUnit().getGrid().positionOnGrid(center.x,center.y);
        this.move(point.x - center.x,point.y - center.y);
        return new Point(point.x - center.x, point.y - center.y);         
    }
    
    @Override
    public Box getBoundingShape(){
        return this.segment.box();
    }
    private void init(Pinable.Orientation orientation){
            this.orientation=orientation;
        switch (this.orientation) {
        case EAST:        
            this.segment.pe.set(this.segment.ps.x + (this.type == PinType.COMPLEX ? Utilities.PIN_LENGTH : Utilities.PIN_LENGTH / 2), this.segment.ps.y);
            break;
        case WEST:
            this.segment.pe.set(this.segment.ps.x - (this.type == PinType.COMPLEX ? Utilities.PIN_LENGTH : Utilities.PIN_LENGTH / 2), this.segment.ps.y);       
            break;
        case NORTH:
            this.segment.pe.set(this.segment.ps.x, this.segment.ps.y - (this.type == PinType.COMPLEX ? Utilities.PIN_LENGTH : Utilities.PIN_LENGTH / 2));       
            break;
        case SOUTH:     
            this.segment.pe.set(this.segment.ps.x, this.segment.ps.y + (this.type == PinType.COMPLEX ? Utilities.PIN_LENGTH : Utilities.PIN_LENGTH / 2));
        }   
    }
    
    @Override
    public boolean isClicked(int x,int y) {
              Box rect = Box.fromRect(x
                                    - (this.selectionRectWidth / 2), y
                                    - (this.selectionRectWidth / 2), this.selectionRectWidth,
                                    this.selectionRectWidth);
              
                    if (Utils.intersectLineRectangle(
                                    this.segment.ps,this.segment.pe, rect.min, rect.max)) {                 
                            return true;
                    }else{
                            return false;
                    }                    
    } 
    public void rotate(double angle,Point origin){    
            //read current position 
            Position oposname= Position.findPositionToLine(this.name.getAnchorPoint().x,this.name.getAnchorPoint().y,this.segment.ps,this.segment.pe);
            Position oposnumber= Position.findPositionToLine(this.number.getAnchorPoint().x,this.number.getAnchorPoint().y,this.segment.ps,this.segment.pe);
            
            this.segment.rotate(angle,origin);
            this.orientation=orientation.rotate(angle>0?false:true);
            this.name.rotate(angle,origin);
            this.number.rotate(angle,origin);
            
            //read new position
            Position nposname=Position.findPositionToLine(this.name.getAnchorPoint().x,this.name.getAnchorPoint().y,this.segment.ps,this.segment.pe);        
            this.normalizeText(this.name,oposname,nposname);

            
            Position nposnumber=Position.findPositionToLine(this.number.getAnchorPoint().x,this.number.getAnchorPoint().y,this.segment.ps,this.segment.pe);  
            this.normalizeText(this.number,oposnumber,nposnumber);			
    }
    private void normalizeText(SymbolFontTexture text,Position opos,Position npos){
            if(opos==npos){
               return;      
            }

            text.mirror(new Line(this.segment.ps,this.segment.pe));      
    }
    @Override
    public void move(double xoffset,double yoffset) {
        this.segment.move(xoffset,yoffset);
        this.name.move(xoffset,yoffset);
        this.number.move(xoffset,yoffset);
    }
    @Override
    public void mirror(Line line) {
        
        Position oposname= Position.findPositionToLine(this.name.shape.anchorPoint.x,this.name.shape.anchorPoint.y,this.segment.ps,this.segment.pe);
        Position oposnumber= Position.findPositionToLine(this.number.shape.anchorPoint.x,this.number.shape.anchorPoint.y,this.segment.ps,this.segment.pe);
              
        this.segment.mirror(line);    
        if(line.isVertical()){ //left-right               
                this.orientation = this.orientation.mirror(true);   
        }else{          
                this.orientation = this.orientation.mirror(false);
        }     
        this.name.mirror(line);
        this.number.mirror(line);
        
              //read new position
        Position nposname=Position.findPositionToLine(this.name.shape.anchorPoint.x,this.name.shape.anchorPoint.y,this.segment.ps,this.segment.pe);              
        Position nposnumber=Position.findPositionToLine(this.number.shape.anchorPoint.x,this.number.shape.anchorPoint.y,this.segment.ps,this.segment.pe);        
        
        this.normalizeText(this.name,oposname,nposname);
        this.normalizeText(this.number,oposnumber,nposnumber);          
    }
    /*
     * keep text orientation too, observing text normalization
     */
    public void setOrientation(Orientation orientation){
     Orientation o=this.orientation;     
     
     while(o!=orientation){
       switch (o) {
             case EAST:        
                     o=Orientation.SOUTH;
                     this.rotate(-90,this.segment.ps);
         break;
             case WEST:
                     o=Orientation.NORTH;
                     this.rotate(-90,this.segment.ps);
         break;
             case NORTH:
                     o=Orientation.EAST;
                     this.rotate(-90,this.segment.ps);
         break;
             case SOUTH:        
                     o=Orientation.WEST;
                     this.rotate(-90,this.segment.ps);
      }   
     }
     this.orientation=orientation;
    }
    
    public void setPinType(PinType type){
            this.type=type;
            this.init(this.orientation);
    }
    @Override
    public Point getCenter(){
       return this.segment.ps;
    }
    @Override
    public void setSelected (boolean selection) {
            super.setSelected(selection);
            this.number.setSelected(selection);
            this.name.setSelected(selection);
    }    
    @Override
    public void paint(Graphics2D g2, ViewportWindow viewportWindow, AffineTransform scale, int layermask) {
        Box rect = this.segment.box();
        rect.scale(scale.getScaleX());
        if (!rect.intersects(viewportWindow)) {
                return;
        }
        
        if (this.isSelected()) {            
            g2.setColor(Color.gray);
            this.name.setFillColor(Color.gray);
            this.number.setFillColor(Color.gray);
        } else {
            g2.setColor(fillColor);
            this.name.setFillColor(fillColor);
            this.number.setFillColor(fillColor);
        }  
        g2.setStroke(new BasicStroke(1));
        
        switch(this.style){
        case LINE:
                this.drawPinLine(g2, viewportWindow, scale,0);
                break;
        case INVERTED:
                this.drawPinLine(g2, viewportWindow, scale,(Utilities.PIN_LENGTH / 3));
                this.drawInverted(g2, viewportWindow, scale);
                break;    
        case CLOCK:
                this.drawPinLine(g2, viewportWindow, scale,0);
                this.drawTriState(g2, viewportWindow, scale);
                break;
        case INVERTED_CLOCK:
                this.drawPinLine(g2, viewportWindow, scale,(Utilities.PIN_LENGTH / 3));
                this.drawInverted(g2, viewportWindow, scale);
                this.drawTriState(g2, viewportWindow, scale);
                break;
        case INPUT_LOW:
                this.drawPinLine(g2, viewportWindow, scale,0);
                this.drawInputLow(g2, viewportWindow, scale);
                break;
        case CLOCK_LOW:
                this.drawPinLine(g2, viewportWindow, scale,0);
                this.drawInputLow(g2, viewportWindow, scale);
                this.drawTriState(g2, viewportWindow, scale);
                break;
        case  OUTPUT_LOW:
                this.drawPinLine(g2, viewportWindow, scale,0);
                this.drawOutputLow(g2, viewportWindow, scale);
                break;
        case  FALLING_EDGE_CLOCK:
                this.drawPinLine(g2, viewportWindow, scale, Utilities.PIN_LENGTH / 6);
                this.drawFallingEdgeClock(g2, viewportWindow, scale);
                break;
                  
        }
        if (this.type == PinType.COMPLEX) {                     
            this.name.paint(g2, viewportWindow, scale,0);
            this.number.paint(g2, viewportWindow, scale,0);        
        }
        
        if (isSelected()) {
            g2.setColor(Color.BLUE);
            Circle c=new Circle(this.segment.pe.clone(), 2);
            c.scale(scale.getScaleX());
            c.move(-viewportWindow.getX(),- viewportWindow.getY());
            c.paint(g2,false);        
        }
    }
    @Override
    public void print(Graphics2D g2, PrintContext printContext, int layermask) {

        ViewportWindow viewportWindow = new ViewportWindow(0, 0, 0, 0);
        AffineTransform scale = AffineTransform.getScaleInstance(1, 1);

        g2.setColor(Color.black);
        this.name.setFillColor(Color.black);
        this.number.setFillColor(Color.black);
          
        g2.setStroke(new BasicStroke(1));
        
        switch(this.style){
        case LINE:
                this.drawPinLine(g2, viewportWindow, scale,0);
                break;
        case INVERTED:
                this.drawPinLine(g2, viewportWindow, scale,(Utilities.PIN_LENGTH / 3));
                this.drawInverted(g2, viewportWindow, scale);
                break;    
        case CLOCK:
                this.drawPinLine(g2, viewportWindow, scale,0);
                this.drawTriState(g2, viewportWindow, scale);
                break;
        case INVERTED_CLOCK:
                this.drawPinLine(g2, viewportWindow, scale,(Utilities.PIN_LENGTH / 3));
                this.drawInverted(g2, viewportWindow, scale);
                this.drawTriState(g2, viewportWindow, scale);
                break;
        case INPUT_LOW:
                this.drawPinLine(g2, viewportWindow, scale,0);
                this.drawInputLow(g2, viewportWindow, scale);
                break;
        case CLOCK_LOW:
                this.drawPinLine(g2, viewportWindow, scale,0);
                this.drawInputLow(g2, viewportWindow, scale);
                this.drawTriState(g2, viewportWindow, scale);
                break;
        case  OUTPUT_LOW:
                this.drawPinLine(g2, viewportWindow, scale,0);
                this.drawOutputLow(g2, viewportWindow, scale);
                break;
        case  FALLING_EDGE_CLOCK:
                this.drawPinLine(g2, viewportWindow, scale, Utilities.PIN_LENGTH / 6);
                this.drawFallingEdgeClock(g2, viewportWindow, scale);
                break;
                  
        }
        if (this.type == PinType.COMPLEX) {                     
            this.name.paint(g2, viewportWindow, scale,0);
            this.number.paint(g2, viewportWindow, scale,0);        
        }        
        
    }
    
    private void drawFallingEdgeClock(Graphics2D g2, ViewportWindow viewportWindow, AffineTransform scale){
        double pinlength = (Utilities.PIN_LENGTH/6)*scale.getScaleX();
        Segment line=new Segment();
        double x=this.segment.ps.x*scale.getScaleX();
        double y=this.segment.ps.y*scale.getScaleX();
        switch (this.orientation) {
        case SOUTH:
            line.set(x - pinlength  - viewportWindow.getX(), y - viewportWindow.getY(), x - viewportWindow.getX(),
                          y + pinlength  - viewportWindow.getY());
            line.paint(g2,false);
            line.set(x + pinlength - viewportWindow.getX(), y - viewportWindow.getY(), x - viewportWindow.getX(),
                          y + pinlength - viewportWindow.getY());
            line.paint(g2,false);
            break;
        case NORTH:
            line.set(x - pinlength  - viewportWindow.getX(), y - viewportWindow.getY(), x - viewportWindow.getX(),
                          y - pinlength - viewportWindow.getY());
            line.paint(g2,false);
            line.set(x + pinlength  - viewportWindow.getX(), y - viewportWindow.getY(), x - viewportWindow.getX(),
                          y - pinlength  - viewportWindow.getY());
            line.paint(g2,false);
            break;
        case WEST:
            line.set(x - viewportWindow.getX(), y - pinlength - viewportWindow.getY(),
                          x - pinlength  - viewportWindow.getX(), y - viewportWindow.getY());
            line.paint(g2,false);
            line.set(x - viewportWindow.getX(), y + pinlength  - viewportWindow.getY(),
                          x - pinlength  - viewportWindow.getX(), y - viewportWindow.getY());
            line.paint(g2,false);
            break;
        case EAST:
            line.set(x - viewportWindow.getX(), y - pinlength  - viewportWindow.getY(),
                          x + pinlength  - viewportWindow.getX(), y - viewportWindow.getY());
            line.paint(g2,false);
            line.set(x - viewportWindow.getX(), y + pinlength  - viewportWindow.getY(),
                          x + pinlength  - viewportWindow.getX(), y - viewportWindow.getY());
            line.paint(g2,false);
            break;
        }
    }    
    private void drawOutputLow(Graphics2D g2, ViewportWindow viewportWindow, AffineTransform scale){
        double pinlength = Utilities.PIN_LENGTH *scale.getScaleX(); 
        Segment line=new Segment();
        double x=this.segment.ps.x*scale.getScaleX();
        double y=this.segment.ps.y*scale.getScaleX();
        switch (this.orientation) {
        case SOUTH:
            line.set(x - viewportWindow.getX(), y+ (pinlength / 3) - viewportWindow.getY(), x - (pinlength / 6) - viewportWindow.getX(),
                          y  - viewportWindow.getY());
            line.paint(g2,false);
            break;
        case NORTH:
            line.set(x - viewportWindow.getX(), y- (pinlength / 3) - viewportWindow.getY(), x - (pinlength / 6) - viewportWindow.getX(),
                          y - viewportWindow.getY());
            line.paint(g2,false);
            break;
        case WEST:
            line.set(x - viewportWindow.getX(), y- (pinlength / 6) - viewportWindow.getY(), x - (pinlength / 3) - viewportWindow.getX(),
                          y - viewportWindow.getY());
            line.paint(g2,false);
            break;
        case EAST:
            line.set(x - viewportWindow.getX(), y - (pinlength / 6) - viewportWindow.getY(), x + (pinlength / 3) - viewportWindow.getX(),
                          y  - viewportWindow.getY());
            line.paint(g2,false);
            break;
        }

    }    
    private void drawInputLow(Graphics2D g2, ViewportWindow viewportWindow, AffineTransform scale){
        double pinlength = Utilities.PIN_LENGTH *scale.getScaleX(); 
        Segment line=new Segment();
        double x=this.segment.ps.x*scale.getScaleX();
        double y=this.segment.ps.y*scale.getScaleX();
        switch (this.orientation) {
        case SOUTH:
            line.set(x - viewportWindow.getX(), y - viewportWindow.getY(), x - (pinlength / 6) - viewportWindow.getX(),
                          y + (pinlength / 3) - viewportWindow.getY());
            line.paint(g2,false);
            line.set(x - (pinlength / 6) - viewportWindow.getX(), y + (pinlength / 3) - viewportWindow.getY(),
                          x - viewportWindow.getX(), y + (pinlength / 3) - viewportWindow.getY());
            line.paint(g2,false);
            break;
        case NORTH:
            line.set(x - viewportWindow.getX(), y - viewportWindow.getY(), x - (pinlength / 6) - viewportWindow.getX(),
                          y - (pinlength / 3) - viewportWindow.getY());
            line.paint(g2,false);
            line.set(x - (pinlength / 6) - viewportWindow.getX(), y - (pinlength / 3) - viewportWindow.getY(),
                          x - viewportWindow.getX(), y - (pinlength / 3) - viewportWindow.getY());
            line.paint(g2,false);
            break;
        case WEST:
            line.set(x - viewportWindow.getX(), y - viewportWindow.getY(), x - (pinlength / 3) - viewportWindow.getX(),
                          y - (pinlength / 6) - viewportWindow.getY());
            line.paint(g2,false);
            line.set(x - (pinlength / 3) - viewportWindow.getX(), y - (pinlength / 6) - viewportWindow.getY(),
                          x - (pinlength / 3) - viewportWindow.getX(), y - viewportWindow.getY());
            line.paint(g2,false);
            break;
        case EAST:
            line.set(x - viewportWindow.getX(), y - viewportWindow.getY(), x + (pinlength / 3) - viewportWindow.getX(),
                          y - (pinlength / 6) - viewportWindow.getY());
            line.paint(g2,false);
            line.set(x + (pinlength / 3) - viewportWindow.getX(), y - (pinlength / 6) - viewportWindow.getY(),
                          x + (pinlength / 3) - viewportWindow.getX(), y - viewportWindow.getY());
            line.paint(g2,false);
            break;
        }

    }    
    public void drawTriState(Graphics2D g2, ViewportWindow viewportWindow, AffineTransform scale){
        double pinlength = Utilities.PIN_LENGTH *scale.getScaleX();    
        Segment line=new Segment();
        double x=this.segment.ps.x*scale.getScaleX();
        double y=this.segment.ps.y*scale.getScaleX();
        switch (this.orientation) {
          case EAST:
              line.set(x - viewportWindow.getX(), y - pinlength / 6 - viewportWindow.getY(),
                      x - pinlength / 6 - viewportWindow.getX(), y - viewportWindow.getY());
              line.paint(g2,false);
              line.set(x - viewportWindow.getX(), y + pinlength / 6 - viewportWindow.getY(),
                      x - pinlength / 6 - viewportWindow.getX(), y - viewportWindow.getY());
              line.paint(g2,false);
              break;
          case WEST:
              line.set(x - viewportWindow.getX(), y - pinlength / 6 - viewportWindow.getY(),
                      x + pinlength / 6 - viewportWindow.getX(), y - viewportWindow.getY());
              line.paint(g2,false);
              line.set(x - viewportWindow.getX(), y + pinlength / 6 - viewportWindow.getY(),
                      x + pinlength / 6 - viewportWindow.getX(), y - viewportWindow.getY());
              line.paint(g2,false);
              break;
          case NORTH:
              line.set(x - pinlength / 6 - viewportWindow.getX(), y - viewportWindow.getY(), x - viewportWindow.getX(),
                      y + pinlength / 6 - viewportWindow.getY());
              line.paint(g2,false);
              line.set(x + pinlength / 6 - viewportWindow.getX(), y - viewportWindow.getY(), x - viewportWindow.getX(),
                      y + pinlength / 6 - viewportWindow.getY());
              line.paint(g2,false);
              break;
          case SOUTH:
              line.set(x - pinlength / 6 - viewportWindow.getX(), y - viewportWindow.getY(), x - viewportWindow.getX(),
                      y - pinlength / 6 - viewportWindow.getY());
              line.paint(g2,false);
              line.set(x + pinlength / 6 - viewportWindow.getX(), y - viewportWindow.getY(), x - viewportWindow.getX(),
                      y - pinlength / 6 - viewportWindow.getY());
              line.paint(g2,false);
              break;                            
        }
    }    
    public void drawInverted(Graphics2D g2, ViewportWindow viewportWindow, AffineTransform scale){
        double invertCircleRadios = (Utilities.PIN_LENGTH / 6);
        Circle circle=new Circle(new Point(this.segment.ps.x,this.segment.ps.y),invertCircleRadios);
        switch (this.orientation) {
          case EAST:
              circle.move(invertCircleRadios,0);
              break;
          case WEST:
              circle.move(-invertCircleRadios,0);
              break;
          case NORTH:
              circle.move(0,-invertCircleRadios);
              break;
          case SOUTH:
              circle.move(0,invertCircleRadios);
              break;                            
        }
            circle.scale(scale.getScaleX());
            circle.move(-viewportWindow.getX(),- viewportWindow.getY());
            circle.paint(g2,false);       
   }
   private void drawPinLine(Graphics2D g2, ViewportWindow viewportWindow, AffineTransform scale, double offset) { 
        Segment line=this.segment.clone();            
            
        switch (this.orientation) {
        case SOUTH:
            line.ps.set(line.ps.x,line.ps.y+offset);
            break;
        case NORTH:
            line.ps.set(line.ps.x,line.ps.y-offset);
            break;
        case WEST:
            line.ps.set(line.ps.x-offset,line.ps.y);  
            break;
        case EAST:
            line.ps.set(line.ps.x+offset,line.ps.y);        
            break;
        }
            line.scale(scale.getScaleX());
        line.move(-viewportWindow.getX(),- viewportWindow.getY());
        
        line.paint(g2,false);
    }
    @Override
    public Point getPinPoint() {        
        return segment.ps;
    }
    @Override
    public Texture getClickedTexture(int x, int y) {
        if(this.type==PinType.SIMPLE){
            return null;
        }
        if(this.name.isClicked(x, y))
        return this.name;
        else if(this.number.isClicked(x, y))
        return this.number;
        else
        return null;     
    
    }

    @Override
    public boolean isClickedTexture(int x, int y) {
        return this.getClickedTexture(x, y)!=null;
    }
    @Override
    public Texture getTextureByTag(String tag) {
        if(tag.equals(number.getTag()))
            return number;
        else if(tag.equals(name.getTag()))
            return name;
        else
        return null;
    }
    @Override
    public String toXML() {
        StringBuffer sb = new StringBuffer();
        sb.append("<pin type=\"" + this.type.ordinal() + "\"  style=\"" + this.style.ordinal() + "\"   x=\""+Utilities.roundDouble(this.segment.ps.x,1)+"\" y=\""+Utilities.roundDouble(this.segment.ps.y,1)+"\" orientation=\""+this.orientation.ordinal()+"\">\r\n");    
        if(this.type == PinType.COMPLEX){
         if (!this.number.isEmpty())
            sb.append("<number>" +
                  this.number.toXML() +
             "</number>\r\n");
        if (!this.name.isEmpty())
            sb.append("<name>" +
                  this.name.toXML() +
             "</name>\r\n");     
        }
        sb.append("</pin>\r\n");
        return sb.toString();
    }

    @Override
    public void fromXML(Node node) {
        Element element = (Element) node;
        style = Style.values()[element.getAttribute("style") == "" ? 0 : (Byte.parseByte(element.getAttribute("style")))];
        type = PinType.values()[(Byte.parseByte(element.getAttribute("type")))];
        Node a = element.getElementsByTagName("a").item(0);
        if(a!=null){
          StringTokenizer st = new StringTokenizer(a.getTextContent(), ",");
          this.segment.ps.set(Double.parseDouble(st.nextToken()),Double.parseDouble(st.nextToken()));
          st.nextToken();
          this.init(Orientation.values()[Byte.parseByte(st.nextToken())]);
        }else{
          this.segment.ps.set(Double.parseDouble(element.getAttribute("x")),Double.parseDouble(element.getAttribute("y")));   
          this.init(Orientation.values()[Byte.parseByte(element.getAttribute("orientation"))]);
        }
        
        node = element.getElementsByTagName("name").item(0);
        if (node != null) {
            name.fromXML(node);
        } else {
            name.setText("");
            name.getAnchorPoint().set(this.segment.ps.x,this.segment.ps.y);
        }
        node = element.getElementsByTagName("number").item(0);

        if (node != null) {
            number.fromXML(node);
        } else {
            number.setText("");
            number.getAnchorPoint().set(this.segment.ps.x,this.segment.ps.y);
        }
    }    
    @Override
    public AbstractMemento getState(MementoType operationType) {
        AbstractMemento memento = new Memento(operationType);
        memento.saveStateFrom(this);
        return memento;
    }
    
    public static class Memento extends AbstractMemento<Symbol, Pin> {
        private double psx,psy;

        private double pex,pey;

        private int orientation;

        private int style;

        private int type;
        
        private Texture.Memento name,number;

        public Memento(MementoType mementoType) {
            super(mementoType);
            number=new SymbolFontTexture.Memento();
            name=new SymbolFontTexture.Memento();    
        }

        public void loadStateTo(Pin shape) {
            super.loadStateTo(shape);
            shape.type = PinType.values()[this.type];
            shape.style = Style.values()[this.style];
            shape.orientation = Pinable.Orientation.values()[orientation];
            number.loadStateTo(shape.number);
            name.loadStateTo(shape.name);            
            shape.segment.set(psx, psy, pex, pey);
        }

        public void saveStateFrom(Pin shape) {
            super.saveStateFrom(shape);
            this.name.saveStateFrom(shape.name);
            this.number.saveStateFrom(shape.number);
            this.type = shape.type.ordinal();
            this.style = shape.style.ordinal();
            this.orientation = shape.orientation.ordinal();
            this.psx = shape.segment.ps.x;
            this.psy = shape.segment.ps.y;            
            this.pex = shape.segment.pe.x;
            this.pey = shape.segment.pe.y;            
            
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof Memento)) {
                return false;
            }
            Memento other = (Memento) obj;
            return super.equals(obj) && 
            this.name.equals(other.name) && this.number.equals(other.number)&& other.type == this.type && other.style == this.style &&other.orientation == this.orientation &&
            Utils.EQ(this.psx, other.psx)&&Utils.EQ(this.psy, other.psy)&&Utils.EQ(this.pex, other.pex)&&Utils.EQ(this.pey, other.pey);
        }

        @Override
        public int hashCode() {
            int hash = super.hashCode();
            hash += this.name.hashCode()+this.number.hashCode()+this.type+this.style+this.orientation+Double.hashCode(psx)+Double.hashCode(psy)+Double.hashCode(pex)+Double.hashCode(pey);                                
            return hash;
        }

        @Override 
        public void clear() {
            super.clear();                                    
        }
        @Override
        public boolean isSameState(Unit unit) {
            Pin pin = (Pin) unit.getShape(getUUID());
            return (pin.getState(getMementoType()).equals(this));
        }
    }    
    
    
}
