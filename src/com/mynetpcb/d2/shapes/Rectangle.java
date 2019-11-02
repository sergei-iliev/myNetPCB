package com.mynetpcb.d2.shapes;

import java.awt.Graphics2D;


public class Rectangle extends Polygon {

    public Rectangle(double x,double y,double width,double height) {        
        this.points.add(new Point(x,y));                        
        this.points.add(new Point(x+width,y));
        this.points.add(new Point(x+width,y+height));
        this.points.add(new Point(x,y+height));      
    }
    

    @Override
    public Rectangle clone() {
        Rectangle copy=new Rectangle(0,0,0,0);
        copy.points.clear();
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
    public void resize(int offX,int offY,Point point){
            if(point==this.points.get(2)){
    //do same
                    Point pt=this.points.get(2);
                    pt.move(offX,offY);
    //do left 
                    Vector v1=new Vector(this.points.get(0),pt);
                    Vector v2=new Vector(this.points.get(0),this.points.get(1));
    
                    Vector v=v1.projectionOn(v2);
    //translate point
                    this.points.get(1).x=this.points.get(0).x +v.x;
                    this.points.get(1).y=this.points.get(0).y + v.y;
    
    //do right 
                    v2=new Vector(this.points.get(0),this.points.get(3));
    
                    v=v1.projectionOn(v2);
    //translate point
                    this.points.get(3).x=this.points.get(0).x +v.x;
                    this.points.get(3).y=this.points.get(0).y + v.y;
    
    
            }else if(point==this.points.get(1)){
            //do same
                    Point pt=this.points.get(1);
                    pt.move(offX,offY);

                    //do left 
                    Vector v1=new Vector(this.points.get(3),pt);
                    Vector v2=new Vector(this.points.get(3),this.points.get(0));
    
                    Vector v=v1.projectionOn(v2);
    //translate point
                    this.points.get(0).x=this.points.get(3).x +v.x;
                    this.points.get(0).y=this.points.get(3).y + v.y;
    //do right 
                    v2=new Vector(this.points.get(3),this.points.get(2));
    
                    v=v1.projectionOn(v2);
    //translate point
                    this.points.get(2).x=this.points.get(3).x +v.x;
                    this.points.get(2).y=this.points.get(3).y + v.y;                                
            }else if(point==this.points.get(3)){
            //do same
                    Point pt=this.points.get(3);
                    pt.move(offX,offY);             
                    
            //do left 
                    Vector v1=new Vector(this.points.get(1),pt);
                    Vector v2=new Vector(this.points.get(1),this.points.get(0));
    
                    Vector v=v1.projectionOn(v2);
    //translate point
                    this.points.get(0).x=this.points.get(1).x +v.x;
                    this.points.get(0).y=this.points.get(1).y + v.y;
                    
            //do right 
                    v2=new Vector(this.points.get(1),this.points.get(2));
    
                    v=v1.projectionOn(v2);
    //translate point
                    this.points.get(2).x=this.points.get(1).x +v.x;
                    this.points.get(2).y=this.points.get(1).y + v.y;
            }else{
            //do same
                    Point pt=this.points.get(0);
                    pt.move(offX,offY);             
                    
            //do left 
                    Vector v1=new Vector(this.points.get(2),pt);
                    Vector v2=new Vector(this.points.get(2),this.points.get(1));
    
                    Vector v=v1.projectionOn(v2);
    //translate point
                    this.points.get(1).x=this.points.get(2).x +v.x;
                    this.points.get(1).y=this.points.get(2).y + v.y;
                    
            //do right 
                    v2=new Vector(this.points.get(2),this.points.get(3));
    
                    v=v1.projectionOn(v2);
    //translate point
                    this.points.get(3).x=this.points.get(2).x +v.x;
                    this.points.get(3).y=this.points.get(2).y + v.y;                                
            }
    
    }    
    public void reset(double width,double height){
//            let pc=this.box.center;                 
//            this.points=[];
//            this.points.push(new d2.Point(pc.x-(width/2),pc.y-(height/2)));     //topleft point
//            this.points.push(new d2.Point(pc.x+(width/2),pc.y-(height/2)));
//            this.points.push(new d2.Point(pc.x+(width/2),pc.y+(height/2)));
//            this.points.push(new d2.Point(pc.x-(width/2),pc.y+(height/2)));                                         
    }
    
    public double area(){
            return (this.points.get(0).distanceTo(this.points.get(1)))*(this.points.get(1).distanceTo(this.points.get(2)));
    }
    @Override
    public void paint(Graphics2D g2, boolean fill) {
        super.paint(g2, fill);
    }

}
