package com.mynetpcb.core.capi.io;


public interface Commandable<V> {
    public  V execute();
    
    public  void cancel(); 
}
