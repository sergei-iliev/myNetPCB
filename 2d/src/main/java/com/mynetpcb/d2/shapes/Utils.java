package com.mynetpcb.d2.shapes;


import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;

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
    *   Intersect Line with Rectangle
    *
    *****/
    public static boolean intersectLineRectangle(Point a1,Point a2,Point min,Point max) {
        
        Point topRight   = new Point( max.x, min.y );
        Point bottomLeft = new Point( min.x, max.y );
        
        boolean inter1 = intersectLineLine(min, topRight, a1, a2);
        boolean inter2 = intersectLineLine(topRight, max, a1, a2);
        boolean inter3 = intersectLineLine(max, bottomLeft, a1, a2);
        boolean inter4 = intersectLineLine(bottomLeft, min, a1, a2);
        
        return inter1||inter2||inter3||inter4;
    };    
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
    /*
     * to the upper left corner of the screen
     */
   	public Point min(Point p1,Point p2){
   		return new Point(Math.min(p1.x,p2.x),Math.min(p1.y,p2.y));	
   	}
   	/*
   	 * to the bottom right corner of the screen
   	 */
   	public Point max(Point p1,Point p2){
   	    return new Point(Math.max(p1.x,p2.x),Math.max(p1.y,p2.y));	
   	}
    
//    public static boolean intersectLine2Circle(Line line,Circle circle) {
//        let ip = [];
//        Point prj = circle.pc.projectionOn(line);            // projection of circle center on line
//        let dist = circle.pc.distanceTo(prj)[0];           // distance from circle center to projection
//
//        if (Flatten.Utils.EQ(dist, circle.r)) {            // line tangent to circle - return single intersection point
//            ip.push(prj);
//        } else if (Flatten.Utils.LT(dist, circle.r)) {       // return two intersection points
//            let delta = Math.sqrt(circle.r * circle.r - dist * dist);
//            let v_trans, pt;
//
//            v_trans = line.norm.rotate90CCW().multiply(delta);
//            pt = prj.translate(v_trans);
//            ip.push(pt);
//
//            v_trans = line.norm.rotate90CW().multiply(delta);
//            pt = prj.translate(v_trans);
//            ip.push(pt);
//        }
//        return ip;
//    }
//    public static void drawCrosshair(Graphics2D g2,int length, Point... points) {
//
//        Line2D line = new Line2D.Double();
//
//        g2.setStroke(new BasicStroke(1));
//
//        for (Point point : points) {
//            g2.setColor(Color.BLUE);
//            line.setLine(point.x - length, point.y, point.x + length, point.y);
//            g2.draw(line);
//
//            line.setLine(point.x, point.y - length, point.x, point.y + length);
//            g2.draw(line);
//        }
//    }
    
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
