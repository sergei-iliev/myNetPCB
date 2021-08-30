package com.mynetpcb.core.capi.event;

public interface Event<T> {
    //item
    public static final int SELECT_SHAPE=1;
    public static final int DELETE_SHAPE=2;
    public static final int RENAME_SHAPE=3;
    public static final int ADD_SHAPE=4;

    //unit
    public static final int ADD_UNIT=1;
    public static final int DELETE_UNIT=2;
    public static final int SELECT_UNIT=3;  
    public static final int RENAME_UNIT=4;

    //container
    public static final int SELECT_CONTAINER=1;
    public static final int RENAME_CONTAINER=2;
    public static final int DELETE_CONTAINER=3;
    
    public static final int PROPERTY_CHANGE=5;
    
    public T getObject();
    
    public int getEventType();
}
