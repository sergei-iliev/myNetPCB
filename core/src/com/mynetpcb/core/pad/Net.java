package com.mynetpcb.core.pad;

import java.util.Objects;


public interface Net {
    
    public default String getNetName(){
        return null;
    }
    
    public default void setNetName(String net){
        
    }
    
    public default boolean isSameNet(Net source){
        if(Objects.equals(source.getNetName(), this.getNetName())&&(!("".equals(this.getNetName())))&&(!(null==(this.getNetName())))){
            return true;
        }
        return false;
    }
}
