package com.mynetpcb.core.capi.shape;

import com.mynetpcb.core.capi.text.Textable;
import com.mynetpcb.core.capi.text.Texture;

/*
 * Single line of text component blueprint
 */

public interface Label extends Textable{
  
   public  Texture getTexture();
   
    /*
     * Get texture under coordinate (x,y)
     */
   public default Texture getClickedTexture(int x,int y){
       return null;
   }
    
   public default boolean isClickedTexture(int x,int y){
       return false;
   }
}
