package com.mynetpcb.d2.shapes;

import java.awt.Graphics2D;

import java.util.LinkedList;
import java.util.List;


public class RoundRectangle extends Rectangle {
    public int rounding;
    private Segment[] segments={new Segment(),new Segment(),new Segment(),new Segment()};
    private Arc[] arcs={new Arc(),new Arc(),new Arc(),new Arc()};

    public RoundRectangle(double x, double y, double width, double height, int rounding) {
        super(x, y, width, height);
        this.rounding = rounding;
        this.reset();
    }
    
    public void setRect(double x,double y,double width,double height,int rounding){
            super.setRect(x,y,width,height);
            this.rounding=rounding;
            this.reset();
    }  
    
    @Override
    public RoundRectangle clone() {
        RoundRectangle copy=new RoundRectangle(0,0,0,0,this.rounding);
        
        copy.points.get(0).set(this.points.get(0));
        copy.points.get(1).set(this.points.get(1));
        copy.points.get(2).set(this.points.get(2));
        copy.points.get(3).set(this.points.get(3));        
        copy.reset();
        return copy;
               
    }

//    /**
//     * Create specific rounding arc 90 degrees long
//     * @param center of the arc
//     * @param start angle point
//     * @param end angle point
//     */
//    private Arc createArc(Point center, Point start, Point end) {
//        double startAngle = 360 - (new Vector(center, start)).slope();
//        double endAngle = (new Vector(center, end)).slope();
//        if (Utils.EQ(startAngle, endAngle)) {
//            endAngle = 360;
//        }
//        double r = (new Vector(center, start)).length();
//        return new Arc(center, r, startAngle, 90);
//    }
    private void resetArc(Arc arc,Point center, Point start, Point end) {
        double startAngle = 360 - (new Vector(center, start)).slope();
        double endAngle = (new Vector(center, end)).slope();
        if (Utils.EQ(startAngle, endAngle)) {
            endAngle = 360;
        }
        double r = (new Vector(center, start)).length();
        arc.pc=center;
        arc.r=r;
        arc.startAngle=startAngle;
        arc.endAngle=90;
        //return new Arc(center, r, startAngle, 90);
    }

    /**
     *
     * @param {Point} p1 corner point
     * @param {Point} p2 left point
     * @param {Point} p3 right point
     * @returns {array of arc points[center,start point,end point]}
     */
    private Point[] findArcPoints(Point p1, Point p2, Point p3) {
        //start angle point
        Vector v = new Vector(p1, p2);
        Vector norm = v.normalize();
        double x = p1.x + this.rounding * norm.x;
        double y = p1.y + this.rounding * norm.y;
        Point A = new Point(x, y);


        //end angle point
        v = new Vector(p1, p3);
        norm = v.normalize();
        x = p1.x + this.rounding * norm.x;
        y = p1.y + this.rounding * norm.y;
        Point B = new Point(x, y);

        //center
        v = new Vector(p1, B);
        x = A.x + v.x;
        y = A.y + v.y;
        Point C = new Point(x, y);

        return new Point[] { C, A, B };
    }
    public void scale(double alpha){
            super.scale(alpha);
            this.rounding*=alpha;
            this.reset();
    }
    public void setRounding(int rounding){
     this.rounding=rounding;
     this.reset();
    }    
    
    public void move(double offX,double offY){
       super.move(offX,offY);
       this.reset();
    }
    public void mirror(Line line){
            super.mirror(line);
            Point p=this.points.get(0).clone();
            this.points.get(0).set(this.points.get(1));
            this.points.get(1).set(p);
            
            p=this.points.get(2).clone();
            this.points.get(2).set(this.points.get(3));
            this.points.get(3).set(p);
            
            this.reset();
    }  
    public boolean contains(Point pt){
       if(!super.contains(pt)){
               return false;
       }               
//       
//       //constrauct polygon
//       let pol=new d2.Polygon();
//       this.segments.forEach(segment=>{
//             pol.add(segment.ps);
//             pol.add(segment.pe);
//       });
//       
//       return pol.contains(pt);
       return true;
    }
    
    public List<Point> vertices(){
        
       List<Point> vertices=new LinkedList<Point>();
       Point p=this.segments[0].ps;
                
       for(Segment segment:this.segments){
           double a=p.distanceTo(segment.ps);
           double b=p.distanceTo(segment.pe);
           if(a<b){
             vertices.add(segment.ps);
             vertices.add(segment.pe);
             p=segment.pe;  
           }else{
             vertices.add(segment.pe);
             vertices.add(segment.ps);                        
             p=segment.ps;  
           }
           
        }
        return vertices;
                
    }
    @Override
    public void resize(int offX,int offY,Point point){
            super.resize(offX,offY,point);
            this.reset();
    }    
    private void reset() {

        if (this.rounding == 0) {
 
            this.segments[0].set(this.points.get(0), this.points.get(1));
            this.segments[1].set(this.points.get(1), this.points.get(2));
            this.segments[2].set(this.points.get(2), this.points.get(3));
            this.segments[3].set(this.points.get(3), this.points.get(0));

        } else {
            //rect
            Segment top = this.segments[0];
            Segment right = this.segments[1];    
            Segment bottom = this.segments[2];
            Segment left =this.segments[3];
 

            //arcs
            Point[] r = this.findArcPoints(this.points.get(0), this.points.get(1), this.points.get(3));
            resetArc(this.arcs[0],r[0], r[1], r[2]);
            top.ps = r[1].clone();
            left.ps = r[2].clone();

            r = this.findArcPoints(this.points.get(1), this.points.get(2), this.points.get(0));
            resetArc(this.arcs[1],r[0], r[1], r[2]);
            top.pe = r[2].clone();
            right.ps = r[1].clone();

            r = this.findArcPoints(this.points.get(2), this.points.get(3), this.points.get(1));
            resetArc(this.arcs[2] ,r[0], r[1], r[2]);
            right.pe = r[2].clone();
            bottom.ps = r[1].clone();


            r = this.findArcPoints(this.points.get(3), this.points.get(0), this.points.get(2));
            resetArc(this.arcs[3],r[0], r[1], r[2]);
            bottom.pe = r[2].clone();
            left.pe = r[1].clone();
        }


    }

    @Override
    public void paint(Graphics2D g2, boolean fill) {
        if(fill){
            List<Point> vertices=this.vertices();
            polygon.reset();
            
            polygon.moveTo(vertices.get(0).x,vertices.get(0).y);

            for (int i = 1; i < vertices.size(); i++) {
               polygon.lineTo(vertices.get(i).x, vertices.get(i).y);
            }
            polygon.closePath();                        
            g2.fill(polygon);             
            
            if(rounding!=0){
                for (Arc arc : this.arcs) {
                   Circle circle=new Circle(arc.pc,arc.r);
                   circle.paint(g2, true); 
                }
            }                        
        }else{

            for (Segment segment : this.segments) {
                segment.paint(g2, fill);
            }
            if(rounding!=0){
                for (Arc arc : this.arcs) {
                  arc.paint(g2, fill);
                }
            }
        }
    }

    @Override
    public void rotate(double angle, Point center) {
        super.rotate(angle,center);
        this.reset(); 
    }

    @Override
    public void rotate(double angle) {
        this.rotate(angle,new Point(0,0));
    }
}
