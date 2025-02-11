package com.mynetpcb.circuit.shape;

import com.mynetpcb.circuit.unit.Circuit;
import com.mynetpcb.core.capi.Externalizable;
import com.mynetpcb.core.capi.ViewportWindow;
import com.mynetpcb.core.capi.layer.Layer;
import com.mynetpcb.core.capi.line.LinePoint;
import com.mynetpcb.core.capi.print.PrintContext;
import com.mynetpcb.core.capi.shape.AbstractLine;
import com.mynetpcb.core.capi.text.Textable;
import com.mynetpcb.core.capi.text.Texture;
import com.mynetpcb.core.capi.text.Texture.Alignment;
import com.mynetpcb.core.capi.text.Texture.Orientation;
import com.mynetpcb.core.capi.text.font.SymbolFontTexture;
import com.mynetpcb.core.capi.undo.AbstractMemento;
import com.mynetpcb.core.capi.undo.MementoType;
import com.mynetpcb.core.capi.unit.Unit;
import com.mynetpcb.core.utils.Utilities;
import com.mynetpcb.d2.shapes.Box;
import com.mynetpcb.d2.shapes.Circle;
import com.mynetpcb.d2.shapes.Line;
import com.mynetpcb.d2.shapes.Point;
import com.mynetpcb.d2.shapes.Polyline;
import com.mynetpcb.d2.shapes.Utils;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import java.util.Arrays;
import java.util.StringTokenizer;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class SCHBusPin extends AbstractLine implements Textable,Externalizable{
    private SymbolFontTexture texture;
    
    public SCHBusPin() {
        super(1,Layer.LAYER_ALL);  
        this.fillColor=Color.BLACK;
        //this.selectionRectWidth=2;
        this.polyline.points.add(new LinePoint(0, 0));
        this.polyline.points.add(new LinePoint(-8, -8));
        this.texture=new SymbolFontTexture("???","name",4,0,Texture.Alignment.LEFT.ordinal(),8,Font.PLAIN);
    }
    public SCHBusPin clone() throws CloneNotSupportedException{
        SCHBusPin copy=(SCHBusPin)super.clone();
        copy.texture =this.texture.clone();  
        copy.polyline=this.polyline.clone();
        return copy;
    }
    @Override
    public void alignResizingPointToGrid(Point targetPoint) {
        this.getOwningUnit().getGrid().snapToGrid(targetPoint);         
    }       
    @Override
    public Point alignToGrid(boolean isRequired) {
       Point point=polyline.points.get(1); 
       Point p=getOwningUnit().getGrid().positionOnGrid(point.x,point.y);        
        
       texture.move(p.x-point.x,p.y-point.y);  
        point.set(p);
        
       point=polyline.points.get(0); 
       p=getOwningUnit().getGrid().positionOnGrid(point.x,point.y);        
       point.set(p);
       
       return null;       
    }
    @Override
    public Box getBoundingShape() {        
        return this.polyline.box();
    }


    @Override
    public Texture getClickedTexture(double x, double y) {
        if(this.texture.isClicked(x, y))
            return this.texture;        
        else
            return null;
    }

    @Override
    public boolean isClickedTexture(double x, double y) {
        return this.getClickedTexture(x, y)!=null;
    }
    @Override
    public Texture getTextureByTag(String tag) {
        return this.texture;
    }
    @Override
    public void setSelected(boolean selection) {        
        super.setSelected(selection);           
        this.texture.setSelected(selection);
    }
    @Override
    public Point isControlRectClicked(double x, double y,ViewportWindow viewportWindow) {
        Point pt=new Point(x,y);
		pt.scale(getOwningUnit().getScalableTransformation().getCurrentTransformation().getScaleX());
		pt.move(-viewportWindow.getX(),- viewportWindow.getY());
		 
        
		var tmp=this.polyline.points.get(1).clone();
        tmp.scale(getOwningUnit().getScalableTransformation().getCurrentTransformation().getScaleX());
        tmp.move(-viewportWindow.getX(),- viewportWindow.getY());

        if(Utils.LE(pt.distanceTo(tmp),selectionRectWidth/2)){
              return this.polyline.points.get(1);
        }
        return null;
    }
    @Override
    public void move(double xoffset, double yoffset) {        
        super.move(xoffset,yoffset);
        this.texture.move(xoffset,yoffset);
    }
    
    @Override
    public void mirror(Line line) {        
        super.mirror(line);
        this.texture.setMirror(line);
    }
    
    @Override
    public void rotate(double angle, Point origin) {
        
        super.rotate(angle,origin);
        this.texture.setRotation(angle, origin);
        
    }
    
    @Override
    public void resize(double xoffset, double yoffset, Point clickedPoint) {
        clickedPoint.set(clickedPoint.x + xoffset,
                                                                clickedPoint.y + yoffset);
    }
    
    @Override
    public void paint(Graphics2D g2, ViewportWindow viewportWindow, AffineTransform scale, int layersmask) {
        
                Box rect = this.polyline.box();
                rect.scale(scale.getScaleX());           
                if (!this.isFloating()&& (!rect.intersects(viewportWindow))) {
                        return;
                }

                                              
                g2.setStroke(new BasicStroke((float)(this.thickness * scale.getScaleX()),BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));  
                g2.setColor(isSelected()?Color.GRAY:fillColor);


                Polyline a=this.polyline.clone();
                a.scale(scale.getScaleX());
                a.move( - viewportWindow.getX(), - viewportWindow.getY());                
                a.paint(g2,false);

                
        this.texture.paint(g2, viewportWindow, scale,layersmask);
                  
        if (this.isSelected()) {
            Circle c=new Circle(this.polyline.points.get(0).clone(), 2);
            c.scale(scale.getScaleX());
            c.move(-viewportWindow.getX(),- viewportWindow.getY());
            c.paint(g2,false);        
        
            
            Utilities.drawCrosshair(g2,null,2,(Point)a.points.get(1));
        }
        
        
                    
     }   
    @Override
    public void print(Graphics2D g2, PrintContext printContext, int layermask) {
        g2.setStroke(new BasicStroke((float)(this.thickness ),BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));  
        g2.setColor(isSelected()?Color.GRAY:fillColor);

        this.polyline.paint(g2,false);        
        this.texture.print(g2, printContext,layermask);        
    }
    @Override
    public AbstractMemento getState(MementoType operationType) {
        AbstractMemento memento = new Memento(operationType);
        memento.saveStateFrom(this);
        return memento;
    }

    @Override
    public String toXML() {
        StringBuffer xml=new StringBuffer();
        xml.append("<buspin>\r\n");
        xml.append("<name>"+texture.toXML()+"</name>\r\n");
        xml.append("<wirepoints>");
        xml.append(Utilities.roundDouble(getLinePoints().get(0).x,1)+","+Utilities.roundDouble(getLinePoints().get(0).y,1)+"|");
        xml.append(Utilities.roundDouble(getLinePoints().get(1).x,1)+","+Utilities.roundDouble(getLinePoints().get(1).y,1)+"|");
        xml.append("</wirepoints>\r\n");       
        xml.append("</buspin>\r\n");
        return xml.toString();
    }

    @Override
    public void fromXML(Node node) throws XPathExpressionException, ParserConfigurationException {
        Element element=(Element)node;
        Node n=element.getElementsByTagName("name").item(0);
        texture.fromXML(n); 

        NodeList nodelist = element.getElementsByTagName("wirepoints");
        n = nodelist.item(0);
        StringTokenizer st=new StringTokenizer(Utilities.trimCRLF(n.getTextContent()),"|");         
        Point point = new Point();
        StringTokenizer stock=new StringTokenizer(st.nextToken(),",");
        point.set(Double.parseDouble(stock.nextToken()),Double.parseDouble(stock.nextToken())); 

        getLinePoints().get(0).set(point);
        stock=new StringTokenizer(st.nextToken(),",");
        point.set(Double.parseDouble(stock.nextToken()),Double.parseDouble(stock.nextToken())); 
        getLinePoints().get(1).set(point);  

    }

    public static class Memento extends AbstractMemento<Circuit, SCHBusPin> {

        private double Ax[];

        private double Ay[];
        
        Texture.Memento textureMemento;
        
        public Memento(MementoType mementoType) {
            super(mementoType);
            textureMemento=new SymbolFontTexture.Memento();

        }

        @Override
        public void loadStateTo(SCHBusPin shape) {
            super.loadStateTo(shape);
            shape.polyline.points.clear();
            for (int i = 0; i < Ax.length; i++) {
                shape.add(new Point(Ax[i], Ay[i]));
            }
            //***reset floating start point
            if (shape.polyline.points.size() > 0) {
                shape.floatingStartPoint.set((Point)shape.polyline.points.get(shape.polyline.points.size() - 1));
                shape.reset();
            }
            textureMemento.loadStateTo(shape.texture);  
        }

        @Override
        public void saveStateFrom(SCHBusPin shape) {
            super.saveStateFrom(shape);
            Ax = new double[shape.polyline.points.size()];
            Ay = new double[shape.polyline.points.size()];
            for (int i = 0; i < shape.polyline.points.size(); i++) {
                Ax[i] = ((Point)shape.polyline.points.get(i)).x;
                Ay[i] = ((Point)shape.polyline.points.get(i)).y;
            }
            
            textureMemento.saveStateFrom(shape.texture);
        }

        @Override
        public void clear() {
            super.clear();
            Ax = null;
            Ay = null;
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
            return (super.equals(obj)&&textureMemento.equals(other.textureMemento)&&
                    Arrays.equals(Ax, other.Ax) && Arrays.equals(Ay, other.Ay));

        }

        @Override
        public int hashCode() {
            int  hash = super.hashCode()+textureMemento.hashCode();
            hash += Arrays.hashCode(Ax);
            hash += Arrays.hashCode(Ay);
            return hash;
        }
        @Override
        public boolean isSameState(Unit unit) {
            SCHBusPin line = (SCHBusPin) unit.getShape(getUUID());
            return (line.getState(getMementoType()).equals(this));
        }
    }    
}
