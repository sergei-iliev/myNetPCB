package com.mynetpcb.core.capi.event;


import java.awt.event.MouseEvent;

import com.mynetpcb.d2.shapes.Point;

public class MouseScaledEvent{
        
    private double x;

    private double y;

    private final MouseEvent event;
    
        public MouseScaledEvent(MouseEvent event,Point basePoint) {
          this.event=event;
          x=basePoint.x;
          y=basePoint.y;
        }
        
        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }
        
        public Point getPoint(){
          return new Point(x,y);  
        }
        
        public int getWindowX(){
          return event.getX();  
        }
        
        public int getWindowY(){
          return event.getY();  
        }
        
        public MouseEvent getMouseEvent(){
        	return event;
        }
        
        
        @Override
        public String toString(){
          StringBuilder sb=new StringBuilder();
          sb.append("base x="+x);
          sb.append("; base y="+y);
          sb.append("; window x="+getWindowX());
          sb.append("; window y="+getWindowY());      
          return sb.toString();
        }
}

