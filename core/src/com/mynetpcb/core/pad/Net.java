package com.mynetpcb.core.pad;

import java.util.Objects;


public interface Net {
    
    public default String getNetName(){
        return null;
    }
    
    public default void setNetName(String net){
        
    }
    
    public default boolean isSameNet(Net source,Net target){
        if(Objects.equals(source.getNetName(), target.getNetName())&&(!("".equals(target.getNetName())))&&(!(null==(target.getNetName())))){
            return true;
        }
        return false;
    }
}
