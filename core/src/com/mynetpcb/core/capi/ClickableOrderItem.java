package com.mynetpcb.core.capi;


public final class ClickableOrderItem{
      private final int elementIndex; 
      private final long orderWeight;
      private final int layermask;
      public ClickableOrderItem(int elementIndex,long orderWeight,int layermask){
          this.elementIndex=elementIndex;
          this.orderWeight=orderWeight;
          this.layermask = layermask;
      }                            
      
      public int getElementIndex(){
          return elementIndex;
      }
      
      public long getOrderWeight(){
         return orderWeight;  
      }
      
      public int getLayerMaskID(){
          return layermask;
      }
}


