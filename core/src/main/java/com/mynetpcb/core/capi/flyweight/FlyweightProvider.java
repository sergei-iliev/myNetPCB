package com.mynetpcb.core.capi.flyweight;

import java.awt.Shape;

import java.util.List;

public abstract class FlyweightProvider<S extends Shape> {
   
   protected byte index;
    
   protected  List<S> pool;
    
   public S getShape(){
      index++;
      if(index>getMaxPoolSize()){
          throw new IllegalStateException("Pool index bigger then the number of objects in the pool.");  
      }            
      return pool.get(index-1);
   }
   
   
   protected  byte getMaxPoolSize(){
       return 2;
   }
   
   public void reset(){
     index=0;  
   }
}
