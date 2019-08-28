package com.mynetpcb.core.capi.event;


import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseEvent;

public class MouseScaledEvent extends MouseEvent{
        
    private int x;

    private int y;

        public MouseScaledEvent(MouseEvent event,Point basePoint) {
          super((Component)event.getSource(),event.getID(),event.getWhen(),event.getModifiers(),event.getX(),event.getY(),event.getClickCount(),event.isPopupTrigger(),event.getButton());
          x=basePoint.x;
          y=basePoint.y;
        }
        
        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }
        
        public Point getPoint(){
          return new Point(x,y);  
        }
        
        public int getWindowX(){
          return super.getX();  
        }
        
        public int getWindowY(){
          return super.getY();  
        }
        
        public Point getWindowPoint(){
          return super.getPoint();  
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

