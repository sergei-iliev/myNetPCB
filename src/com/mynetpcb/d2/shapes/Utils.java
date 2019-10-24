package com.mynetpcb.d2.shapes;


import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

public class Utils{
    public static final double DP_TOL = 0.000001;
    
    public static double radians(double degrees) {
        return degrees * Math.PI / 180;
    }
    
    public static double degrees (double radians) {
        return radians * 180 / Math.PI;
    }
    /*****
    *
    *   Intersect Line with Line
    *
    *****/
    public static boolean intersectLineLine (Point a1,Point a2,Point b1,Point b2) {
        boolean result=false;
        
        double ua_t = (b2.x - b1.x) * (a1.y - b1.y) - (b2.y - b1.y) * (a1.x - b1.x);
        double ub_t = (a2.x - a1.x) * (a1.y - b1.y) - (a2.y - a1.y) * (a1.x - b1.x);
        double u_b  = (b2.y - b1.y) * (a2.x - a1.x) - (b2.x - b1.x) * (a2.y - a1.y);

        if ( u_b != 0 ) {
            double ua = ua_t / u_b;
            double ub = ub_t / u_b;

            if ( 0 <= ua && ua <= 1 && 0 <= ub && ub <= 1 ) {
                result = true;
            } else {
                result = false;
            }
        }
        return result;
    };
    /*****
    *
    *   Intersect Line with Rectangle
    *
    *****/
    public static boolean intersectLineRectangle(Point a1,Point a2,Point topRight,Point bottomLeft) {
        //var min        = Min(r1,r2);
        //var max        = Max(r1,r2);
        //var topRight   = new d2.Point( max.x, min.y );
        //var bottomLeft = new d2.Point( min.x, max.y );
        
        //boolean inter1 = intersectLineLine(min, topRight, a1, a2);
        //boolean inter2 = intersectLineLine(topRight, max, a1, a2);
        //boolean inter3 = intersectLineLine(max, bottomLeft, a1, a2);
        //boolean inter4 = intersectLineLine(bottomLeft, min, a1, a2);
        
        return intersectLineRectangle(a1, a2, topRight, bottomLeft);
    };
    public static void drawCrosshair(Graphics2D g2,int length, Point... points) {

        Line2D line = new Line2D.Double();

        g2.setStroke(new BasicStroke(1));

        for (Point point : points) {
            g2.setColor(Color.BLUE);
            line.setLine(point.x - length, point.y, point.x + length, point.y);
            g2.draw(line);

            line.setLine(point.x, point.y - length, point.x, point.y + length);
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
