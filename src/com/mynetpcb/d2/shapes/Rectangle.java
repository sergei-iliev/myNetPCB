package com.mynetpcb.d2.shapes;

import java.awt.Graphics2D;
import com.mynetpcb.d2.shapes.Polygon;


public class Rectangle extends Polygon {
    private double x,y,width,height;
    public Rectangle(double x,double y,double width,double height) {
      this.x=x;
      this.y=y;
      this.width=width;
      this.height=height;
    }
    

    @Override
    public Rectangle clone() {
        Rectangle copy=new Rectangle(0,0,0,0);

        this.points.forEach(point->{
            copy.points.add(point.clone());
        });  
        return copy;
    }

    public void setRect(double x,double y,double width,double height){                                                
              this.points.get(0).set(x,y);                        
              this.points.get(1).set(x+width,y);
              this.points.get(2).set(x+width,y+height);
              this.points.get(3).set(x,y+height);                                                                       
    }
    public void reset(double width,double height){
//            let pc=this.box.center;                 
//            this.points=[];
//            this.points.push(new d2.Point(pc.x-(width/2),pc.y-(height/2)));     //topleft point
//            this.points.push(new d2.Point(pc.x+(width/2),pc.y-(height/2)));
//            this.points.push(new d2.Point(pc.x+(width/2),pc.y+(height/2)));
//            this.points.push(new d2.Point(pc.x-(width/2),pc.y+(height/2)));                                         
    }
    
    @Override
    public void rotate(double angle, Point center) {
        // TODO Implement this method

    }

    @Override
    public void rotate(double angle) {
        // TODO Implement this method
    }
    
    @Override
    public void paint(Graphics2D g2, boolean fill) {
        super.paint(g2, fill);
    }

}
