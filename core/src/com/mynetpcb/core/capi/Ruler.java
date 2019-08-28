package com.mynetpcb.core.capi;

import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.text.Text;
import com.mynetpcb.core.capi.text.font.FontTexture;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;

public class Ruler extends Shape implements Resizeable{
    
    private Point resizingPoint;
    //private ChipText text;
    private FontTexture text;
    
    public Ruler() {
        super(0,0,0,0,0,0);
        text=new FontTexture("label","Label",0,0,Text.Alignment.LEFT,Grid.MM_TO_COORD(1.2));        
        text.setFillColor(Color.WHITE);
        text.setStyle(Text.Style.BOLD);
        text.setSelectionRectWidth(3000);
    }

    @Override
    public void Paint(Graphics2D g2, ViewportWindow viewportWindow, AffineTransform scale, int layermask) {
        if(resizingPoint==null){
            return;
        }
        text.setText(String.valueOf(Grid.COORD_TO_MM((int)resizingPoint.distance(getX(),getY())))+" "+Grid.Units.MM);
        text.Paint(g2, viewportWindow, scale, layermask);
        
        GeneralPath line= new GeneralPath(GeneralPath.WIND_EVEN_ODD,2);
      
        Point2D tmp=new Point2D.Double(); 
        scale.transform(new Point(getX(),getY()),tmp);        
        line.moveTo((float)tmp.getX()-viewportWindow.x,(float)tmp.getY()-viewportWindow.y);
        
        scale.transform(resizingPoint,tmp);
        line.lineTo((float)tmp.getX()-viewportWindow.x,(float)tmp.getY()-viewportWindow.y);
        
        g2.setStroke(new BasicStroke(1));    
        g2.setColor(Color.WHITE);
        
        g2.draw(line); 
    }

    @Override
    public Point isControlRectClicked(int x, int y) {
        return null;
    }

    @Override
    public Point getResizingPoint() {
        return resizingPoint;
    }

    @Override
    public void setResizingPoint(Point point) {
        resizingPoint=point;
    }

    @Override
    public void Resize(int xOffset, int yOffset, Point clickedPoint) {
       resizingPoint.setLocation(resizingPoint.x+xOffset,resizingPoint.y+yOffset);
       text.setLocation(resizingPoint.x, resizingPoint.y);
    }

    @Override
    public void drawControlShape(Graphics2D g2, ViewportWindow viewportWindow, AffineTransform scale) {
        

    }

    @Override
    public void alignResizingPointToGrid(Point targetPoint) {
        
    }
}
