package com.mynetpcb.core.pad;


public interface Net {
    
    public default String getNetName(){
        return null;
    }
    
    public default void setNetName(String net){
        
    }
    
}
