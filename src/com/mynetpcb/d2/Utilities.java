package com.mynetpcb.d2;

import com.mynetpcb.d2.shapes.Point;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

public class Utilities{
    public static final double DP_TOL = 0.000001;
    
    public static double radians(double degrees) {
        return degrees * Math.PI / 180;
    }
    
    public static double degrees (double radians) {
        return radians * 180 / Math.PI;
    }
    
    public static void drawCrosshair(Graphics2D g2,int length, Point... points) {

        Line2D line = new Line2D.Double();

        g2.setStroke(new BasicStroke(1));

        for (Point point : points) {
            g2.setColor(Color.BLUE);
            line.setLine(point.getX() - length, point.getY(), point.getX() + length, point.getY());
            g2.draw(line);

            line.setLine(point.getX(), point.getY() - length, point.getX(), point.getY() + length);
            g2.draw(line);
        }
    }
    
    public static boolean GT(double x,double y) {
         return ( (x)-(y) >  DP_TOL );
    }
    
    public static boolean GE(double x,double y){
         return ( (x)-(y) > -DP_TOL );
    }
    public static boolean EQ(double x,double y) {
         return ( (x)-(y) <  DP_TOL && (x)-(y) > -DP_TOL );
    }
    public static boolean LT(double x,double y){
         return ( (x)-(y) < -DP_TOL );
    }
    public static boolean LE(double x,double y){
         return ( (x)-(y) <  DP_TOL );
    }
   
}
