package com.mynetpcb.core.capi;

import com.mynetpcb.core.capi.shape.Shape;
import com.mynetpcb.core.capi.text.font.FontTexture;
import com.mynetpcb.core.utils.Utilities;
import com.mynetpcb.d2.shapes.Line;
import com.mynetpcb.d2.shapes.Point;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

public class Ruler extends Shape implements Resizeable{
    
    private Point resizingPoint;

    private FontTexture text;
    
    private Line line;
    
    private double x,y;
    
    public Ruler() {
        super(0,0);
        text=new FontTexture("label","0",0,0,(int)Grid.MM_TO_COORD(1.2));        
        text.setFillColor(Color.WHITE);
        line=new Line(0,0,0,0);
        //text.setStyle(Font.BOLD);
        //text.setSelectionRectWidth(3000);
    }

    @Override
    public void paint(Graphics2D g2, ViewportWindow viewportWindow, AffineTransform scale, int layermask) {
        if(resizingPoint==null){
            return;
        }
        
        text.setText(String.valueOf(Grid.COORD_TO_MM(Utilities.roundDouble(resizingPoint.distanceTo(x,y))))+" "+Grid.Units.MM);
        text.paint(g2, viewportWindow, scale, layermask);
        
        line.setLine(x, y, resizingPoint.x, resizingPoint.y);
        line.scale(scale.getScaleX());
        line.move(-viewportWindow.getX(),- viewportWindow.getY());
       
        g2.setStroke(new BasicStroke(1));    
        g2.setColor(Color.WHITE);
        
        line.paint(g2,true); 
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
    public void resize(int xoffset, int yoffset, Point clickedPoint) {
       resizingPoint.set(resizingPoint.x+xoffset,resizingPoint.y+yoffset);
       text.set(resizingPoint.x,resizingPoint.y);
    }

    @Override
    public void alignResizingPointToGrid(Point targetPoint) {
        
    }
    
    @Override
    public void setLocation(double x, double y) {        
        this.x=x;
        this.y=y;
    }
}
