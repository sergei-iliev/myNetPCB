package com.mynetpcb.core.board;

import com.mynetpcb.core.capi.shape.Shape;

import java.util.Collection;
import java.util.Objects;
import java.util.UUID;


public interface Net {
    
    public default String getNetName(){
        return null;
    }
    
    public default void setNetName(String net){
        
    }
    /*
     * Return all shapes that are over/within proximity
     */
    public default Collection<Shape> getNetShapes(Collection<UUID> selected){        
       return null; 
    }
    
    public default boolean isSameNet(Net source){
        if(Objects.equals(source.getNetName(), this.getNetName())&&(!("".equals(this.getNetName())))&&(!(null==(this.getNetName())))){
            return true;
        }
        return false;
    }
}
