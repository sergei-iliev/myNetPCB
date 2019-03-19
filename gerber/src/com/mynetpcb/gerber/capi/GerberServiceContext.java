package com.mynetpcb.gerber.capi;

import java.util.HashMap;
import java.util.Map;

public class GerberServiceContext {
    public static final int FOOTPRINT_REFERENCE_ON_SILKSCREEN=1;
    public static final int FOOTPRINT_VALUE_ON_SILKSCREEN=2;
    public static final int FOOTPRINT_SHAPES_ON_SILKSCREEN=3;    
    
    //parameter bag
    private final Map<Integer, Object> parameters;
    
    public GerberServiceContext() {
        parameters = new HashMap<Integer, Object>(5);
        parameters.put(GerberServiceContext.FOOTPRINT_REFERENCE_ON_SILKSCREEN,false);
        parameters.put(GerberServiceContext.FOOTPRINT_VALUE_ON_SILKSCREEN,false);
        parameters.put(GerberServiceContext.FOOTPRINT_SHAPES_ON_SILKSCREEN,false);        
    }
    
    public <T> T getParameter(Integer key, Class<T> clazz) {
        return clazz.cast(parameters.get(key));
    }

    public <T> T getParameter(Integer key, Class<T> clazz, T defaultValue) {
        T value = clazz.cast(parameters.get(key));
        if (value == null) {
            return defaultValue;
        } else {
            return value;
        }
    }

    public void setParameter(Integer key, Object value) {
        parameters.put(key, value);
    }
}
