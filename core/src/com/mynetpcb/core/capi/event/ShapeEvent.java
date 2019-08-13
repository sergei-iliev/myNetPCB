package com.mynetpcb.core.capi.event;

import com.mynetpcb.core.capi.shape.Shape;


public class ShapeEvent implements Event<Shape>{
    private final int eventType;
    
    private final Shape o;  //***reference depends on the Message type
    

    public ShapeEvent(Shape o,int eventType) {
      this.o=o;   
      this.eventType=eventType;
    }    
    
    public Shape getObject(){
      return o;  
    }
    
    public int getEventType(){
      return this.eventType;  
    }
}
